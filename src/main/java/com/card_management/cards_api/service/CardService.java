package com.card_management.cards_api.service;

import com.card_management.application.configuration.AppConfig;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.exception.BlockedCardException;
import com.card_management.cards_api.exception.DuplicateCardException;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.cards_api.specification.CardSpecifications;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.util.CardEncryptor;
import com.card_management.transaction_api.enumeration.SortDirection;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final UserRepository userRepository;

    private final AppConfig appConfig;

    public CardEnvelopDto getCards(int page, int size, String sort) {
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var cardPage = cardRepository.findAll(pageRequest);
        var cardDto = cardPage.stream()
                .map(cardMapper::map)
                .toList();
        return new CardEnvelopDto(
                cardDto,
                cardPage.getTotalElements(),
                cardPage.getTotalPages()
        );
    }

    public CardDto findById(Long id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + id + " не найдена"));
        return cardMapper.map(card);
    }

    public CardDto create(CardCreateDto cardDto) {
        userRepository.findById(cardDto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + cardDto.getOwnerId()
                        + " не найден"));
        checkDuplicateCard(cardDto.getCardNumber());
        var card = cardMapper.map(cardDto);
        var salt = KeyGenerators.string().generateKey();
        var coder = new CardEncryptor(appConfig.getPassword(), salt);
        var encryptedNumber = coder.encryptCardNumber(cardDto.getCardNumber());
        card.setEncryptedCardNumber(encryptedNumber);
        card.setSaltNumberCard(salt);
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public void delete(Long id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + id + " не найдена"));
        cardRepository.delete(card);
    }

    public CardEnvelopDto getFilteredCards(CardFilterDto cardFilterDto) {
        if (cardFilterDto.getOwnerUuid() != null) {
            if (!userRepository.existsByUuid(UUID.fromString(cardFilterDto.getOwnerUuid()))) {
                throw new ResourceNotFoundException("Пользователь с UUID " + cardFilterDto.getOwnerUuid()
                        + " не зарегистрирован в системе");
            }
        }
        if (cardFilterDto.getPage() == null) {
            cardFilterDto.setPage(1);
        }
        if (cardFilterDto.getSize() == null) {
            cardFilterDto.setSize(5);
        }
        if (cardFilterDto.getSortBy() == null || cardFilterDto.getSortBy().isEmpty()) {
            cardFilterDto.setSortBy("balance");
        }
        if (cardFilterDto.getSortDirection() == null || cardFilterDto.getSortDirection().isEmpty()) {
            cardFilterDto.setSortDirection("DESC");
        }
        var direction = SortDirection.valueOf(cardFilterDto.getSortDirection().toUpperCase());
        Sort sort = direction == SortDirection.DESC
                ? Sort.by(cardFilterDto.getSortBy()).descending()
                : Sort.by(cardFilterDto.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(
                cardFilterDto.getPage() - 1,
                cardFilterDto.getSize(),
                sort
        );
        var cardPage = cardRepository.findAll(
                CardSpecifications.withFilter(cardFilterDto), pageable
        );
        var cardDtoList = cardPage.stream()
                .map(cardMapper::map)
                .toList();
        return new CardEnvelopDto(
                cardDtoList,
                cardPage.getTotalElements(),
                cardPage.getTotalPages()
        );
    }

    private void checkDuplicateCard(String numberCard) {
        var duplicateMaybe = cardRepository.findByMaskedNumber(numberCard);
        if (duplicateMaybe != null) {
            var fullNumberDuplicateCard = decryptCardNumber(duplicateMaybe);
            if (fullNumberDuplicateCard.equals(numberCard)) {
                throw new DuplicateCardException("Карта зарегистрирована в системе. Номер: "
                        + duplicateMaybe.getMaskNumber());
            }
        }
    }

    public List<CardDto> getUserCards(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + userId + " не найден"));
        var listCards = cardRepository.findByOwnerId(userId);
        return listCards.stream()
                .map(cardMapper::map)
                .toList();
    }

    public Card findMatchByNumberCard(String numberCard, Long userId) {
        return cardRepository.findByOwnerId(userId)
                .stream()
                .filter(card -> decryptCardNumber(card).equals(numberCard))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Карта с номером " + numberCard
                + " не принадлежит пользователю с ID " + userId));
    }

    private String decryptCardNumber(Card card) {
        var decoder = new CardEncryptor(appConfig.getPassword(), card.getSaltNumberCard());
        return decoder.decryptCardNumber(card.getEncryptedCardNumber());
    }

    public void checkCardStatus(Card card) {
        if(!card.getStatus().equals(CardStatus.ACTIVE)) {
            throw new BlockedCardException("Операция не может быть совершена. " + card.getStatus().getDescription()
                    + " Номер карты: " + card.getMaskNumber());
        }
    }

    public String getCardLastFourDigits(Card card) {
        var maskedNumberCard = card.getMaskNumber();
        if (maskedNumberCard == null) {
            throw new RuntimeException("Отсутствует номер карты");
        }
        return maskedNumberCard.substring(maskedNumberCard.length() - 4);
    }
}

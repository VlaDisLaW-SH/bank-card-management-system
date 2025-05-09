package com.card_management.cards_api.service;

import com.card_management.application.configuration.AppConfig;
import com.card_management.cards_api.dto.*;
import com.card_management.cards_api.enumeration.CardSortFields;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.exception.BlockedCardException;
import com.card_management.cards_api.exception.DuplicateCardException;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.cards_api.specification.CardSpecifications;
import com.card_management.technical.enumeration.FieldEnumerable;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.util.factory.CardEncryptorFactory;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.users_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.card_management.technical.util.PaginationUtils.createPageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final UserRepository userRepository;

    private final UserService userService;

    private final AppConfig appConfig;

    private final CardEncryptorFactory cardEncryptorFactory;

    public CardEnvelopDto getCards(int page, int size, String sort) {
        if (!FieldEnumerable.containsField(CardSortFields.class, sort)) {
            throw new FieldsValidationException("Недопустимое поле сортировки: " + sort);
        }
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
        var encryptor = cardEncryptorFactory.create(appConfig.getPassword(), salt);
        var encryptedNumber = encryptor.encryptCardNumber(cardDto.getCardNumber());
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

    public void setCardStatus(Long userId, String cardLastFourDigits, String status) {
        var userCard = cardRepository.findByOwnerId(userId)
                .stream()
                .filter(card -> getCardLastFourDigits(card).equals(cardLastFourDigits))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Карта с последними четырьмя цифрами "
                        + cardLastFourDigits + " не найдена."));
        userCard.setStatus(CardStatus.valueOf(status));
        cardRepository.save(userCard);
    }

    public CardEnvelopDto filterCardsForAdmin(CardAdminFilterDto adminFilterDto) {
        if (adminFilterDto.getOwnerId() == null) {
            return filterCards(adminFilterDto.getCardFilterDto(), null);
        }
        var user = userService.findById(adminFilterDto.getOwnerId());
        return filterCards(adminFilterDto.getCardFilterDto(), user.getId());
    }

    public CardEnvelopDto filterCards(CardFilterDto filterDto, Long ownerId) {
        var pageable = createPageable(
                filterDto,
                CardFilterDto::getSortBy,
                CardFilterDto::getSortDirection,
                CardFilterDto::getPage,
                CardFilterDto::getSize,
                "balance",
                "DESC"
        );
        var cardPage = cardRepository.findAll(
                CardSpecifications.withFilter(filterDto, ownerId), pageable
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

    public void setBlockedStatusForCard(Long userId, String cardLastFourDigits) {
        setCardStatus(userId, cardLastFourDigits, "BLOCKED");
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
        var decoder = cardEncryptorFactory.create(appConfig.getPassword(), card.getSaltNumberCard());
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

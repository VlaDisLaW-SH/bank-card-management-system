package com.card_management.cards_api.service;

import com.card_management.application.configuration.AppConfig;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.exception.BlockedCardException;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.util.CardEncryptor;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.util.List;

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
        var card = cardMapper.map(cardDto);
        var salt = KeyGenerators.string().generateKey();
        var coder = new CardEncryptor(appConfig.getPassword(), salt);
        var coderNumber = coder.encryptCardNumber(cardDto.getCardNumber());
        card.setEncryptedCardNumber(coderNumber);
        card.setSaltNumberCard(salt);
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public void delete(Long id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + id + " не найдена"));
        cardRepository.delete(card);
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
        Card cardSought = null;
        var listCards = cardRepository.findByOwnerId(userId);
        for (Card card: listCards) {
            if (decryptCardNumber(card).equals(numberCard)) {
                cardSought = card;
            }
        }
        if (cardSought == null) {
            throw new ResourceNotFoundException("Карта с номером " + numberCard
                    + " не принадлежит пользователю с ID " + userId);
        }
        return cardSought;
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
}

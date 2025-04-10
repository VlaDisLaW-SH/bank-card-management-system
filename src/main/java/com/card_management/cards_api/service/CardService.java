package com.card_management.cards_api.service;

import com.card_management.application.configuration.AppConfig;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.dto.CardNumberDto;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.util.CardEncryptor;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final UserRepository userRepository;

    private final CardEncryptor cardEncryptor;

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
        var card = cardMapper.map(cardDto);
        var encryptedCardNumber = cardEncryptor.encryptCardNumber(cardDto.getCardNumber());
        card.setEncryptedCardNumber(encryptedCardNumber);
        var salt = appConfig.getSaltCard();
        card.setSaltNumberCard(salt);
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public void delete(Long id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + id + " не найдена"));
        cardRepository.delete(card);
    }

    public CardEnvelopDto getUserCards(Long userId, int page, int size, String sort) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + userId + " не найден"));
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var cardPage = cardRepository.findByOwnerId(userId, pageRequest);
        var cardDto = cardPage.stream()
                .map(cardMapper::map)
                .toList();
        return new CardEnvelopDto(
                cardDto,
                cardPage.getTotalElements(),
                cardPage.getTotalPages()
        );
    }

    public CardNumberDto getNumberCard(Long cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + cardId + " не найдена"));
        var decryptedCardNumber = cardEncryptor.decryptCardNumber(card.getEncryptedCardNumber());
        return cardMapper.map(decryptedCardNumber);
    }
}

package com.card_management.cards_api.service;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.technical.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

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
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public void delete(Long id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + id + " не найдена"));
        cardRepository.delete(card);
    }
}

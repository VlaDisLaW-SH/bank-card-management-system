package com.card_management.controllers;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.service.CardService;
import com.card_management.technical.exception.CustomValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardsController {

    private final CardService cardService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CardEnvelopDto> getCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        CardEnvelopDto cardEnvelopDto = cardService.getCards(page, size, sort);
        return ResponseEntity.ok(cardEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CardDto> findById(@PathVariable Long id) {
        var card = cardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CardDto> create(
            @Valid @RequestBody CardCreateDto cardData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        var card = cardService.create(cardData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(card);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable Long id) {
        cardService.delete(id);
    }

    @GetMapping(path = "/userCards/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CardDto>> getUserCards(@PathVariable Long userId) {
        List<CardDto> cardDtoList = cardService.getUserCards(userId);
        return ResponseEntity.ok(cardDtoList);
    }
}

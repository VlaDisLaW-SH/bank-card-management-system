package com.card_management.controllers;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.service.CardService;
import com.card_management.controllers.common.CardValidator;
import com.card_management.technical.exception.CustomValidationException;
import com.card_management.users_api.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardsController {

    private final CardService cardService;

    private final CardValidator cardValidator;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> findById(@PathVariable Long id) {
        var card = cardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> create(
            @Valid @RequestBody CardCreateDto cardData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        cardValidator.validateCreateCard(cardData);
        var card = cardService.create(cardData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(card);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@PathVariable Long id) {
        cardService.delete(id);
    }

    @GetMapping(path = "/userCards/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDto>> getUserCards(@PathVariable Long userId) {
        List<CardDto> cardDtoList = cardService.getUserCards(userId);
        return ResponseEntity.ok(cardDtoList);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDto>> getMyCards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CardDto> cardDtoList = cardService.getUserCards(userDetails.getId());
        return ResponseEntity.ok(cardDtoList);
    }

    @PostMapping(path = "/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CardEnvelopDto> filterCards(
            @Valid @RequestBody CardFilterDto filterDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        cardValidator.validateFilterCard(filterDto);
        var cards = cardService.getFilteredCards(filterDto);
        return ResponseEntity.ok(cards);
    }
}

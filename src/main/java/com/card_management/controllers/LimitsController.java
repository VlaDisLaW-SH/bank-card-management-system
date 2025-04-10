package com.card_management.controllers;

import com.card_management.limits_api.dto.BalancesByLimitDto;
import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.dto.LimitEnvelopDto;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.CustomValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/limits")
@RequiredArgsConstructor
public class LimitsController {

    private final LimitService limitService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LimitEnvelopDto> getLimits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        LimitEnvelopDto limitEnvelopDto = limitService.getLimits(page, size, sort);
        return ResponseEntity.ok(limitEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LimitDto> findById(@PathVariable Long id) {
        var limit = limitService.findById(id);
        return ResponseEntity.ok(limit);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LimitDto> create(
            @Valid @RequestBody LimitCreateDto limitData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        var limit = limitService.create(limitData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(limit);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable Long id) {
        limitService.delete(id);
    }

    @GetMapping(path = "/userLimit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<LimitDto>> getUserLimit(@PathVariable Long userId) {
        var limitsList = limitService.getUserLimits(userId);
        return ResponseEntity.ok(limitsList);
    }

    @GetMapping(path = "/balancesByLimit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BalancesByLimitDto> getBalancesByLimit(@PathVariable Long id) {
        var balancesByLimit = limitService.getBalancesByLimit(id);
        return ResponseEntity.ok(balancesByLimit);
    }
}

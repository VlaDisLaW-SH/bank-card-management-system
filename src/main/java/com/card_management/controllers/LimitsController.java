package com.card_management.controllers;

import com.card_management.controllers.common.LimitValidator;
import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.dto.LimitEnvelopDto;
import com.card_management.limits_api.service.LimitService;
import com.card_management.users_api.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/limits")
@RequiredArgsConstructor
public class LimitsController {

    private final LimitService limitService;

    private final LimitValidator limitValidator;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LimitDto> findById(@PathVariable Long id) {
        var limit = limitService.findById(id);
        return ResponseEntity.ok(limit);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LimitDto> create(@Valid @RequestBody LimitCreateDto limitData) {
        limitValidator.validateCreateLimit(limitData);
        var limit = limitService.create(limitData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(limit);
    }

    @PostMapping(path = "/setLimit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> setLimit(@Valid @RequestBody LimitCreateDto limitData) {
        limitValidator.validateCreateLimit(limitData);
        var message = limitService.setLimit(limitData);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(message);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@PathVariable Long id) {
        limitService.delete(id);
    }

    @GetMapping(path = "/userLimit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LimitDto>> getUserLimit(@PathVariable Long userId) {
        var limitsList = limitService.getUserLimits(userId);
        return ResponseEntity.ok(limitsList);
    }

    @GetMapping(path = "/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LimitDto>> getLimitsByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        var limitsList = limitService.getUserLimits(userDetails.getId());
        return ResponseEntity.ok(limitsList);
    }
}

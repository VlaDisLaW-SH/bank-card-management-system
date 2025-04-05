package com.card_management.controllers;

import com.card_management.technical.exception.CustomValidationException;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.dto.UserEnvelopDto;
import com.card_management.users_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserEnvelopDto> index(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        UserEnvelopDto userEnvelopDto = userService.getUsers(page, size, sort);
        return ResponseEntity.ok(userEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> show(@PathVariable Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> create(
            @Valid @RequestBody UserCreateDto userData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        var user = userService.create(userData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable Long id) {
        userService.delete(id);
    }
}

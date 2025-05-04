package com.card_management.cards_api.service;

import com.card_management.application.configuration.AppConfig;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardEnvelopDto;
import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.exception.DuplicateCardException;
import com.card_management.cards_api.mapper.CardMapper;
import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.repository.CardRepository;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.util.CardEncryptor;
import com.card_management.technical.util.factory.CardEncryptorFactory;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.util.unit_test.CardTestFactory;
import com.card_management.util.unit_test.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppConfig appConfig;

    @Mock
    private CardEncryptorFactory cardEncryptorFactory;

    @Mock
    private CardEncryptor cardEncryptor;

    @Mock
    private CardMapper cardMapper;

    private Card card1;

    private Card card2;

    private User user;

    private CardCreateDto cardCreateDto;

    private CardDto cardDto1;

    private CardDto cardDto2;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.createUser(1L);

        cardCreateDto = new CardCreateDto();
        cardCreateDto.setCardNumber("1234567812341001");
        cardCreateDto.setOwnerId(1L);
        cardCreateDto.setValidityPeriodMonth(12);
        cardCreateDto.setValidityPeriodYear(30);
        cardCreateDto.setStatus("ACTIVE");
        cardCreateDto.setBalance(1000);

        card1 = CardTestFactory.createCard(1L, user);
        cardDto1 = CardTestFactory.createCardDto(1L);

        card2 = CardTestFactory.createCard(2L, user);
        cardDto2 = CardTestFactory.createCardDto(2L);
    }

    @Test
    void getCards_returnsMappedDtoPage_whenCardsExist() {
        var cards = List.of(card1, card2);
        Page<Card> cardPage = new PageImpl<>(
                cards,
                PageRequest.of(0, 2, Sort.by("id")), 2);

        when(cardRepository.findAll(
                PageRequest.of(0, 2, Sort.by("id")))).thenReturn(cardPage);
        when(cardMapper.map(card1)).thenReturn(cardDto1);
        when(cardMapper.map(card2)).thenReturn(cardDto2);

        CardEnvelopDto result = cardService.getCards(1, 2, "id");

        assertEquals(2, result.getCards().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getCards_returnsEmptyResult_whenNoCardsExist() {
        Page<Card> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 2, Sort.by("id")), 0);

        when(cardRepository.findAll(
                PageRequest.of(0, 2, Sort.by("id")))).thenReturn(emptyPage);

        CardEnvelopDto result = cardService.getCards(1, 2, "id");

        assertNotNull(result);
        assertTrue(result.getCards().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void findById_returnsCardDto_whenCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardMapper.map(card1)).thenReturn(cardDto1);

        CardDto result = cardService.findById(1L);

        assertNotNull(result);
        assertEquals(cardDto1.getId(), result.getId());
        assertEquals(cardDto1.getMaskNumber(), result.getMaskNumber());
        assertEquals(cardDto1.getStatus(), result.getStatus());
    }

    @Test
    void findById_throwsResourceNotFoundException_whenCardNotExists() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(
                ResourceNotFoundException.class,
                () -> cardService.findById(99L)
        );
        assertEquals("Карта с ID 99 не найдена", ex.getMessage());
    }

    @Test
    void createCard_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(cardRepository.findByMaskedNumber(any())).thenReturn(null);
        when(cardMapper.map(cardCreateDto)).thenReturn(card1);
        when(appConfig.getPassword()).thenReturn("secret");
        when(cardEncryptorFactory.create(eq("secret"), anyString())).thenReturn(cardEncryptor);
        when(cardEncryptor.encryptCardNumber(cardCreateDto.getCardNumber())).thenReturn(card1.getEncryptedCardNumber());
        when(cardMapper.map(card1)).thenReturn(cardDto1);

        var result = cardService.create(cardCreateDto);

        assertEquals(cardDto1, result);
        verify(cardEncryptor).encryptCardNumber("1234567812341001");
        verify(cardRepository).save(card1);
    }

    @Test
    void createCard_userNotFound_throwsException() {
        var dto = new CardCreateDto();
        dto.setOwnerId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> cardService.create(dto));
        assertEquals("Пользователь с ID 99 не найден", exception.getMessage());

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(cardRepository, cardEncryptorFactory, cardEncryptor);
    }

    @Test
    void createCard_throwsDuplicateCardException_whenCardAlreadyExists() {
        var duplicateCard = new Card();
        duplicateCard.setEncryptedCardNumber("encrypted1234");
        duplicateCard.setSaltNumberCard("test-salt");

        when(userRepository.findById(cardCreateDto.getOwnerId())).thenReturn(Optional.of(new User()));
        when(cardRepository.findByMaskedNumber(anyString())).thenReturn(duplicateCard);
        when(appConfig.getPassword()).thenReturn("secret");
        when(cardEncryptorFactory.create(eq("secret"), eq("test-salt"))).thenReturn(cardEncryptor);
        when(cardEncryptor.decryptCardNumber("encrypted1234")).thenReturn(cardCreateDto.getCardNumber());

        assertThrows(DuplicateCardException.class, () -> cardService.create(cardCreateDto));

        verify(cardEncryptor).decryptCardNumber(duplicateCard.getEncryptedCardNumber());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void delete_deletesCard_whenCardExists() {
        when(cardRepository.findById(card1.getId())).thenReturn(Optional.of(card1));

        cardService.delete(card1.getId());

        verify(cardRepository).findById(card1.getId());
        verify(cardRepository).delete(card1);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenCardDoesNotExist() {
        var cardId = 99L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> cardService.delete(cardId));
        assertEquals("Карта с ID 99 не найдена", exception.getMessage());
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).delete(any(Card.class));
    }

    @Test
    void setCardStatus_successfullyUpdatesStatus() {
        var userId = 1L;
        var lastFourDigits = "1002";
        var newStatus = "BLOCKED";
        when(cardRepository.findByOwnerId(userId)).thenReturn(List.of(card2));
        cardService.setCardStatus(userId, lastFourDigits, newStatus);
        assertEquals(CardStatus.BLOCKED, card2.getStatus());
        verify(cardRepository).save(card2);
    }

    @Test
    void setCardStatus_throwsException_whenCardNotFound() {
        var userId = 1L;
        var lastFourDigits = "9999";
        var newStatus = "BLOCKED";
        when(cardRepository.findByOwnerId(userId)).thenReturn(List.of(card1));
        var exception = assertThrows(ResourceNotFoundException.class, () ->
                cardService.setCardStatus(userId, lastFourDigits, newStatus)
        );
        assertEquals("Карта с последними четырьмя цифрами 9999 не найдена.", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void filterCards_returnsFilteredCardsSuccessfully_withCorrectPaginationAndSorting() {
        var ownerId = 1L;
        var filterDto = new CardFilterDto();
        filterDto.setPage(2);
        filterDto.setSize(5);
        filterDto.setSortBy("balance");
        filterDto.setSortDirection("ASC");
        filterDto.setStatus("ACTIVE");

        Page<Card> mockPage = new PageImpl<>(List.of(card1, card2));

        when(cardRepository.findAll(
                ArgumentMatchers.<Specification<Card>>any(),
                any(Pageable.class))
        ).thenReturn(mockPage);
        when(cardMapper.map(card1)).thenReturn(cardDto1);
        when(cardMapper.map(card2)).thenReturn(cardDto2);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        CardEnvelopDto result = cardService.filterCards(filterDto, ownerId);

        assertNotNull(result);
        assertEquals(2, result.getCards().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(cardRepository).findAll(
                ArgumentMatchers.<Specification<Card>>any(),
                pageableCaptor.capture()
        );
        var usedPageable = pageableCaptor.getValue();
        assertEquals(1, usedPageable.getPageNumber());
        assertEquals(5, usedPageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "balance"), usedPageable.getSort());

        verify(cardMapper, times(2)).map(any(Card.class));
    }

    @Test
    void filterCards_returnsEmptyResult_whenNoCardsMatch() {
        var filterDto = new CardFilterDto();
        filterDto.setPage(1);
        filterDto.setSize(10);
        filterDto.setSortBy("balance");
        filterDto.setSortDirection("DESC");

        when(cardRepository.findAll(
                ArgumentMatchers.<Specification<Card>>any(),
                any(Pageable.class))
        ).thenReturn(new PageImpl<>(List.of()));

        CardEnvelopDto result = cardService.filterCards(filterDto, 99L);

        assertNotNull(result);
        assertTrue(result.getCards().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(cardRepository).findAll(
                ArgumentMatchers.<Specification<Card>>any(),
                any(Pageable.class)
        );
        verify(cardMapper, never()).map(any(Card.class));
    }

    @Test
    void getUserCards_returnsCardListSuccessfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerId(user.getId())).thenReturn(List.of(card1, card2));

        when(cardMapper.map(card1)).thenReturn(cardDto1);
        when(cardMapper.map(card2)).thenReturn(cardDto2);

        List<CardDto> result = cardService.getUserCards(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(List.of(cardDto1, cardDto2), result);

        verify(userRepository).findById(user.getId());
        verify(cardRepository).findByOwnerId(user.getId());
        verify(cardMapper).map(card1);
        verify(cardMapper).map(card2);
    }

    @Test
    void getUserCards_throwsException_whenUserNotFound() {
        var userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () ->
                cardService.getUserCards(userId)
        );
        assertEquals("Пользователь с ID 99 не найден", exception.getMessage());

        verify(userRepository).findById(userId);
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(cardMapper);
    }

    @Test
    void findMatchByNumberCard_returnsMatchingCardSuccessfully() {
        var targetCardNumber = "1234567812341001";
        var password = "super-secret";
        List<Card> cards = List.of(card1);

        when(cardRepository.findByOwnerId(user.getId())).thenReturn(cards);
        when(appConfig.getPassword()).thenReturn(password);
        when(cardEncryptorFactory.create(password, card1.getSaltNumberCard())).thenReturn(cardEncryptor);
        when(cardEncryptor.decryptCardNumber(card1.getEncryptedCardNumber())).thenReturn(targetCardNumber);

        var result = cardService.findMatchByNumberCard(targetCardNumber, user.getId());

        assertEquals(card1, result);
    }

    @Test
    void findMatchByNumberCard_throwsExceptionIfNoMatchingCardFound() {
        var searchedCardNumber = "1234-xxxx";
        var password = "secret";

        List<Card> cards = List.of(card1);

        when(cardRepository.findByOwnerId(user.getId())).thenReturn(cards);
        when(appConfig.getPassword()).thenReturn(password);
        when(cardEncryptorFactory.create(password, card1.getSaltNumberCard())).thenReturn(cardEncryptor);
        when(cardEncryptor.decryptCardNumber(card1.getEncryptedCardNumber())).thenReturn("1234567812341001");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                cardService.findMatchByNumberCard(searchedCardNumber, user.getId())
        );
        assertEquals("Карта с номером " + searchedCardNumber +
                " не принадлежит пользователю с ID " + user.getId(), ex.getMessage());
    }
}

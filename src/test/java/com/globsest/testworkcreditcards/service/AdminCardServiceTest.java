package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.exceptions.CardOperationException;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminCardServiceTest {

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private AdminCardService adminCardService;

    @Test
    void createCardForUser_shouldSuccess() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardNumberGenerator.generateCardNumber()).thenReturn("1234567890123456");
        when(cardNumberGenerator.encryptCardNumber(anyString())).thenReturn("encrypted");
        when(cardNumberGenerator.maskCardNumber(anyString())).thenReturn("**** **** **** 3456");
        when(bankCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        BankCard result = adminCardService.createCardForUser(userId);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals("encrypted", result.getCard_number_encrypted());
        assertEquals("**** **** **** 3456", result.getCard_number_masked());
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void createCardForUser_shouldThrowWhenUserBlocked() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(CardOperationException.class, () -> adminCardService.createCardForUser(userId));
    }

    @Test
    void updateStatusCardUser_shouldBlockCard() throws ChangeSetPersister.NotFoundException {
        Long cardId = 1L;
        BankCard card = new BankCard();
        card.setStatus(CardStatus.ACTIVE);

        when(bankCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(bankCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BankCard result = adminCardService.updateStatusCardUser(cardId, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, result.getStatus());
    }

    @Test
    void updateStatusCardUser_shouldThrowWhenUserBlocked() {
        Long cardId = 1L;
        BankCard card = new BankCard();
        card.setStatus(CardStatus.BLOCKED);
        User user = new User();
        user.setActive(false);
        card.setUser(user);

        when(bankCardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(CardOperationException.class,
                () -> adminCardService.updateStatusCardUser(cardId, CardStatus.ACTIVE));
    }
}
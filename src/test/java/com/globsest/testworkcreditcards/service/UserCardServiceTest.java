package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.dto.CardTransferDto;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.Transfer;
import com.globsest.testworkcreditcards.entity.TransferStatus;
import com.globsest.testworkcreditcards.exceptions.TransferException;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.TransferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private UserCardService userCardService;

    @Test
    void transferBetweenCards_shouldSuccess() throws Exception {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        BankCard fromCard = new BankCard();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("200.00"));
        fromCard.setStatus(CardStatus.ACTIVE);

        BankCard toCard = new BankCard();
        toCard.setId(2L);
        toCard.setBalance(new BigDecimal("50.00"));
        toCard.setStatus(CardStatus.ACTIVE);

        CardTransferDto transferDto = new CardTransferDto(1L, 2L, amount, "Test transfer");

        when(bankCardRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(fromCard));
        when(bankCardRepository.findByIdAndUserId(2L, userId)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transfer result = userCardService.transferBetweenCards(userId, transferDto);

        assertEquals(new BigDecimal("100.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("150.00"), toCard.getBalance());
        assertEquals(TransferStatus.COMPLETED, result.getStatus());
    }

    @Test
    void transferBetweenCards_shouldThrowWhenSameCard() {
        Long userId = 1L;
        CardTransferDto transferDto = new CardTransferDto(1L, 1L, new BigDecimal("100.00"), "Test");

        assertThrows(EntityNotFoundException.class,
                () -> userCardService.transferBetweenCards(userId, transferDto));
    }

    @Test
    void requestCardBlock_shouldSuccess() {
        Long userId = 1L;
        Long cardId = 1L;

        BankCard card = new BankCard();
        card.setStatus(CardStatus.ACTIVE);

        when(bankCardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));
        when(bankCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BankCard result = userCardService.requestCardBlock(userId, cardId);

        assertEquals(CardStatus.BLOCKED, result.getStatus());
    }
}
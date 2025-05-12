package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.dto.CardTransferDto;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.Transfer;
import com.globsest.testworkcreditcards.entity.TransferStatus;
import com.globsest.testworkcreditcards.exceptions.CardOperationException;
import com.globsest.testworkcreditcards.exceptions.TransferException;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.TransferRepository;
import com.globsest.testworkcreditcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCardService {
    private final BankCardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;

    public BankCard requestCardBlock(Long userId, Long cardId) {
        BankCard card = validateCardOwnership(userId, cardId);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException("Card is already blocked", "CARD_BLOCKED");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardOperationException("Only active cards can be blocked", "INVALID_CARD_STATUS");
        }
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    public Transfer transferBetweenCards(Long userId, CardTransferDto transferDto) throws Exception {
        BankCard fromCard = validateCardOwnership(userId, transferDto.fromCardId());
        BankCard toCard = validateCardOwnership(userId, transferDto.toCardId());

        if (fromCard.getId().equals(toCard.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("Source card is not active", "SOURCE_CARD_INACTIVE");
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("Target card is not active", "TARGET_CARD_INACTIVE");
        }
        if (transferDto.amount() == null) {
            throw new TransferException("Amount cannot be null", "NULL_AMOUNT");
        }
        if (transferDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Amount must be positive", "NON_POSITIVE_AMOUNT");
        }
        if (fromCard.getBalance().compareTo(transferDto.amount()) < 0) {
            throw new TransferException(
                    String.format("Insufficient funds. Available: %s, requested: %s",
                            fromCard.getBalance(), transferDto.amount()),
                    "INSUFFICIENT_FUNDS"
            );
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transferDto.amount()));
        toCard.setBalance(toCard.getBalance().add(transferDto.amount()));

        Transfer transfer = new Transfer();
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(transferDto.amount());
        transfer.setDescription(transferDto.description());
        transfer.setStatus(TransferStatus.COMPLETED);

        cardRepository.saveAll(List.of(fromCard, toCard));
        return transferRepository.save(transfer);
    }

    public Map<String, Object> getUserCardsWithBalances(Long userId) {
        List<Map<String, Object>> cards = cardRepository.findCardBalancesByUserId(userId);
        BigDecimal totalBalance = cardRepository.calculateTotalBalanceByUserId(userId)
                .orElse(BigDecimal.ZERO);

        return Map.of(
                "cards", cards,
                "totalBalance", totalBalance
        );
    }

    public Page<BankCardDto> getUserCards(Long userId, Optional<String> status, Pageable pageable) {
        return status.map(s -> cardRepository.findByUserIdAndStatus(userId, CardStatus.valueOf(s), pageable))
                .orElseGet(() -> cardRepository.findByUserId(userId, pageable))
                .map(BankCardDto::fromEntity);
    }

    private BankCard validateCardOwnership(Long userId, Long cardId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found or access denied"));
    }

}

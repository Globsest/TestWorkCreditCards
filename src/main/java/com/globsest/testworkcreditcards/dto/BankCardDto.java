package com.globsest.testworkcreditcards.dto;

import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;

import java.math.BigDecimal;

public record BankCardDto(
        Long id,
        String cardNumberMasked,
        String cardNumberEncrypted,
        String expirationDate,
        String status,
        BigDecimal balance,
        Long userId

) {
    public static BankCardDto fromEntity(BankCard card) {
        return new BankCardDto(
                card.getId(),
                card.getCard_number_masked(),
                card.getExpiration_date(),
                card.getStatus().name(),
                card.getCard_number_encrypted(),
                card.getBalance(),
                card.getUser().getId()
        );
    }
}

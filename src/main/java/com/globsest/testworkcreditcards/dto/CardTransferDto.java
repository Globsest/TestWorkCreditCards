package com.globsest.testworkcreditcards.dto;

import java.math.BigDecimal;

public record CardTransferDto(Long fromCardId,
                              Long toCardId,
                              BigDecimal amount,
                              String description) {
}

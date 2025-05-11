package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.dto.CardTransferDto;
import com.globsest.testworkcreditcards.entity.Transfer;
import com.globsest.testworkcreditcards.service.AuthenticationService;
import com.globsest.testworkcreditcards.service.UserCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final UserCardService userCardService;
    private final AuthenticationService authenticationService;

    @GetMapping("/allCards")
    public Page<BankCardDto> getUserCards(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = authenticationService.getCurrentUserId();
        return userCardService.getUserCards(userId, Optional.ofNullable(status), pageable);
    }

    @PostMapping("/{cardId}/block")
    public BankCardDto requestCardBlock(@PathVariable Long cardId) {
        Long userId = authenticationService.getCurrentUserId();
        return BankCardDto.fromEntity(userCardService.requestCardBlock(userId, cardId));
    }

    @PostMapping("/transfer")
    public Transfer transferBetweenCards(@RequestBody CardTransferDto transferDto) throws Exception {
        Long userId = authenticationService.getCurrentUserId();
        return userCardService.transferBetweenCards(userId, transferDto);
    }

    @GetMapping("/balances")
    public ResponseEntity<Map<String, Object>> getUserCardsWithBalances() {
        Long userId = authenticationService.getCurrentUserId();
        Map<String, Object> balancesInfo = userCardService.getUserCardsWithBalances(userId);
        return ResponseEntity.ok(balancesInfo);
    }
}

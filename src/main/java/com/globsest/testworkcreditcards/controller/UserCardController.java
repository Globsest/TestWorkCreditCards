package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.dto.CardTransferDto;
import com.globsest.testworkcreditcards.entity.Transfer;
import com.globsest.testworkcreditcards.service.AuthenticationService;
import com.globsest.testworkcreditcards.service.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Мои карты",
            description = "Возвращает карты текущего пользователя",
            parameters = {
                    @Parameter(name = "status", description = "Фильтр по статусу", example = "ACTIVE"),
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "10")
            }
    )
    public Page<BankCardDto> getUserCards(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = authenticationService.getCurrentUserId();
        return userCardService.getUserCards(userId, Optional.ofNullable(status), pageable);
    }

    @PostMapping("/{cardId}/block")
    @Operation(
            summary = "Запрос блокировки карты",
            description = "Позволяет пользователю запросить блокировку своей карты",
            parameters = {
                    @Parameter(name = "cardId", description = "ID карты для блокировки",
                            required = true, example = "123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос на блокировку успешно создан",
                            content = @Content(schema = @Schema(implementation = BankCardDto.class))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public BankCardDto requestCardBlock(@PathVariable Long cardId) {
        Long userId = authenticationService.getCurrentUserId();
        return BankCardDto.fromEntity(userCardService.requestCardBlock(userId, cardId));
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Перевод между картами",
            description = "Переводит средства между картами текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Перевод выполнен"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    public Transfer transferBetweenCards(@RequestBody CardTransferDto transferDto) throws Exception {
        Long userId = authenticationService.getCurrentUserId();
        return userCardService.transferBetweenCards(userId, transferDto);
    }

    @GetMapping("/balances")
    @Operation(
            summary = "Балансы карт",
            description = "Возвращает информацию о балансах всех карт пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о балансах получена",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<Map<String, Object>> getUserCardsWithBalances() {
        Long userId = authenticationService.getCurrentUserId();
        Map<String, Object> balancesInfo = userCardService.getUserCardsWithBalances(userId);
        return ResponseEntity.ok(balancesInfo);
    }
}

package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.dto.CreateCardRequest;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.service.AdminCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final AdminCardService adminCardService;

    @PostMapping("/create")
    @Operation(
            summary = "Создать карту",
            description = "Создает новую карту для указанного пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Карта создана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public BankCard createCard(@RequestBody CreateCardRequest request) {
        return adminCardService.createCardForUser(request.getUserId());
    }


    @PutMapping("/{cardId}/block")

    public BankCard blockCard(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        return adminCardService.updateStatusCardUser(cardId, CardStatus.BLOCKED);
    }
    @Operation(
            summary = "Активация карты",
            description = "Активирует ранее заблокированную карту",
            parameters = {
                    @Parameter(name = "cardId", description = "ID карты", required = true, example = "123", in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Карта успешно активирована",
                            content = @Content(schema = @Schema(implementation = BankCard.class)))
                    ,
                    @ApiResponse(
                            responseCode = "404",
                            description = "Карта не найдена"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен")
            }
    )
    @PutMapping("/{cardId}/activate")
    public BankCard activateCard(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        return adminCardService.updateStatusCardUser(cardId, CardStatus.ACTIVE);
    }

    @DeleteMapping("/{cardId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление карты",
            description = "Полное удаление карты из системы",
            parameters = {
                    @Parameter(name = "cardId", description = "ID карты", required = true, example = "123", in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Карта успешно удалена"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Карта не найдена"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен")
            }
    )
    public void delete(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        adminCardService.deleteCardUser(cardId);
    }

    @GetMapping("allcards")
    @Operation(
            summary = "Получение всех карт",
            description = "Возвращает список всех карт с возможностью фильтрации",
            parameters = {
                    @Parameter(name = "status", description = "Фильтр по статусу карты", example = "ACTIVE"),
                    @Parameter(name = "userId", description = "Фильтр по ID пользователя", example = "1"),
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "20"),
                    @Parameter(name = "sort", description = "Поле сортировки", example = "id,asc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список карт получен",
                            content = @Content(schema = @Schema(implementation = Page.class)))
                    ,
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен")
            }
    )
    public Page<BankCardDto> getAllCards(@RequestParam(required = false) String status, @RequestParam(required = false) Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return adminCardService.getAllCards(pageable);
    }
}

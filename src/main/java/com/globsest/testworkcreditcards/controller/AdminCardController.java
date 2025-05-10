package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.dto.CreateCardRequest;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.service.AdminCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final AdminCardService adminCardService;

    @PostMapping("/create")
    public BankCard createCard(@RequestBody CreateCardRequest request) {
        return adminCardService.createCardForUser(request.getUserId());
    }

    @PutMapping("/{cardId}/block")
    public BankCard blockCard(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        return adminCardService.updateStatusCardUser(cardId, CardStatus.BLOCKED);
    }

    @PutMapping("/{cardId}/activate")
    public BankCard activateCard(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        return adminCardService.updateStatusCardUser(cardId, CardStatus.ACTIVE);
    }

    @DeleteMapping("/{cardId}/delete")
    public void delete(@PathVariable Long cardId) throws ChangeSetPersister.NotFoundException {
        adminCardService.deleteCardUser(cardId);
    }

    @GetMapping("allcards")
    public Page<BankCardDto> getAllCards(@RequestParam(required = false) String status, @RequestParam(required = false) Long userId, @PageableDefault(size = 20) Pageable pageable) {

        return adminCardService.getAllCards(pageable);
    }


}

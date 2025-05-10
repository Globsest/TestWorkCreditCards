package com.globsest.testworkcreditcards.service;


import com.globsest.testworkcreditcards.CardNumberGenerator;
import com.globsest.testworkcreditcards.dto.BankCardDto;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import com.globsest.testworkcreditcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCardService{
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final BankCardRepository bankCardRepository;

    public void deleteCardUser(Long cardId) {
        bankCardRepository.deleteById(cardId);
    }

    public BankCard createCardForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String cardNumber = cardNumberGenerator.generateCardNumber();

        BankCard card = new BankCard();
        card.setUser(user);
        card.setCard_number_encrypted(cardNumberGenerator.encryptCardNumber(cardNumber));
        card.setCard_number_masked(cardNumberGenerator.maskCardNumber(cardNumber));
        card.setExpiration_date(LocalDate.now().plusYears(3)
                .format(DateTimeFormatter.ofPattern("MM/yyyy")));
        card.setStatus(CardStatus.ACTIVE);

        return bankCardRepository.save(card);
    }

    public BankCard updateStatusCardUser(@RequestBody Long cardId, @RequestBody CardStatus cardStatus) throws ChangeSetPersister.NotFoundException {
        BankCard bankCard = bankCardRepository.findById(cardId).orElseThrow(()->new EntityNotFoundException("Card not found"));
        bankCard.setStatus(cardStatus);
        return bankCardRepository.save(bankCard);
    }

    public Page<BankCardDto> getAllCards(Pageable pageable) {
        return bankCardRepository.findAll(pageable)
                .map(BankCardDto::fromEntity);
    }
}

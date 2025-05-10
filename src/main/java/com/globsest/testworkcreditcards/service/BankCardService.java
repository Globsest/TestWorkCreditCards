package com.globsest.testworkcreditcards.service;

import com.globsest.testworkcreditcards.dto.CreateCardRequest;
import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import com.globsest.testworkcreditcards.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
@Service
@RequiredArgsConstructor
@Transactional
public class BankCardService {
    @Value("${encryption.secret.key}")
    private String secretKey;

    private final BankCardRepository cardRepository;

    public BankCard createCard(CreateCardRequest cardRequest) {
        BankCard card = new BankCard();
        card.setCard_number_encrypted(encryptCardNumber(generateCardNumber()));
        card.setCard_number_masked(maskCardNumber(generateCardNumber()));
        card.setExpiration_date(generateExpirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        return cardRepository.save(card);
    }

    public Page <BankCard> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    private String generateCardNumber() {
        Random random = new Random();
        return String.format("%04d%04d%04d%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }

    private String encryptCardNumber(String cardNumber) {
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(cardNumber.getBytes("UTF-8"));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to encrypt card number", ex);
        }
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.substring(0, 4) + " **** **** " + cardNumber.substring(12);
    }

    private String generateExpirationDate() {
        LocalDate date = LocalDate.now().plusYears(3);
        return date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
    }
}

package com.globsest.testworkcreditcards.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Component
public class CardNumberGenerator {
    private final SecureRandom random = new SecureRandom();

    @Value("${encryption.secret.key}")
    private String secretKey;

    public String generateCardNumber() {
        Random random = new Random();
        return String.format("%04d%04d%04d%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }

    public String encryptCardNumber(String cardNumber) {
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

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return "**** **** **** ****";
        }

        String cleanNumber = cardNumber.replaceAll("\\s+", "");

        if (cleanNumber.length() < 4 || !cleanNumber.matches("\\d+")) {
            return "**** **** **** ****";
        }

        String lastFourDigits = cleanNumber.substring(cleanNumber.length() - 4);
        return "**** **** **** " + lastFourDigits;
    }

}
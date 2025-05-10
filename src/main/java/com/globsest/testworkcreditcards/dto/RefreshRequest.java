package com.globsest.testworkcreditcards.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshRequest {
    String refreshToken;
}
package com.globsest.testworkcreditcards.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
@Data
public class CreateCardRequest {
    @NotNull
    private Long userId;
}

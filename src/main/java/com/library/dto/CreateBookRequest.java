package com.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBookRequest(
        @NotNull Long bookId,
        @NotNull @Positive Integer quantity
) {
}

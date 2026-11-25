package com.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IssueBookRequest(
        @NotNull Long userId,
        @NotNull Long bookId,
        @NotNull @Positive Integer quantity
) {
}

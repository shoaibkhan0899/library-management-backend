package com.library.dto;

import com.library.enums.BookRequestStatus;

import java.time.LocalDate;

public record BookRequestResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        Long bookId,
        String bookTitle,
        Integer quantity,
        LocalDate requestedAt,
        BookRequestStatus status
) {
}


package com.library.dto;

import com.library.enums.IssueStatus;

import java.time.LocalDate;

public record BookIssueResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        Long bookId,
        String bookTitle,
        Integer quantity,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate,
        IssueStatus status
) {
}

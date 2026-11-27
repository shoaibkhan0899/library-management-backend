package com.library.controller;

import com.library.dto.BookIssueResponse;
import com.library.dto.BookRequestResponse;
import com.library.dto.CreateBookRequest;
import com.library.entity.BookIssue;
import com.library.entity.BookRequest;
import com.library.service.BookRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookRequestController {

    private final BookRequestService bookRequestService;

    public BookRequestController(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    @PostMapping("/student/book-requests")
    public ResponseEntity<BookRequestResponse> createRequest(
            @Valid @RequestBody CreateBookRequest request,
            Authentication authentication) {
        BookRequest created = bookRequestService.createRequest(
                authentication.getName(),
                request.bookId(),
                request.quantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/student/book-requests/my")
    public ResponseEntity<List<BookRequestResponse>> getMyRequests(
            Authentication authentication) {
        return ResponseEntity.ok(bookRequestService.getMyRequests(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/admin/book-requests")
    public ResponseEntity<List<BookRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(bookRequestService.getAllRequests()
                .stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping("/admin/book-requests/{requestId}/approve")
    public ResponseEntity<BookIssueResponse> approveRequest(@PathVariable Long requestId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toIssueResponse(bookRequestService.approveRequest(requestId)));
    }

    @PostMapping("/admin/book-requests/{requestId}/reject")
    public ResponseEntity<BookRequestResponse> rejectRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(toResponse(bookRequestService.rejectRequest(requestId)));
    }

    private BookRequestResponse toResponse(BookRequest request) {
        return new BookRequestResponse(
                request.getId(),
                request.getUser().getId(),
                request.getUser().getName(),
                request.getUser().getEmail(),
                request.getBook().getId(),
                request.getBook().getTitle(),
                request.getQuantity(),
                request.getRequestedAt(),
                request.getStatus()
        );
    }

    private BookIssueResponse toIssueResponse(BookIssue issue) {
        return new BookIssueResponse(
                issue.getId(),
                issue.getUser().getId(),
                issue.getUser().getName(),
                issue.getBook().getId(),
                issue.getBook().getTitle(),
                issue.getQuantity(),
                issue.getIssueDate(),
                issue.getDueDate(),
                issue.getReturnDate(),
                issue.getStatus()
        );
    }
}

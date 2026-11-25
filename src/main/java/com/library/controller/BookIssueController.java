package com.library.controller;

import com.library.dto.BookIssueResponse;
import com.library.dto.IssueBookRequest;
import com.library.entity.BookIssue;
import com.library.service.BookIssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookIssueController {

    private final BookIssueService bookIssueService;

    public BookIssueController(BookIssueService bookIssueService) {
        this.bookIssueService = bookIssueService;
    }

    @PostMapping("/admin/book-issues")
    public ResponseEntity<BookIssueResponse> issueBook(
            @Valid @RequestBody IssueBookRequest request) {
        BookIssue issue = bookIssueService.issueBook(
                request.userId(),
                request.bookId(),
                request.quantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(issue));
    }

    @PostMapping("/admin/book-issues/{issueId}/return")
    public ResponseEntity<BookIssueResponse> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(toResponse(bookIssueService.returnBook(issueId)));
    }

    @GetMapping("/admin/book-issues")
    public ResponseEntity<List<BookIssueResponse>> getAllIssues() {
        List<BookIssueResponse> issues = bookIssueService.getAllIssues()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/student/book-issues/my")
    public ResponseEntity<List<BookIssueResponse>> getMyIssues(Authentication authentication) {
        List<BookIssueResponse> issues = bookIssueService
                .getMyIssuesByEmail(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(issues);
    }

    private BookIssueResponse toResponse(BookIssue issue) {
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

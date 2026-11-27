package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.BookRequest;
import com.library.entity.User;
import com.library.enums.BookRequestStatus;
import com.library.enums.IssueStatus;
import com.library.enums.Role;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.BookRequestRepository;
import com.library.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookRequestService {

    private final BookRequestRepository bookRequestRepository;
    private final BookIssueRepository bookIssueRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookRequestService(BookRequestRepository bookRequestRepository,
                              BookIssueRepository bookIssueRepository,
                              UserRepository userRepository,
                              BookRepository bookRepository) {
        this.bookRequestRepository = bookRequestRepository;
        this.bookIssueRepository = bookIssueRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookRequest createRequest(String studentEmail, Long bookId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with email: " + studentEmail));
        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only students can request books");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found with ID: " + bookId));
        if (book.getQuantity() == null || book.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient book quantity available");
        }

        BookRequest request = new BookRequest();
        request.setUser(student);
        request.setBook(book);
        request.setQuantity(quantity);
        request.setRequestedAt(LocalDate.now());
        request.setStatus(BookRequestStatus.PENDING);

        return bookRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<BookRequest> getAllRequests() {
        return bookRequestRepository.findAllByOrderByRequestedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<BookRequest> getMyRequests(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with email: " + studentEmail));
        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only students can view their book requests");
        }
        return bookRequestRepository.findAllByUser_IdOrderByRequestedAtDesc(student.getId());
    }

    @Transactional
    public BookIssue approveRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book request not found with ID: " + requestId));
        if (request.getStatus() != BookRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        Book book = bookRepository.findByIdForUpdate(request.getBook().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Book not found with ID: " + request.getBook().getId()));
        if (book.getQuantity() == null || book.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient book quantity available");
        }

        LocalDate issueDate = LocalDate.now();
        BookIssue issue = new BookIssue();
        issue.setUser(request.getUser());
        issue.setBook(book);
        issue.setQuantity(request.getQuantity());
        issue.setIssueDate(issueDate);
        issue.setDueDate(issueDate.plusDays(14));
        issue.setReturnDate(null);
        issue.setStatus(IssueStatus.ISSUED);

        book.setQuantity(book.getQuantity() - request.getQuantity());
        bookRepository.save(book);
        BookIssue savedIssue = bookIssueRepository.save(issue);

        request.setStatus(BookRequestStatus.APPROVED);
        bookRequestRepository.save(request);
        return savedIssue;
    }

    @Transactional
    public BookRequest rejectRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book request not found with ID: " + requestId));
        if (request.getStatus() != BookRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        request.setStatus(BookRequestStatus.REJECTED);
        return bookRequestRepository.save(request);
    }
}

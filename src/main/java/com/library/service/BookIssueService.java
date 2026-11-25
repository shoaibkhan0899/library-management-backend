package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.User;
import com.library.enums.IssueStatus;
import com.library.enums.Role;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookIssueService {

    private final BookIssueRepository bookIssueRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookIssueService(BookIssueRepository bookIssueRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository) {
        this.bookIssueRepository = bookIssueRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookIssue issueBook(Long userId, Long bookId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only students can borrow books");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found with ID: " + bookId));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (book.getQuantity() == null || book.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient book quantity available");
        }

        LocalDate issueDate = LocalDate.now();

        BookIssue bookIssue = new BookIssue();
        bookIssue.setUser(user);
        bookIssue.setBook(book);
        bookIssue.setQuantity(quantity);
        bookIssue.setIssueDate(issueDate);
        bookIssue.setDueDate(issueDate.plusDays(14));
        bookIssue.setReturnDate(null);
        bookIssue.setStatus(IssueStatus.ISSUED);

        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);

        return bookIssueRepository.save(bookIssue);
    }

    @Transactional
    public BookIssue returnBook(Long issueId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book issue not found with ID: " + issueId));

        if (bookIssue.getStatus() == IssueStatus.RETURNED) {
            throw new IllegalStateException("This book issue has already been returned");
        }

        bookIssue.setReturnDate(LocalDate.now());
        bookIssue.setStatus(IssueStatus.RETURNED);

        Book book = bookIssue.getBook();
        int availableQuantity = book.getQuantity() == null ? 0 : book.getQuantity();
        book.setQuantity(availableQuantity + bookIssue.getQuantity());
        bookRepository.save(book);

        return bookIssueRepository.save(bookIssue);
    }

    @Transactional(readOnly = true)
    public List<BookIssue> getAllIssues() {
        return bookIssueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BookIssue> getMyIssues(Long userId) {
        return bookIssueRepository.findAllByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<BookIssue> getMyIssuesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with email: " + email));
        return bookIssueRepository.findAllByUser_Id(user.getId());
    }
}

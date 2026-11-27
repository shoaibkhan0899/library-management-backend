package com.library.repository;

import com.library.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select book from Book book where book.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);
}

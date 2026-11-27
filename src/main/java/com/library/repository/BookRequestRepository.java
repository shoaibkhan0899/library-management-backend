package com.library.repository;

import com.library.entity.BookRequest;
import com.library.enums.BookRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    @EntityGraph(attributePaths = {"user", "book"})
    List<BookRequest> findAllByOrderByRequestedAtDesc();

    @EntityGraph(attributePaths = {"user", "book"})
    List<BookRequest> findAllByUser_IdOrderByRequestedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select request from BookRequest request where request.id = :id")
    Optional<BookRequest> findByIdForUpdate(@Param("id") Long id);
}

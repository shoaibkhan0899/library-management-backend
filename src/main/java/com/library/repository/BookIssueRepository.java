package com.library.repository;

import com.library.entity.BookIssue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "book"})
    List<BookIssue> findAll();

    @EntityGraph(attributePaths = {"user", "book"})
    List<BookIssue> findAllByUser_Id(Long userId);
}

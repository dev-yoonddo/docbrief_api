package com.docbrief.summary.repository;

import com.docbrief.summary.domain.SummaryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SummaryJobRepository extends JpaRepository<SummaryJob, Long> {
    @Query("select s from SummaryJob s")
    List<SummaryJob> findAllByJpql();
}

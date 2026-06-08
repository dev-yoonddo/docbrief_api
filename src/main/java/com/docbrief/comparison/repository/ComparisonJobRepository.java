package com.docbrief.comparison.repository;

import com.docbrief.comparison.domain.ComparisonJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComparisonJobRepository extends JpaRepository<ComparisonJob, Long> {
}

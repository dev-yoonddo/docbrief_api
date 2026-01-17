package com.docbrief.summary.service;

import com.docbrief.summary.repository.SummaryJobRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaTestConfig {



    @Bean
    CommandLineRunner summaryJobSelectTest(SummaryJobRepository repository) {

        return args -> {
            System.out.println("=== SummaryJob Select Test ===");
            long count = repository.count();
            System.out.println("SummaryJob count = " + count);

            repository.findAllByJpql()
            .forEach(job ->
                    System.out.println("JPQL ID = " + job.getJobId())
            );
        };
    }

    @Bean
    CommandLineRunner entityCheck(EntityManager em) {
        return args -> {
            em.getMetamodel().getEntities()
                    .forEach(e -> System.out.println("Entity = " + e.getName()));
        };
    }
}
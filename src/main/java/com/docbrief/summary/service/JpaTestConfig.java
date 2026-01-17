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
            long count = repository.count();

            repository.findAllByJpql();
        };
    }

    @Bean
    CommandLineRunner entityCheck(EntityManager em) {
        return args -> {
            em.getMetamodel().getEntities()
                    .forEach(e -> e.getName());
        };
    }
}
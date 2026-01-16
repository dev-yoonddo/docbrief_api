package com.docbrief;

import com.docbrief.summary.service.JpaTestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class DocbriefApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocbriefApplication.class, args);
        System.out.println("start application !!");
	}

}

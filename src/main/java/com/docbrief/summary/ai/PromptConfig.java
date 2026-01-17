package com.docbrief.summary.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.summary")
public class PromptConfig {
    private String role;
    private String instruction;
    private String constraint;
    private String highlightRule;
    private String outputFormat;
    private String violationReason;
}

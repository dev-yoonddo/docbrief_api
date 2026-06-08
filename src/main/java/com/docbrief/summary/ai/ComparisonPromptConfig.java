package com.docbrief.summary.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 문서 비교분석용 프롬프트 설정
 * application.yml의 prompt.comparison 섹션에서 로드
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.comparison")
public class ComparisonPromptConfig {
    private String role;
    private String instruction;
    private String constraint;
    private String outputFormat;
    private String violationReasons;
}

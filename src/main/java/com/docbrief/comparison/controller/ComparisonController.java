package com.docbrief.comparison.controller;

import com.docbrief.common.ComparisonProcessingException;
import com.docbrief.common.ResourceNotFoundException;
import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.comparison.domain.ComparisonResult;
import com.docbrief.comparison.dto.*;
import com.docbrief.comparison.repository.ComparisonJobRepository;
import com.docbrief.comparison.repository.ComparisonResultRepository;
import com.docbrief.comparison.service.ComparisonService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/comparisons")
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final ComparisonJobRepository comparisonJobRepository;
    private final ComparisonResultRepository comparisonResultRepository;
    private final ObjectMapper objectMapper;

    /**
     * 여러 파일을 동시에 업로드하여 비교분석 Job 생성
     * POST /comparisons
     */
    @PostMapping
    public ResponseEntity<ComparisonCreateResponse> createComparison(
            @RequestParam("files") List<MultipartFile> files) {
        ComparisonCreateResponse response = comparisonService.createComparisonJob(files);
        return ResponseEntity.ok(response);
    }

    /**
     * 두 문서의 비교분석 처리 시작
     * POST /comparisons/{id}/process
     *
     * @param id 비교분석 Job ID
     * @param request 비교분석 요청 정보 (documentAId, documentBId, mode)
     * @return 처리 시작 후 상태 응답
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<ComparisonStatusResponse> startComparison(
            @PathVariable Long id,
            @RequestBody ComparisonProcessRequest request) {
        // startComparison() 메서드 호출하여 비교분석 처리 시작
        comparisonService.startComparison(id, request.getDocumentAId(), request.getDocumentBId(), request.getMode());

        // 처리 시작 후 현재 상태 조회
        // 실제 구현에서는 ComparisonJobRepository에서 조회
        // 여기서는 SimpleResponse 반환 (실제 상태는 ComparisonService 내에서 업데이트됨)
        log.info("비교분석 처리 시작 - jobId: {}, documentAId: {}, documentBId: {}, mode: {}",
                id, request.getDocumentAId(), request.getDocumentBId(), request.getMode());

        return ResponseEntity.ok(new ComparisonStatusResponse(
                id,
                com.docbrief.comparison.domain.ComparisonStatus.PROCESSING,
                null,
                null
        ));
    }

    /**
     * 비교분석 작업 상태 조회
     * GET /comparisons/{id}/status
     *
     * @param id 비교분석 Job ID
     * @return 비교분석 작업의 현재 상태 정보
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<ComparisonStatusResponse> getComparisonStatus(@PathVariable Long id) {
        // ComparisonJobRepository에서 Job 조회
        ComparisonJob job = comparisonJobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("비교분석 작업을 찾을 수 없습니다 - jobId: {}", id);
                    return new ResourceNotFoundException(id);
                });

        log.info("비교분석 작업 상태 조회 성공 - jobId: {}, status: {}", id, job.getStatus());

        return ResponseEntity.ok(new ComparisonStatusResponse(
                job.getJobId(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        ));
    }

    /**
     * 비교분석 결과 조회
     * GET /comparisons/{id}
     *
     * @param id 비교분석 Job ID
     * @return 비교분석 결과 (공통사항, 차이점 등)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComparisonResponse> getComparison(@PathVariable Long id) {
        // ComparisonResultRepository에서 jobId로 결과 조회
        ComparisonResult result = comparisonResultRepository.findByComparisonJob_JobId(id)
                .orElseThrow(() -> {
                    log.warn("비교분석 결과를 찾을 수 없습니다 - jobId: {}", id);
                    return new ResourceNotFoundException(id);
                });

        // JSON 문자열을 ComparisonResponse 객체로 파싱
        try {
            ComparisonResponse response = objectMapper.readValue(result.getContent(), ComparisonResponse.class);
            log.info("비교분석 결과 조회 성공 - jobId: {}", id);
            return ResponseEntity.ok(response);
        } catch (JsonMappingException e) {
            log.error("JSON 파싱 오류 - jobId: {}, 원인: {}", id, e.getMessage(), e);
            throw new ComparisonProcessingException("결과 데이터 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("결과 데이터 처리 중 오류 발생 - jobId: {}, 원인: {}", id, e.getMessage(), e);
            throw new ComparisonProcessingException("결과 데이터 처리 실패: " + e.getMessage());
        }
    }
}

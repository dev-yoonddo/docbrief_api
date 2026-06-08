package com.docbrief.comparison.service;

import com.docbrief.common.*;
import com.docbrief.comparison.domain.ComparisonDocument;
import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.comparison.domain.ComparisonStatus;
import com.docbrief.comparison.dto.ComparisonCreateResponse;
import com.docbrief.comparison.repository.ComparisonDocumentRepository;
import com.docbrief.comparison.repository.ComparisonJobRepository;
import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.repository.DocumentRepository;
import com.docbrief.document.service.SummaryRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final ComparisonJobRepository comparisonJobRepository;
    private final ComparisonDocumentRepository comparisonDocumentRepository;
    private final DocumentRepository documentRepository;
    private final SummaryRequestService summaryRequestService;
    private final ComparisonProcessor comparisonProcessor;

    /**
     * 여러 파일을 받아 비교분석 Job을 생성하고, 각 파일에 대한 Document를 생성
     */
    @Transactional
    public ComparisonCreateResponse createComparisonJob(List<MultipartFile> files) {
        // 파일 유효성 검증
        if (files == null || files.isEmpty()) {
            throw new DocumentResourceException("파일을 1개 이상 업로드해야 합니다.");
        }
        if (files.size() < 2) {
            throw new DocumentResourceException("비교분석에는 파일이 2개 이상 필요합니다.");
        }

        // ComparisonJob 생성
        ComparisonJob job = new ComparisonJob(ComparisonStatus.CREATED);
        comparisonJobRepository.save(job);

        List<Long> documentIds = new ArrayList<>();

        // 각 파일에 대해 Document 생성 및 ComparisonDocument 연결
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            if (file == null || file.isEmpty()) {
                throw new DocumentResourceException("빈 파일이 포함되어 있습니다: " + (i + 1) + "번째 파일");
            }
            if (file.getOriginalFilename() == null) {
                throw new DocumentResourceException("파일명이 올바르지 않습니다: " + (i + 1) + "번째 파일");
            }

            DocumentType type = DocumentType.fromFileName(file.getOriginalFilename());
            Document document = new Document(file.getOriginalFilename(), type);
            documentRepository.save(document);

            ComparisonDocument comparisonDocument = new ComparisonDocument(job, document, i);
            comparisonDocumentRepository.save(comparisonDocument);

            documentIds.add(document.getDocumentId());
        }

        return new ComparisonCreateResponse(job.getJobId(), documentIds);
    }

    /**
     * 두 문서의 비교분석을 시작합니다.
     * 문서 유효성 검증 후 ComparisonJob 상태를 PROCESSING으로 변경하고,
     * 각 문서의 SummaryInternalRequest를 조회하여 ComparisonProcessor에 위임합니다.
     *
     * @param jobId 비교분석 Job ID
     * @param documentAId 첫 번째 문서 ID
     * @param documentBId 두 번째 문서 ID
     * @param mode 비교 모드 (예: "full", "summary" 등)
     * @throws ResourceNotFoundException 문서를 찾을 수 없는 경우
     * @throws InvalidDocumentStatusException 문서 상태가 올바르지 않은 경우
     * @throws ComparisonProcessingException 비교분석 처리 중 오류 발생 시
     */
    @Transactional
    public void startComparison(Long jobId, Long documentAId, Long documentBId, String mode) {
        // ComparisonJob 조회
        ComparisonJob job = comparisonJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(jobId));

        // 두 문서의 유효성 검증
        Document documentA = documentRepository.findById(documentAId)
                .orElseThrow(() -> new ResourceNotFoundException(documentAId));
        Document documentB = documentRepository.findById(documentBId)
                .orElseThrow(() -> new ResourceNotFoundException(documentBId));

        // 문서 상태 검증 (EXTRACTED 이상 상태 확인)
        validateDocumentStatus(documentA, documentAId);
        validateDocumentStatus(documentB, documentBId);

        try {
            // 각 문서의 SummaryInternalRequest 조회
            SummaryInternalRequest requestA = summaryRequestService.buildSummaryInternalRequest(documentA);
            SummaryInternalRequest requestB = summaryRequestService.buildSummaryInternalRequest(documentB);

            // ComparisonProcessor에 위임하여 비교분석 처리
            // startComparisonEngine() 메서드는 내부적으로 작업 상태를 변경하고 결과를 저장
            comparisonProcessor.startComparisonEngine(job, requestA, requestB);

        } catch (DocumentParsingException e) {
            // 문서 파싱 예외 처리
            job.updateStatus(ComparisonStatus.FAILED);
            comparisonJobRepository.save(job);
            throw new ComparisonProcessingException("문서 파싱 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            job.updateStatus(ComparisonStatus.FAILED);
            comparisonJobRepository.save(job);
            throw new ComparisonProcessingException("비교분석 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 상태의 유효성을 검증합니다.
     * EXTRACTED 이상의 상태(EXTRACTED, SUMMARIZING, SUMMARIZED)만 비교분석 가능합니다.
     *
     * @param document 검증할 문서
     * @param documentId 문서 ID (에러 메시지용)
     * @throws InvalidDocumentStatusException 문서 상태가 유효하지 않은 경우
     */
    private void validateDocumentStatus(Document document, Long documentId) {
        DocumentStatus status = document.getStatus();

        // EXTRACTED 이상 상태만 허용
        if (status != DocumentStatus.EXTRACTED &&
            status != DocumentStatus.SUMMARIZING &&
            status != DocumentStatus.SUMMARIZED) {
            throw new InvalidDocumentStatusException(
                    "비교분석을 위해서는 문서가 파싱 완료된 상태(EXTRACTED 이상)여야 합니다. " +
                    "현재 상태: " + status + ", 문서 ID: " + documentId
            );
        }
    }
}

package com.docbrief.document.service;

import com.docbrief.common.InvalidDocumentStatusException;
import com.docbrief.common.ResourceNotFoundException;
import com.docbrief.common.DocumentResourceException;
import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentStatusService documentStatusService;
    private final DocumentParsingService documentParsingService;
    private final SummaryRequestService summaryRequestService;

    private final DocumentRepository documentRepository;

    public Long create(MultipartFile file){
        if(file == null || file.isEmpty()) { throw new DocumentResourceException("파일 업로드가 필요합니다."); }
        if(file.getOriginalFilename() == null) { throw new DocumentResourceException("파일명이 올바르지 않습니다."); }

        DocumentType type = DocumentType.fromFileName(file.getOriginalFilename());
        Document document = new Document(file.getOriginalFilename(), type);
        documentRepository.save(document);
        return document.getDocumentId();
    }

    public Long createFromUrl(String url){
        if (url == null || url.isBlank()) { throw new DocumentResourceException("Url 입력이 필요합니다."); }

        DocumentType type = DocumentType.HTML;
        Document document = new Document(url, type);
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException(documentId));
        return document.getStatus();
    }

    public SummaryInternalRequest processDocumentParsing(Long documentId, MultipartFile file){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(documentId));
        DocumentStatus status = document.getStatus();
        switch(status) {
            case CREATED:
            case FAILED:
                documentParsingService.parseAndSaveDocument(documentId, file);
                break;
            case EXTRACTING:
                throw new InvalidDocumentStatusException("해당 문서는 이미 파싱중입니다.");
            case EXTRACTED:
                break;
            case SUMMARIZING:
                throw new InvalidDocumentStatusException("해당 문서는 이미 요약중입니다.");
            case SUMMARIZED:
                break;
            default:
                throw new InvalidDocumentStatusException(status);
        }

        if (document.getStatus() != DocumentStatus.EXTRACTED){ throw new InvalidDocumentStatusException(document.getStatus());         }

        return summaryRequestService.buildSummaryInternalRequest(document);
    }

    public SummaryInternalRequest processUrlParsing(Long documentId, String url){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(documentId));
        DocumentStatus status = document.getStatus();
        switch(status) {
            case CREATED:
            case FAILED:
                documentParsingService.parseAndSaveUrlHtml(documentId, url);
                break;
            case EXTRACTING:
                throw new InvalidDocumentStatusException("해당 문서는 이미 파싱중입니다.");
            case EXTRACTED:
            case SUMMARIZING:
                throw new InvalidDocumentStatusException("해당 문서는 이미 요약중입니다.");
            case SUMMARIZED:
                break;
            default:
                throw new InvalidDocumentStatusException(status);
        }
        return summaryRequestService.buildSummaryInternalRequest(document);
    }

}

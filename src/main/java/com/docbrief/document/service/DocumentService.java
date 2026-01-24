package com.docbrief.document.service;

import com.docbrief.common.DocumentResourceException;
import com.docbrief.common.InvalidDocumentStatusException;
import com.docbrief.common.ResourceNotFoundException;
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

    public Long create(String mode, MultipartFile file, String url){
        if ((file == null || file.isEmpty()) && (url == null || url.isBlank())) { throw new DocumentResourceException();}

        DocumentType type;
        Document document;
        switch (mode.toLowerCase()){
            case "file":
                if(file == null || file.isEmpty()) { throw new DocumentResourceException("파일 업로드가 필요합니다."); }
                if(file.getOriginalFilename() == null) { throw new DocumentResourceException("파일명이 올바르지 않습니다."); }

                type = DocumentType.fromFileName(file.getOriginalFilename());
                document = new Document(file.getOriginalFilename(), type);
                documentRepository.save(document);
                return document.getDocumentId();

            case "url":
                if (url == null || url.isBlank()) { throw new DocumentResourceException("Url 입력이 필요합니다."); }

                type = DocumentType.HTML;
                document = new Document(url, type);
                documentRepository.save(document);
                return document.getDocumentId();

            default:
                throw new DocumentResourceException();
        }
    }

    public SummaryInternalRequest processParsing(String mode, Long documentId, MultipartFile file, String url){
        if ((file == null || file.isEmpty()) && (url == null || url.isBlank())) { throw new DocumentResourceException();}

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(documentId));
        DocumentStatus status = document.getStatus();
        switch(status) {
            case CREATED:
            case FAILED:
                if("file".equals(mode.toLowerCase())){
                    documentParsingService.parseAndSaveDocument(documentId, file);
                }else if("url".equals(mode.toLowerCase())){
                    documentParsingService.parseAndSaveUrlHtml(documentId, url);
                }else{
                    throw new DocumentResourceException();
                }
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

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(documentId));
        return document.getStatus();
    }

}

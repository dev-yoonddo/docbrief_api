package com.docbrief.document.service;

import com.docbrief.document.domain.*;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.repository.DocumentContentRepository;
import com.docbrief.document.repository.DocumentParagraphRepository;
import com.docbrief.document.repository.DocumentRepository;
import com.docbrief.document.repository.DocumentSentenceRepository;
import com.docbrief.summary.service.SummaryProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SummaryRequestService {

    private final DocumentRepository documentRepository;
    private final DocumentContentRepository contentRepository;
    private final DocumentParagraphRepository paragraphRepository;
    private final DocumentSentenceRepository sentenceRepository;

    private final ObjectMapper objectMapper;
    private final SummaryProcessor summaryProcessor;

    // TODO:규격 협의 후 최종 반환타입 변경
    public String requestSummary(Long documentId) {

        // 문서 조회 및 문서 상태 체크
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for summary request not found"));

        if (!DocumentStatus.EXTRACTED.equals(document.getStatus())) {
            new IllegalStateException("this document is not already for summary");
        }

        // 문서 원문 조회
        DocumentContent documentContent = contentRepository.findByDocumentId(documentId);
        if (documentContent == null) {
            throw new IllegalStateException("document content for summray not found");
        }

        // 문단 및 문장 조회
        List<DocumentParagraph> paragraphs = paragraphRepository.findByDocumentIdOrderByParagraphOrder(documentId);
        List<SummaryInternalRequest.ParagraphDto> paragraphDtos = new ArrayList<>();

        for (DocumentParagraph paragraph : paragraphs) {
            List<DocumentSentence> sentences
                    = sentenceRepository.findByParagraphIdOrderBySentenceOrder(paragraph.getParagraphId());

            List<SummaryInternalRequest.SentenceDto> sentenceDtos =
                    sentences.stream().map(s -> new SummaryInternalRequest.SentenceDto(
                            s.getSentenceOrder(),
                            s.getSentenceText()
                    )).toList();

            paragraphDtos.add(new SummaryInternalRequest.ParagraphDto(paragraph.getParagraphOrder(), sentenceDtos));
        }

        SummaryInternalRequest request = new SummaryInternalRequest(documentId, documentContent.getFullText(), paragraphDtos);


        //return requestJson;

        // ai 분석 요청
        // 추후 return 값 결정 후 응답dto 구성(DocumentStatus, Stirng returnText...)
        // SummaryInternalResponse response  = analysisClient.summarize(requestJson);
        // SummaryInternalResponse 상태값에 따라 document 상태 업데이트
        // return analysisClient.summarize(requestJson);
        return summaryProcessor.startSummaryEngine(request);
    }


}

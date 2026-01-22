package com.docbrief.document.service;

import com.docbrief.document.domain.*;
import com.docbrief.document.parser.*;
import com.docbrief.document.repository.DocumentContentRepository;
import com.docbrief.document.repository.DocumentParagraphRepository;
import com.docbrief.document.repository.DocumentRepository;
import com.docbrief.document.repository.DocumentSentenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@AllArgsConstructor
public class DocumentParsingService {

    private final DocumentStatusService documentStatusService;
    private final DocumentTypeResolver typeResolver;
    private final ParserFactory parserFactory;

    private final DocumentRepository documentRepository;
    private final DocumentContentRepository contentRepository;
    private final DocumentParagraphRepository paragraphRepository;
    private final DocumentSentenceRepository sentenceRepository;

    // 문서 파싱 중 오류 발생 시 rollback
    @Transactional
    public void parseAndSaveDocument(Long documentId, MultipartFile file){
        // 영속성 보장을 위해 document 재조회
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for summarizing not found ::: documentId : " + documentId));

        // 최초/실패 후 재파싱하는 경우
        contentRepository.deleteByDocumentId(document.getDocumentId());
        documentStatusService.updateDocumentStatus(documentId, DocumentStatus.EXTRACTING);

        try(InputStream inputStream = file.getInputStream()) {
            DocumentType type = typeResolver.resolveFromFileName(file.getOriginalFilename());
            DocumentParser parser = parserFactory.getParser(type);
            ParsedText parsedText = parser.parse(inputStream);

            saveParsedText(document, parsedText);
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.EXTRACTED);

        }catch (Exception e){
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.FAILED);
            throw new RuntimeException("failed to parsing", e);
        }
    }

    @Transactional
    public void parseAndSaveUrlText(Long documentId, String url){
        // 영속성 보장을 위해 document 재조회
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for summarizing not found ::: documentId : " + documentId));

        // 최초/실패 후 재파싱하는 경우
        contentRepository.deleteByDocumentId(document.getDocumentId());
        documentStatusService.updateDocumentStatus(documentId, DocumentStatus.EXTRACTING);

        try{
            DocumentType type = DocumentType.URL;
            DocumentParser parser = parserFactory.getParser(type);
            ParsedText parsedText = parser.parseFromUrl(url);

            saveParsedText(document, parsedText);
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.EXTRACTED);

        }catch (Exception e){
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.FAILED);
            throw new RuntimeException("failed to parsing", e);
        }
    }

    // 부모의 transaction을 그대로 사용(기존 트랜잭션 전파)
    @Transactional
    private void saveParsedText(Document document, ParsedText parsedText){
        Long documentId = document.getDocumentId();

        // 원문 저장
        DocumentContent content = DocumentContent.create(documentId, parsedText.getFullText());
        contentRepository.save(content);

        // 문단, 문장 저장
        for(ParsedParagraph parsedParagraph : parsedText.getParagraphs()){
            DocumentParagraph paragraph = DocumentParagraph.create(documentId, parsedParagraph.getOrder());
            paragraphRepository.save(paragraph);

            for(ParsedSentence parsedSentence : parsedParagraph.getSentences()){
                DocumentSentence sentence
                        = DocumentSentence.create(documentId, paragraph.getParagraphId(), parsedSentence.getOrder(), parsedSentence.getText());
                sentenceRepository.save(sentence);
            }
        }
    }

}

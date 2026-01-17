package com.docbrief.document.service;

import com.docbrief.document.domain.*;
import com.docbrief.document.parser.*;
import com.docbrief.document.repository.DocumentContentRepository;
import com.docbrief.document.repository.DocumentParagraphRepository;
import com.docbrief.document.repository.DocumentSentenceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class DocumentParsingService {

    private final DocumentService documentService;

    private final DocumentContentRepository contentRepository;
    private final DocumentParagraphRepository paragraphRepository;
    private final DocumentSentenceRepository sentenceRepository;

    public void parseAndSaveDocument(Long documentId, MultipartFile file){

        Document document = documentService.updateStatus(documentId, DocumentStatus.EXTRACTING);

        try(InputStream inputStream = file.getInputStream()) {
            DocumentParser documentParser = new TxtDocumentParser();
            ParsedText parsedText = documentParser.parse(inputStream);

            saveParsedText(document, parsedText);
            document.updateStatus(DocumentStatus.EXTRACTED);

        }catch (IOException ioe){
            document.updateStatus(DocumentStatus.FAILED);
            throw new RuntimeException("failed to parsing", ioe);
        }
    }

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

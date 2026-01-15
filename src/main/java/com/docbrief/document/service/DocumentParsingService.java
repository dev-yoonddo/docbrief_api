package com.docbrief.document.service;

import com.docbrief.document.parser.DocumentParser;
import com.docbrief.document.parser.ParsedText;
import com.docbrief.document.parser.TxtDocumentParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentParsingService {

    private final DocumentParser documentParser = new TxtDocumentParser();

    public ParsedText parseTxtDocument(MultipartFile file){
        try(InputStream inputStream = file.getInputStream()) {
            return documentParser.parse(inputStream);
        }catch (IOException ioe){
            throw new RuntimeException("failed to parsing", ioe);
        }
    }
}

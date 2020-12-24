package com.askwinston.service.impl;

import com.askwinston.exception.NotFoundException;
import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;
import com.askwinston.repository.DocumentRepository;
import com.askwinston.service.DocumentStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private static final String WRONG_FILE_SYMBOLS = "[+]";

    private DocumentRepository repository;

    private DocumentStorage documentStorage;

    @Autowired
    public DocumentStorageService(DocumentRepository repository, DocumentStorage documentStorage) {
        this.repository = repository;
        this.documentStorage = documentStorage;
    }

    @Transactional
    public Document getDocumentById(Long documentId) throws NotFoundException {
        return repository.findById(documentId).orElseThrow(() -> new NotFoundException("Document with id " + documentId + " not found."));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Document createDocument(String folder, DocumentResource resource) {
        String filename = resource.getFilename();
        filename = filename.replaceAll(WRONG_FILE_SYMBOLS, "_");

        int n = filename.lastIndexOf('.');
        String type;
        if (n >= 0) {
            type = filename.substring(n);
            filename = filename.substring(0, n);
        } else {
            type = "";
        }
        filename = filename + "_" + UUID.randomUUID().toString().replace("-", "") + type;

        try {
            final Document document = documentStorage.create(folder, filename, resource.getContentType(), resource.getInputStream());
            return repository.save(document);
        } catch (Exception e) {
            throw new IllegalStateException("Error saving documentd", e);
        }
    }

    public DocumentResource getDocumentResource(Document document) {
        try {
            return documentStorage.load(document);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error loading document: " + document.getPath(), e);
        }
    }

    @Transactional
    public void deleteFile(Document document) {
        try {
            documentStorage.remove(document);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error deleting document: " + document.getPath(), e);
        }
        repository.delete(document);
    }
}

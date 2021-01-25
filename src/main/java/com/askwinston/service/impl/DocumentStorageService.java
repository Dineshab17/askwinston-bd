package com.askwinston.service.impl;

import com.askwinston.exception.NotFoundException;
import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;
import com.askwinston.repository.DocumentRepository;
import com.askwinston.service.DocumentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class DocumentStorageService {

    private static final String WRONG_FILE_SYMBOLS = "[+]";

    private DocumentRepository repository;

    private DocumentStorage documentStorage;

    @Autowired
    public DocumentStorageService(DocumentRepository repository, DocumentStorage documentStorage) {
        this.repository = repository;
        this.documentStorage = documentStorage;
    }

    /**
     * @param documentId
     * @return Document
     * @throws NotFoundException
     * To get the document details by document id
     */
    @Transactional
    public Document getDocumentById(Long documentId) throws NotFoundException {
        return repository.findById(documentId).orElseThrow(() -> new NotFoundException("Document with id " + documentId + " not found."));
    }

    /**
     * @param folder
     * @param resource
     * @return To upload document in google storage
     */
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

    /**
     * @param document
     * @return DocumentResource
     * To get the document resource form document details
     */
    public DocumentResource getDocumentResource(Document document) {
        try {
            return documentStorage.load(document);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error loading document: " + document.getPath(), e);
        }
    }

    /**
     * @param document
     * To delete file or document from google storage and table
     */
    @Transactional
    public void deleteFile(Document document) {
        try {
            documentStorage.remove(document);
        } catch (IOException e) {
            log.error("Error deleting document: {}", document.getPath());
            throw new IllegalArgumentException("Error deleting document: " + document.getPath(), e);
        }
        repository.delete(document);
    }
}

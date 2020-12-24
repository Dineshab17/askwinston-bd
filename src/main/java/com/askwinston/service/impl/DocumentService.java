package com.askwinston.service.impl;

import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;
import com.askwinston.model.User;
import com.askwinston.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

    private UserRepository userRepository;

    private DocumentStorageService documentStorageService;

    @Autowired
    public DocumentService(UserRepository userRepository, DocumentStorageService documentStorageService) {
        this.userRepository = userRepository;
        this.documentStorageService = documentStorageService;
    }

    @Transactional
    public Document saveIdDocument(Long userId, String name, String contentType, byte[] data) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (user.getIdDocument() != null)
            documentStorageService.deleteFile(user.getIdDocument());
        Document document = saveDocument(userId, name, contentType, data);
        user.setIdDocument(document);
        userRepository.save(user);
        return document;
    }

    @Transactional
    public Document saveInsuranceDocument(Long userId, String name, String contentType, byte[] data) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (user.getInsuranceDocument() != null)
            documentStorageService.deleteFile(user.getInsuranceDocument());
        Document document = saveDocument(userId, name, contentType, data);
        user.setInsuranceDocument(document);
        userRepository.save(user);
        return document;
    }

    public Document saveDocument(Long userId, String name, String contentType, byte[] data) {
        return documentStorageService.createDocument(folder(userId), new DocumentResource(name, contentType, data));
    }

    public static String folder(Long userId) {
        return "user/" + userId;
    }

}

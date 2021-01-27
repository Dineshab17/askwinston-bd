package com.askwinston.service.impl;

import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;
import com.askwinston.model.User;
import com.askwinston.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DocumentService {

    private UserRepository userRepository;

    private DocumentStorageService documentStorageService;

    @Autowired
    public DocumentService(UserRepository userRepository, DocumentStorageService documentStorageService) {
        this.userRepository = userRepository;
        this.documentStorageService = documentStorageService;
    }

    /**
     * @param userId
     * @param name
     * @param contentType
     * @param data
     * @return Document
     * To save id proof document of the patient
     */
    @Transactional
    public Document saveIdDocument(Long userId, String name, String contentType, byte[] data) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (user.getIdDocument() != null)
            documentStorageService.deleteFile(user.getIdDocument());
        Document document = saveDocument(userId, name, contentType, data);
        user.setIdDocument(document);
        userRepository.save(user);
        log.info("Id proof document for the patient with id {} saved.", userId);
        return document;
    }

    /**
     * @param userId
     * @param name
     * @param contentType
     * @param data
     * @return Document
     * To save Insurance Document of the patient
     */
    @Transactional
    public Document saveInsuranceDocument(Long userId, String name, String contentType, byte[] data) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (user.getInsuranceDocument() != null)
            documentStorageService.deleteFile(user.getInsuranceDocument());
        Document document = saveDocument(userId, name, contentType, data);
        user.setInsuranceDocument(document);
        log.info("Insurance Document for the patient with id {} is saved", userId);
        userRepository.save(user);
        return document;
    }

    /**
     * @param userId
     * @param name
     * @param contentType
     * @param data
     * @return Document
     * To save the document of the user
     */
    public Document saveDocument(Long userId, String name, String contentType, byte[] data) {
        return documentStorageService.createDocument(folder(userId), new DocumentResource(name, contentType, data));
    }

    /**
     * @param userId
     * @return String
     * To create folder with user id
     */
    public static String folder(Long userId) {
        return "user/" + userId;
    }

}

package com.askwinston.web.controller;

import com.askwinston.exception.NotFoundException;
import com.askwinston.helper.HttpHelper;
import com.askwinston.model.Document;
import com.askwinston.service.impl.DocumentService;
import com.askwinston.service.impl.DocumentStorageService;
import com.askwinston.web.secuity.AwUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping(value = "/document")
public class DocumentController {

    private DocumentStorageService documentStorageService;
    private DocumentService documentService;

    @Autowired
    public DocumentController(DocumentStorageService documentStorageService,
                              DocumentService documentService) {
        this.documentStorageService = documentStorageService;
        this.documentService = documentService;
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long documentId) throws NotFoundException {
        final Document document = documentStorageService.getDocumentById(documentId);
        return HttpHelper.fileDownload(documentStorageService.getDocumentResource(document));
    }

    @GetMapping("/{documentId}/view")
    public ResponseEntity<InputStreamResource> view(@PathVariable Long documentId) throws NotFoundException {
        final Document document = documentStorageService.getDocumentById(documentId);
        return HttpHelper.fileView(documentStorageService.getDocumentResource(document));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Long save(@AuthenticationPrincipal AwUserPrincipal principal,
                     @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

}

package com.askwinston.service;

import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentStorage {

    String type();

    Document create(String folder, String filename, String contentType, InputStream inputStream) throws IOException;

    DocumentResource load(Document document) throws IOException;

    void remove(Document document) throws IOException;

}

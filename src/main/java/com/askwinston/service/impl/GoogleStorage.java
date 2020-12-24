package com.askwinston.service.impl;

import com.amazonaws.util.IOUtils;
import com.askwinston.model.Document;
import com.askwinston.model.DocumentResource;
import com.askwinston.service.DocumentStorage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class GoogleStorage implements DocumentStorage {

    private Storage storage;
    private Bucket bucket;

    public GoogleStorage(@Value("${google.cloud.platform.credentials.json.path}") Resource credentialsJson,
                         @Value("${google.cloud.platform.bucket.name}") String bucketName) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsJson.getInputStream())
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        bucket = storage.get(bucketName);
    }

    @Override
    public String type() {
        return "google";
    }

    @Override
    public Document create(String folder, String filename, String contentType, InputStream inputStream) throws IOException {
        String key = folder + "/" + filename;
        BlobId blobId = BlobId.of(bucket.getName(), key);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        byte[] data = IOUtils.toByteArray(inputStream);
        storage.create(blobInfo, data);
        return new Document(contentType, filename, key, getMD5Hex(data), type());
    }

    @Override
    public DocumentResource load(Document document) throws IOException {
        String key = document.getFolder() + "/" + document.getFilename();
        BlobId blobId = BlobId.of(bucket.getName(), key);
        Blob blob = storage.get(blobId);
        return new DocumentResource(document.getFilename(), document.getContentType(), blob.getContent());
    }

    @Override
    public void remove(Document document) throws IOException {
        String key = document.getFolder() + "/" + document.getFilename();
        storage.delete(bucket.getName(), key);
    }

    private byte[] getMD5Hex(byte[] data) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        md.update(data);

        return md.digest();
    }
}

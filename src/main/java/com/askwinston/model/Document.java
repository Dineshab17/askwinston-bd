package com.askwinston.model;

import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "path", length = 2_000, nullable = false)
    private String path;

    @Column(name = "hash", nullable = false)
    private byte[] hash;

    @Column(name = "storage", nullable = false)
    private String storage;

    public Document() {
    }

    public Document(String contentType, String filename, String path, byte[] hash, String storage) {
        super();
        this.contentType = contentType;
        this.filename = filename;
        this.path = path;
        this.hash = hash;
        this.storage = storage;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    byte[] getHash() {
        return hash;
    }

    public String getStorage() {
        return storage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public String getFolder() {
        return path.substring(0, path.lastIndexOf("/"));
    }

}

package com.askwinston.model;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@JsonIgnoreProperties(value = {"inputStream"})
public class DocumentResource {
    private String filename;
    private String contentType;
    private byte[] data;
    private InputStream inputStream;

    public DocumentResource(String filename, String contentType, byte[] data) {
        super();
        Assert.notNull(data, "data must not be null");
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
    }

    public DocumentResource(String filename, String contentType, InputStream inputStream) {
        super();
        Assert.notNull(inputStream, "inputStream must not be null");
        this.filename = filename;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        if (data == null) {
            try {
                data = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot load resource", e);
            }
        }
        return data;
    }

    public InputStream getInputStream() {
        if (inputStream == null) {
            inputStream = new ByteArrayInputStream(data);
        }
        return inputStream;
    }

    public String getText(Charset charset) {
        return new String(getData(), charset);
    }
}

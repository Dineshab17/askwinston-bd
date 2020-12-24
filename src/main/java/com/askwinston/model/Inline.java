package com.askwinston.model;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class Inline {
    private String cid;
    private Resource resource;

    public Inline(String cid, Resource resource) {
        this.cid = cid;
        this.resource = resource;
    }
}

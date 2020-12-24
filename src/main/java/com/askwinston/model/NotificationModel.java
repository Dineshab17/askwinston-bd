package com.askwinston.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NotificationModel {

    @Setter(AccessLevel.PRIVATE)
    private Map<String, String> map = new HashMap<>();

    private String email;

    @Setter(AccessLevel.PRIVATE)
    private List<Inline> inlines = new ArrayList<>();
}

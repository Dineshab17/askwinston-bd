package com.askwinston.notification.mapper;

import com.askwinston.model.NotificationModel;

import java.util.Map;

public interface EntityModelMapper<T> {

    void doMap(T entity, NotificationModel model, Map<String, String> additionalParams);
}

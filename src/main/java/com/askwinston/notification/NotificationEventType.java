
package com.askwinston.notification;

import com.askwinston.model.NotificationTarget;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.function.Function;

/*
    Class that describes an event that can occur on the system that requires sending a notification
 */

@Data
public class NotificationEventType<T extends Notifiable> implements Serializable {

    private final String name;

    @Transient
    private final Function<Pair<T, NotificationTarget>, String> emailProvider;

    private final Class<T> consumedEntityClass;

    protected NotificationEventType(String name, Class<T> consumedEntityClass, Function<Pair<T, NotificationTarget>, String> emailProvider) {
        this.name = name;
        this.emailProvider = emailProvider;
        this.consumedEntityClass = consumedEntityClass;
    }


}

package com.askwinston.notification;

import com.askwinston.model.*;
import com.askwinston.service.EmailService;
import com.askwinston.subscription.ProductSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class NotificationEventTypeContainer {

    private static EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        NotificationEventTypeContainer.emailService = emailService;
    }

    public static final NotificationEventType<DoctorSlot> NEW_APPOINTMENT =
            addValue("New appointment", DoctorSlot.class, pair -> {
                if (pair.getSecond().equals(NotificationTarget.ADMIN))
                    return NotificationEventTypeContainer.getAskwinstonEmailInfo();
                else
                    return pair.getFirst().getPatient().getEmail();
            });

    public static final NotificationEventType<PurchaseOrder> ORDER_APPROVAL =
            addValue("Order approval", PurchaseOrder.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> NEW_SUBSCRIPTION =
            addValue("New subscription", ProductSubscription.class, pair -> {
                if (pair.getSecond().equals(NotificationTarget.ADMIN))
                    return NotificationEventTypeContainer.getAskwinstonEmailInfo();
                else
                    return pair.getFirst().getUser().getEmail();
            });

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_APPROVAL =
            addValue("Subscription approval", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<PurchaseOrder> ORDER_REJECTION =
            addValue("Order rejection", PurchaseOrder.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_REJECTION =
            addValue("Subscription rejection", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<PurchaseOrder> ORDER_CHECKOUT =
            addValue("Order checkout", PurchaseOrder.class, pair -> {
                if (pair.getSecond().equals(NotificationTarget.DOCTOR))
                    return pair.getFirst().getDoctor().getEmail();
                else if (pair.getSecond().equals(NotificationTarget.PATIENT))
                    return pair.getFirst().getUser().getEmail();
                else
                    return NotificationEventTypeContainer.getAskwinstonEmailInfo();
            });

    public static final NotificationEventType<PurchaseOrder> ORDER_REFILL =
            addValue("Order refill", PurchaseOrder.class, pair -> {
                if (pair.getSecond().equals(NotificationTarget.PHARMACY))
                    return emailService.choosePharmacyEmail(pair.getFirst().getShippingAddressProvince());
                else
                    return pair.getFirst().getDoctor().getEmail();
            });

    public static final NotificationEventType<ProductSubscription> EARLY_REFILL_SUBMITTED =
            addValue("Early refill submitted", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<PurchaseOrder> RECEIPT =
            addValue("Receipt", PurchaseOrder.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<User> REGISTRATION =
            addValue("Registration", User.class, pair -> pair.getFirst().getEmail());

    public static final NotificationEventType<User> PASSWORD_RESET =
            addValue("Password was reset", User.class, pair -> pair.getFirst().getEmail());

    public static final NotificationEventType<User> ID_DOWNLOAD_CHECK_FAILED =
            addValue("ID download reminder", User.class, pair -> pair.getFirst().getEmail());

    public static final NotificationEventType<ProductSubscription> AUTO_REFILL_SKIP =
            addValue("Auto-Refill-Skip", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<User> CONSULTATION_REMINDER =
            addValue("Consultation reminder", User.class, pair -> pair.getFirst().getEmail());

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_PAUSED =
            addValue("Subscription paused", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_RESUMED =
            addValue("Subscription resumed", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_CANCELED =
            addValue("Subscription canceled by patient", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ContactUsRecord> CONTACT_US_REQUEST_SAVED =
            addValue("New contact us request", ContactUsRecord.class, pair -> NotificationEventTypeContainer.getAskwinstonEmailInfo());

    public static final NotificationEventType<PurchaseOrder> PHARMACIST_APPROVED_RX =
            addValue("Pharmacist approved RX", PurchaseOrder.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> TRANSFER_RX_SUBMITTED =
            addValue("Transfer RX submitted", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_PAUSED_BY_USER =
            addValue("Subscription paused by patient", ProductSubscription.class, pair -> {
                if (pair.getSecond().equals(NotificationTarget.ADMIN))
                    return NotificationEventTypeContainer.getAskwinstonEmailInfo();
                else
                    return pair.getFirst().getUser().getEmail();
            });

    public static final NotificationEventType<ProductSubscription> SUBSCRIPTION_PAUSED_BY_USER_RESUMED =
            addValue("Subscription paused by patient resumed", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    public static final NotificationEventType<ProductSubscription> REFILL_SOON_REMINDER =
            addValue("Refill soon reminder", ProductSubscription.class, pair -> pair.getFirst().getUser().getEmail());

    private static Map<String, NotificationEventType<?>> values;

    public static <T extends Notifiable> NotificationEventType<T> addValue(String name, Class<T> consumedEntityClass, Function<Pair<T, NotificationTarget>, String> emailProvider) {
        NotificationEventType<T> notificationEventType = new NotificationEventType<>(name, consumedEntityClass, emailProvider);
        getValues().put(name.toUpperCase(), notificationEventType);
        return notificationEventType;
    }

    public static NotificationEventType<?> valueOf(String name) {
        if (name == null)
            throw new NullPointerException("Name is null");
        if (values.containsKey(name.toUpperCase())) {
            return getValues().get(name.toUpperCase());
        } else {
            throw new IllegalArgumentException("No NotificationEventType with name " + name);
        }
    }

    public static Map<String, NotificationEventType<?>> getValues() {
        if (values == null)
            values = new HashMap<>();
        return values;
    }

    public static String getAskwinstonEmailInfo() {
        return emailService.getEmailInfo();
    }
}

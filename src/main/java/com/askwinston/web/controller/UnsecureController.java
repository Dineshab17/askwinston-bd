package com.askwinston.web.controller;

import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.order.OrderEngine;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.SubscriptionEngine;
import com.rollbar.notifier.Rollbar;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

/*
    Controller used for debug or testing.
    Available only with 'dev' or 'demo' profiles.
 */

@RestController
@RequestMapping("/unsecure")
@Profile({"demo", "alpha", "dev", "local"})
public class UnsecureController {

    private final NotificationEngine notificationEngine;
    private final OrderEngine orderEngine;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionEngine subscriptionEngine;

    public UnsecureController(NotificationEngine notificationEngine,
                              OrderEngine orderEngine,
                              PasswordEncoder passwordEncoder,
                              SubscriptionEngine subscriptionEngine) {
        this.notificationEngine = notificationEngine;
        this.orderEngine = orderEngine;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionEngine = subscriptionEngine;
    }

    @Autowired
    private Environment env;

    @Autowired
    private Rollbar rollbar;

    @GetMapping(value = "/password/{password}")
    public String encryptPassword(@PathVariable String password) {
        return passwordEncoder.encode(password);
    }

    @GetMapping(value = "/wait/{sec}")
    public void wait(@PathVariable long sec, HttpServletResponse hsr) throws InterruptedException, IOException {
        for (long i = 1; i <= sec; i++) {
            Thread.sleep(1000);
            hsr.getOutputStream().print(i);
            hsr.getOutputStream().println();
            hsr.getOutputStream().flush();
        }
        hsr.getOutputStream().close();
    }

    @GetMapping(value = "/test-mail/{id}")
    public void testMail(@PathVariable Long id) {
        notificationEngine.notify(NotificationEventTypeContainer.RECEIPT, orderEngine.getById(id));
    }

    @GetMapping(value = "/subscription/check")
    public String performCheckSubscriptions() {
        subscriptionEngine.checkSubscriptions();
        return "Done";
    }

    @GetMapping(value = "/ping")
    public String ping() {
        return LocalDate.now().toString();
    }

    @GetMapping(value = "/debug-rollbar")
    public String debugRollbar() {
        rollbar.debug("Here is some debug message from Spring");
        return "Done";
    }

    @GetMapping(value = "/pause/{id}")
    public void testPauseSubscription(@PathVariable Long id) {
        ProductSubscription subscription = subscriptionEngine.getById(id);
        subscriptionEngine.pauseSubscription(subscription, "Test");
    }

    @GetMapping(value = "/resume/{id}")
    public void testResumeSubscription(@PathVariable Long id) {
        ProductSubscription subscription = subscriptionEngine.getById(id);
        subscriptionEngine.resumeSubscription(subscription);
    }

    @GetMapping(value = "/profile")
    public String[] profile() {
        return env.getActiveProfiles();
    }
}

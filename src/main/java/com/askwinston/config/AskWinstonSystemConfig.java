package com.askwinston.config;

import com.askwinston.subscription.SubscriptionEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AskWinstonSystemConfig {

    @Value("${askwinston.db.init-subscription-id:true}")
    private boolean initSubscriptionId;

    private SubscriptionEngine subscriptionEngine;

    public AskWinstonSystemConfig(SubscriptionEngine subscriptionEngine) {
        this.subscriptionEngine = subscriptionEngine;
    }

    @PostConstruct
    public void postConstruct() {
        if (initSubscriptionId) {
            //ensure that ProductSubscription IDs start with 1001
            subscriptionEngine.initSubscriptionId(1001L);
        }
    }
}

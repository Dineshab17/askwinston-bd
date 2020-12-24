package com.askwinston.config;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration()
@ComponentScan({
        // ADD YOUR PROJECT PACKAGE HERE
        "com.askwinston",
        "com.rollbar.spring"
})
public class RollbarConfig {
    @Autowired
    private Environment env;

    @Value("${rollbar.access-token}")
    private String accessToken;

    /**
     * Register a Rollbar bean to configure App with Rollbar.
     */
    @Bean
    public Rollbar rollbar() {
        return new Rollbar(getRollbarConfigs());
    }

    private Config getRollbarConfigs() {
        String profile = env.getActiveProfiles()[0];
        boolean enabled = !profile.equals("local");

        // Reference ConfigBuilder.java for all the properties you can set for Rollbar
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .enabled(enabled)
                .environment(profile)
                .build();
    }
}

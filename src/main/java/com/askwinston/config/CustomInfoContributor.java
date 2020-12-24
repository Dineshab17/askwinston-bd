package com.askwinston.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {
    @Autowired
    private Environment env;

    @Value("${askwinston.domain.url}")
    private String domainUrl;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("profiles", env.getActiveProfiles());
        builder.withDetail("domainUrl", domainUrl);
    }
}

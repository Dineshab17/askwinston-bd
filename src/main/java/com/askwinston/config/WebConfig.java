package com.askwinston.config;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.repository.NotificationTemplateRepository;
import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${askwinston.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Autowired
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

    @Bean
    @Autowired
    public ParsingHelper parsingHelper(NotificationTemplateRepository notificationTemplateRepository) {
        return new ParsingHelper(modelMapper(), notificationTemplateRepository);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATH")
                .allowedHeaders("X-Requested-With", "Content-Type", "Origin", "Accept", "Authorization")
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirecting links for routing on FE
        registry.addViewController("//**/{path:[^\\\\.]*}")
                .setViewName("forward:/");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:static/index.html")
                .setCacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS)
                        .mustRevalidate())
                .setCacheControl(CacheControl.noCache())
                .setCacheControl(CacheControl.noStore())
                .setCachePeriod(0);
    }
}

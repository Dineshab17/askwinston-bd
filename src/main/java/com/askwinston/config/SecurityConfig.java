package com.askwinston.config;

import com.askwinston.web.secuity.AuthenticationTokenFilter;
import com.askwinston.web.secuity.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize->
                                authorize.requestMatchers(HttpMethod.GET, "/").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/index.html").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/index.html").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/sitemap.xml").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/robots.txt").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/favicon.jpg").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/*.js").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/static/**").permitAll()
//                 Opening links for routing on FE
                                        .requestMatchers(HttpMethod.GET, "/reset-password/*").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/faq").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/cabinet").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/profile").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/orders").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/shop").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/how-it-works").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/learn").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/messages").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/about-us").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/why-winston").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/login").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/terms-of-use").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/privacy-policy").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/contact-us").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/contact-us-record").permitAll() //

                                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/swagger.json").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/webjars/**").permitAll() //

                                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/user/forgot-password/*").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/user/reset-password/*").permitAll()
                                        // .antMatchers(HttpMethod.GET, "/order/promo-code/*").permitAll() //
                                        .requestMatchers(HttpMethod.POST, "/patient").permitAll() // WASNT COMMENTED
                                        .requestMatchers(HttpMethod.PUT, "/patient/*").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/product").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/product/*").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/token/*/validate").permitAll() //
                                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/stay-connected").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/contact-us-record").permitAll()

                                        .requestMatchers(HttpMethod.GET, "/document").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/document/*").permitAll() //
                                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                        .requestMatchers("/unsecure/**").permitAll().anyRequest().authenticated()
                )
                .addFilterBefore(new AuthenticationTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}

package com.askwinston.config;

import com.askwinston.web.secuity.AuthenticationTokenFilter;
import com.askwinston.web.secuity.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/index.html").permitAll()
                .antMatchers(HttpMethod.GET, "/sitemap.xml").permitAll()
                .antMatchers(HttpMethod.GET, "/robots.txt").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.jpg").permitAll()
                .antMatchers(HttpMethod.GET, "/*.js").permitAll()
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()

                // Opening links for routing on FE
                .antMatchers(HttpMethod.GET, "/reset-password/*").permitAll() //
                .antMatchers(HttpMethod.GET, "/faq").permitAll() //
                .antMatchers(HttpMethod.GET, "/cabinet").permitAll() //
                .antMatchers(HttpMethod.GET, "/profile").permitAll() //
                .antMatchers(HttpMethod.GET, "/orders").permitAll() //
                .antMatchers(HttpMethod.GET, "/shop").permitAll() //
                .antMatchers(HttpMethod.GET, "/how-it-works").permitAll() //
                .antMatchers(HttpMethod.GET, "/learn").permitAll() //
                .antMatchers(HttpMethod.GET, "/messages").permitAll() //
                .antMatchers(HttpMethod.GET, "/about-us").permitAll() //
                .antMatchers(HttpMethod.GET, "/why-winston").permitAll() //
                .antMatchers(HttpMethod.GET, "/login").permitAll() //
                .antMatchers(HttpMethod.GET, "/terms-of-use").permitAll() //
                .antMatchers(HttpMethod.GET, "/privacy-policy").permitAll() //
                .antMatchers(HttpMethod.GET, "/contact-us").permitAll() //
                .antMatchers(HttpMethod.GET, "/contact-us-record").permitAll() //

                .antMatchers(HttpMethod.GET, "/actuator/**").permitAll() //
                .antMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll() //
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll() //
                .antMatchers(HttpMethod.GET, "/swagger.json").permitAll() //
                .antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll() //
                .antMatchers(HttpMethod.GET, "/webjars/**").permitAll() //

                .antMatchers(HttpMethod.POST, "/auth/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user/forgot-password/*").permitAll()
                .antMatchers(HttpMethod.POST, "/user/reset-password/*").permitAll()
                // .antMatchers(HttpMethod.GET, "/order/promo-code/*").permitAll() //
                .antMatchers(HttpMethod.POST, "/patient").permitAll() // WASNT COMMENTED
                .antMatchers(HttpMethod.PUT, "/patient/*").permitAll().antMatchers(HttpMethod.GET, "/product")
                .permitAll() //
                .antMatchers(HttpMethod.GET, "/product/*").permitAll() //
                .antMatchers(HttpMethod.GET, "/token/*/validate").permitAll() //
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll().antMatchers(HttpMethod.POST, "/stay-connected")
                .permitAll().antMatchers(HttpMethod.POST, "/contact-us-record").permitAll()

                .antMatchers(HttpMethod.GET, "/document").permitAll() //
                .antMatchers(HttpMethod.GET, "/document/*").permitAll() //
                .antMatchers(HttpMethod.GET, "/**").permitAll()

                .antMatchers("/unsecure/**").permitAll() //
                .anyRequest().authenticated().and()
                .addFilterBefore(new AuthenticationTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}

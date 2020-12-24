package com.askwinston.web.secuity;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private static final String HEADER = "Authorization";
    private static final String HEADER_BEARER_PREFIX = "Bearer";
    private final JwtService jwtService;

    public AuthenticationTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader(HEADER);
        if (!StringUtils.isEmpty(token) && token.startsWith(HEADER_BEARER_PREFIX)) {
            token = token.replace(HEADER_BEARER_PREFIX, "").trim();
            try {
                AwUserPrincipal userPrincipal = jwtService.parseToken(token);
                PreAuthenticatedAuthenticationToken authentication =
                        new PreAuthenticatedAuthenticationToken(userPrincipal, null,
                                Arrays.asList(new SimpleGrantedAuthority(userPrincipal.getAuthority())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                // SecurityContext is not set up with any Authentication, so further processing chain.doFilter()
                // will be successful only if the endpoint is not secured according Spring Security configuration
            }
        }
        chain.doFilter(request, response);
    }

}

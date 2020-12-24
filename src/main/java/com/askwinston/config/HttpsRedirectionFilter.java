package com.askwinston.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(0)
@Slf4j
public class HttpsRedirectionFilter implements Filter {

    @Value("${askwinston.domain.url}")
    private String askwinstonDomainUrl;

    @Value("${askwinston.domain.redirect.enabled}")
    private boolean redirectEnabled;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String redirectUrl = calculateRedirect((HttpServletRequest) servletRequest);
        if (redirectEnabled && StringUtils.hasText(redirectUrl)) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.sendRedirect(redirectUrl);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private String calculateRedirect(HttpServletRequest req) {
        String redirectUrl = null;
        String headerXForwardedProto = req.getHeader("x-forwarded-proto");
        String headerHost = req.getHeader("host");
        if (StringUtils.hasText(headerXForwardedProto) && StringUtils.hasText(headerHost)) {
            String actualDomainUrl = headerXForwardedProto + "://" + headerHost;
            if (!askwinstonDomainUrl.startsWith(actualDomainUrl)) {
                String queryString = getQueryString(req);
                redirectUrl = askwinstonDomainUrl + req.getRequestURI() + queryString;
                log.info("Original request URL: [" + req.getRequestURL() + queryString +
                        "], calculated redirect: [" + redirectUrl + "]");
            }
        }
        return redirectUrl;
    }

    private String getQueryString(HttpServletRequest req) {
        String queryString = "";
        if (req.getQueryString() != null && req.getQueryString().length() > 0) {
            queryString = "?" + req.getQueryString();
        }
        return queryString;
    }

}

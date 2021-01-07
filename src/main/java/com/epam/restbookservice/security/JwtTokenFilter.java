package com.epam.restbookservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class JwtTokenFilter implements Filter {

    public static final String BEARER_KEYWORD = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final LibraryUserDetailsService userDetailsService;

    public JwtTokenFilter(LibraryUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String headerValue = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION_HEADER);

        getBearerToken(headerValue)
                .flatMap(userDetailsService::loadUserByJwtToken)
                .ifPresent(userDetails ->
                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(new PreAuthenticatedAuthenticationToken(userDetails, "", userDetails.getAuthorities())));

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Optional<String> getBearerToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith(BEARER_KEYWORD)) {
            return Optional.of(headerValue.replace(BEARER_KEYWORD, "").trim());
        }

        return Optional.empty();
    }
}

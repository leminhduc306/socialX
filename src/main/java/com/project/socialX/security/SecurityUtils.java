package com.project.socialX.security;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.socialX.web.rest.errors.MessageCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author SlowV ❤ H3yae
 * @since 9/2/2025 - 13:20
 */

@UtilityClass
public class SecurityUtils {
    public static void responseFailCredential(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        new ObjectMapper()
                .writeValue(response.getOutputStream(), new MessageCode(String.valueOf(status.value()), message));
        response.flushBuffer();
    }

    public static Optional<String> getCurrentUserLogin() {
        final var securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    public static List<? extends GrantedAuthority> getCurrentUserAuthorities() {
        final var securityContext = SecurityContextHolder.getContext();
        final var authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return new ArrayList<>(userDetails.getAuthorities());
        }
        return Collections.emptyList();
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            if(authentication.getPrincipal().equals("anonymousUser")) {
                return null;
            }
            return (String) authentication.getPrincipal();
        }
        return null;
    }

//    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
//        final var refreshTokenCookie = new Cookie(name, value);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true);
//        refreshTokenCookie.setPath("/**");
//        refreshTokenCookie.setMaxAge(maxAge); // Seconds
//        response.addCookie(refreshTokenCookie);
//    }

}

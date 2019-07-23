package de.uni.swt.spring.app.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.uni.swt.spring.ui.utils.SwtConst.*;

public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        String targetUrl = "";
        if (role.equalsIgnoreCase("[Dozent]")) {
            targetUrl = "/" + PAGE_GRUPPEN;
        } else if (role.equalsIgnoreCase("[Student]")) {
            targetUrl = "/" + PAGE_TEAMS_GRUPPEN;
        } else if (role.equalsIgnoreCase("[Admin]")) {
            targetUrl = "/" + PAGE_ADMIN;
        }
        return targetUrl;
    }
}

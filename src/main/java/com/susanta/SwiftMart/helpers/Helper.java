package com.susanta.SwiftMart.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {
    public static String getEmailOfLoggedInUser(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {

            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oAuth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";
            // if sign in with google
            if (clientId.equals("google")) {
                username = oAuth2User.getAttribute("email").toString();
            }
            // if sign in with github
            else if (clientId.equals("github")) {
                username = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email")
                        : oAuth2User.getAttribute("login") + "@gmail.com";
            }
            return username;
        } else {
            return authentication.getName();
        }

    }
}

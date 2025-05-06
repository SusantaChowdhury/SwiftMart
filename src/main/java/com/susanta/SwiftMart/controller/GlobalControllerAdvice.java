package com.susanta.SwiftMart.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute
    public void clearMessage(HttpSession session, HttpServletRequest request) {
        // Only clear the message if it has already been displayed
        String currentUri = request.getRequestURI();
        if (!currentUri.equals("/login")) {
            session.removeAttribute("message");
        }
    }
}

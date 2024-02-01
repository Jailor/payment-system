package com.team1.paymentsystem.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

@Log
class FilterLogging{
    public static void logRequestHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request headers for ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            sb.append("Request Header: " + name + " = " + request.getHeader(name) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

    public static void logSessionAttributes(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Session attributes for ").append(request.getSession().hashCode()).append(":{\r\n");
        Enumeration<String> names = request.getSession().getAttributeNames();
        while (names.hasMoreElements()) {
            String att = names.nextElement();
            sb.append("Session attribute: " + att + " = " + request.getSession().getAttribute(att) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

    public static void logRequestParameters(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request parameters for  ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Map<String, String[]> s = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : s.entrySet()) {
            sb.append("Request parameter : " + entry.getKey() + "=" + Arrays.toString(entry.getValue()) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }
    public static void logRequestAttributes(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n Request attributes for  ").append(request.getRequestURI()).append(" with method: ").append(request.getMethod()).append(":{\r\n");
        Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            String att = names.nextElement();
            sb.append("Request attribute: " + att + " = " + request.getAttribute(att) + "\r\n");
        }
        sb.append("}");
        log.info(String.valueOf(sb));
    }

}
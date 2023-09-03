package com.team1.paymentsystem.controllers;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtils {
    public static String getUsername(HttpServletRequest request){
        String username = request.getHeader("username");
        if(username == null){
            throw new RuntimeException("Username not found in request header");
            //username =  "admin";
        }
        return username;
    }
}

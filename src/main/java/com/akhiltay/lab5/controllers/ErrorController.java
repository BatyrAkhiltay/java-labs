package com.akhiltay.lab5.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMsg = "";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            switch(statusCode) {
                case 404:
                    errorMsg = "Page not found";
                    break;
                case 403:
                    errorMsg = "Access denied";
                    break;
                case 500:
                    errorMsg = "Internal server error";
                    break;
                default:
                    errorMsg = "An unexpected error occurred";
            }
        }

        model.addAttribute("errorMessage", errorMsg);
        return "error";
    }
}
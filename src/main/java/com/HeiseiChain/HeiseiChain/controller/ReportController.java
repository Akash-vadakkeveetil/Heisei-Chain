package com.HeiseiChain.HeiseiChain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/blockchain")
public class ReportController {

    @Autowired

    // Display the report generation page
    @GetMapping("/report")
    public String getReportPage() {
        return "report"; // Thymeleaf template name (report.html)
    }

    // Handle report generation based on date and time range


}

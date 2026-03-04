package com.example.controller;

import com.example.service.IncomeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/income")
@RequiredArgsConstructor
@CrossOrigin("*")
public class IncomeReportController {

    private final IncomeReportService incomeReportService;

    // =========================
    // Download Excel (GET)
    // =========================
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam Long profileId) throws Exception {

        byte[] file = incomeReportService.generateExcel(profileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=income_details.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    // =========================
    // Send Email (POST)
    // =========================
    @PostMapping("/email")
    public ResponseEntity<String> email(
            @RequestParam Long profileId,
            @RequestParam String email) throws Exception {

        incomeReportService.sendEmail(profileId, email);

        return ResponseEntity.ok("Email sent successfully");
    }
}
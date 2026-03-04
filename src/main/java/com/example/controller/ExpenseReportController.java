package com.example.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ExpenseReportService;
import com.example.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/expenses")
@RequiredArgsConstructor
public class ExpenseReportController {
	private final ExpenseReportService expenseReportService;
	
	 @GetMapping("/download")
	    public ResponseEntity<byte[]> download(
	            @RequestParam Long profileId) throws Exception {

	        byte[] file = expenseReportService.generateExcel(profileId);

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=expense_details.xlsx")
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

	    	expenseReportService.sendEmail(profileId, email);

	        return ResponseEntity.ok("Email sent successfully");
	    }

}

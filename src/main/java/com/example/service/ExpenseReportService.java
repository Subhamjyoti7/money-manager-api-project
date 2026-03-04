package com.example.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.entity.ExpenseEntity;
import com.example.entity.IncomeEntity;
import com.example.repository.ExpenseRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseReportService {
	
	 private final ExpenseRepository expenseRepository;
	    private final RestTemplate restTemplate;

	    @Value("${BREVO_API_KEY}")
	    private String brevoApiKey;

	    @Value("${BREVO_FROM_EMAIL}")
	    private String fromEmail;

	    @Value("${BREVO_FROM_NAME}")
	    private String fromName;
	    
	    public byte[] generateExcel(Long profileId) throws Exception {

	        List<ExpenseEntity> expenses =
	                expenseRepository.findByProfileId(profileId);

	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Income Details");

	        int rowNum = 0;

	        // Header Row
	        Row header = sheet.createRow(rowNum++);
	        header.createCell(0).setCellValue("S.No");
	        header.createCell(1).setCellValue("Name");
	        header.createCell(2).setCellValue("Category");
	        header.createCell(3).setCellValue("Amount");
	        header.createCell(4).setCellValue("Date");

	        int index = 1;

	        for (ExpenseEntity expense : expenses) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(index++);
	            row.createCell(1).setCellValue(expense.getName());
	            row.createCell(2).setCellValue(expense.getCategory().getName());
	            row.createCell(3).setCellValue(expense.getAmount().doubleValue());
	            row.createCell(4).setCellValue(expense.getDate().toString());
	        }

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();

	        return outputStream.toByteArray();
	    }
	    
	    
	    
	    public void sendEmail(Long profileId, String toEmail) throws Exception {

	        byte[] file = generateExcel(profileId);
	        String base64File = Base64.getEncoder().encodeToString(file);

	        String url = "https://api.brevo.com/v3/smtp/email";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("api-key", brevoApiKey);

	        Map<String, Object> body = Map.of(
	                "sender", Map.of(
	                        "name", fromName,
	                        "email", fromEmail
	                ),
	                "to", List.of(Map.of("email", toEmail)),
	                "subject", "Expense Report",
	                "htmlContent", "<p>Please find attached your expense report.</p>",
	                "attachment", List.of(
	                        Map.of(
	                                "content", base64File,
	                                "name", "expense_details.xlsx"
	                        )
	                )
	        );

	        HttpEntity<Map<String, Object>> request =
	                new HttpEntity<>(body, headers);

	        restTemplate.postForEntity(url, request, String.class);
	    }


}

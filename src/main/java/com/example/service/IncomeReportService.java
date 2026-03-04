package com.example.service;

import com.example.entity.IncomeEntity;
import com.example.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IncomeReportService {

    private final IncomeRepository incomeRepository;
    private final RestTemplate restTemplate;

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${BREVO_FROM_EMAIL}")
    private String fromEmail;

    @Value("${BREVO_FROM_NAME}")
    private String fromName;

    // =========================
    // 1️⃣ Generate Excel
    // =========================
    public byte[] generateExcel(Long profileId) throws Exception {

        List<IncomeEntity> incomes =
                incomeRepository.findByProfileId(profileId);

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

        for (IncomeEntity income : incomes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            row.createCell(1).setCellValue(income.getName());
            row.createCell(2).setCellValue(income.getCategory().getName());
            row.createCell(3).setCellValue(income.getAmount().doubleValue());
            row.createCell(4).setCellValue(income.getDate().toString());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    // =========================
    // 2️⃣ Send Email (Brevo)
    // =========================
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
                "subject", "Income Report",
                "htmlContent", "<p>Please find attached your income report.</p>",
                "attachment", List.of(
                        Map.of(
                                "content", base64File,
                                "name", "income_details.xlsx"
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}
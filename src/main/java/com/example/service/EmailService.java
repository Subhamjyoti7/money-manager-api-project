package com.example.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${BREVO_FROM_EMAIL}")
    private String fromEmail;

    @Value("${BREVO_FROM_NAME}")
    private String fromName;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String to, String subject, String body) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", fromName);

            Map<String, Object> toMap = new HashMap<>();
            toMap.put("email", to);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender);
            payload.put("to", new Object[]{toMap});
            payload.put("subject", subject);
            payload.put("htmlContent", "<p>" + body + "</p>");

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(BREVO_URL, request, String.class);

            System.out.println("✅ Brevo API email sent to " + to);

        } catch (Exception e) {
            System.err.println("❌ Brevo API email FAILED: " + e.getMessage());
        }
    }
}
package com.batuaa.transactionservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
@Slf4j
@Service
public class EmailService {
@Autowired
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
@Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendTransactionEmail(String toEmail, Map<String, Object> model) throws MessagingException {
        // 1. Prepare the Thymeleaf context
        Context context = new Context();
        context.setVariables(model);

        // 2. Render HTML from the template (src/main/resources/templates/transaction-email.html)
        String htmlContent = templateEngine.process("transaction-email", context);

        // 3. Create MIME message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Transaction Details");
        helper.setText(htmlContent, true); // true = HTML content

        // 4. Send the email safely
        try {
            mailSender.send(message);
           log.info("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
           log.error(" Failed to send email: " + e.getMessage());
            throw e;
        }
    }
}

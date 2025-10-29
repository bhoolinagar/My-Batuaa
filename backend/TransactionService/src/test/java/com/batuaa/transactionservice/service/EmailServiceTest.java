package com.batuaa.transactionservice.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

@TestPropertySource(properties = "logging.level.org.springframework.boot.autoconfigure=INFO")
@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

//
//    @Test
//   public void testSendEmail() throws MessagingException {
//        Map<String, Object> model = new HashMap<>();
//        model.put("name", "User");
//        model.put("transactionId", "90129");
//        model.put("amount", 100);
//        model.put("date", "12/10/2025");
//        model.put("receiverEmail", "bhoolinagar123@gmail.com");
//        emailService.sendTransactionEmail("bhoolinagar123@gmail.com", model);
//    }
}

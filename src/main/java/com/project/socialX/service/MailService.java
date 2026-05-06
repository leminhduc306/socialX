package com.project.socialX.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    @Value("${spring.mail.username}")
    private String email;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("otp", otp);

            String html = templateEngine.process("otp-email", context);
            helper.setFrom(email);
            helper.setTo(toEmail);
            helper.setSubject("[SocialX] Mã OTP Đổi Mật Khẩu");
            helper.setText(html, true);

            javaMailSender.send(message);
            log.info("Email OTP đã được gửi đến: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email OTP đến {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email OTP, vui lòng thử lại sau.");
        }
    }
}

package com.example.learning_api.service.common;

import com.example.learning_api.model.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "verify_email_template";
    public static final String CODE_TEMPLATE = "verify_code_template";
    public static final String USER_BLOCKED_TEMPLATE = "user_blocked_bc_boom_order";
    public static final String TEXT_HTML_ENCONDING = "text/html";
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;


    @Async
    public void sendHtmlVerifyCodeToRegister(String to, String code,String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("body", code);
            context.setVariable("toMail", to);
            String htmlContent = templateEngine.process("email-template", context);


            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject(subject);



            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException( "Error while sending email");
        }
    }

    @Async
    public void sendUserBlocked(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariables(Map.of( "user_name", userName));
            String text = templateEngine.process(USER_BLOCKED_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("LOCK SHOPFEE ACCOUNT");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            emailSender.send(message);

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }


    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }
}
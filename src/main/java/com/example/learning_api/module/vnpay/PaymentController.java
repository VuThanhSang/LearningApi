package com.example.learning_api.module.vnpay;

import com.example.learning_api.dto.response.cart.PaymentsResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TransactionEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final TransactionRepository transactionRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    private final JavaMailSender javaMailSender;
    private final ClassRoomRepository classroomRepository;
    @Value("${client.url.payment-status}")
    private String clientRedirectUrl;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @PostMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request, @RequestBody PaymentRequest body) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request, body));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String transactionRef = request.getParameter("vnp_TxnRef"); // Transaction reference from VNPay
        String status = request.getParameter("vnp_ResponseCode");
        List<TransactionEntity> transactionEntities = transactionRepository.findByTransactionRef(transactionRef);

        if (status.equals("00")) { // Giao dịch thành công
            for (TransactionEntity transactionEntity : transactionEntities) {
                transactionEntity.setStatus("SUCCESS");
                transactionRepository.save(transactionEntity);

                StudentEnrollmentsEntity studentEnrollmentsEntity = new StudentEnrollmentsEntity();
                StudentEntity studentEntity = studentRepository.findByUserId(transactionEntity.getUserId());
                studentEnrollmentsEntity.setStudentId(studentEntity.getId());
                studentEnrollmentsEntity.setClassroomId(transactionEntity.getClassroomId());
                studentEnrollmentsEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                studentEnrollmentsRepository.save(studentEnrollmentsEntity);
                ClassRoomEntity classRoomEntity = classroomRepository.findById(transactionEntity.getClassroomId()).get();
                // Send email notification
                sendEnrollmentSuccessEmail(studentEntity.getUser().getEmail(), classRoomEntity.getName(), transactionRef);
            }
            // Chuyển hướng kèm tham số
            response.sendRedirect(clientRedirectUrl + "?status=success&transactionRef=" + transactionRef);
        } else { // Giao dịch thất bại
            response.sendRedirect(clientRedirectUrl + "?status=failed&transactionRef=" + transactionRef);
        }
    }
    @Async
    private void sendEnrollmentSuccessEmail(String toMail, String courseName, String transactionRef) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("courseName", courseName);
            context.setVariable("transactionRef", transactionRef);
            String htmlContent = templateEngine.process("course-enrollment-success", context);

            mimeMessageHelper.setFrom(mailFrom);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject("Course Enrollment Success");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException("Error while sending email");
        }
    }

    @GetMapping("/payment-status/{transactionRef}")
    public ResponseEntity<ResponseAPI<PaymentsResponse>> getPaymentStatus(@PathVariable String transactionRef) {
        try {
            PaymentsResponse paymentsResponse = paymentService.getPaymentStatus(transactionRef);
            ResponseAPI<PaymentsResponse> res = ResponseAPI.<PaymentsResponse>builder()
                    .timestamp(new Date())
                    .message("Get payment status successfully")
                    .data(paymentsResponse)
                    .build();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            ResponseAPI<PaymentsResponse> res = ResponseAPI.<PaymentsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

}
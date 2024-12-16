package com.example.learning_api.module.vnpay;

import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TransactionEntity;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final TransactionRepository transactionRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    @Value("${client.url.payment-status}")
    private String clientRedirectUrl;
    @PostMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request, @RequestBody PaymentRequest body) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request,body));
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
                studentEnrollmentsEntity.setCreatedAt(transactionEntity.getCreatedAt());
                studentEnrollmentsEntity.setUpdatedAt(transactionEntity.getUpdatedAt());
                studentEnrollmentsRepository.save(studentEnrollmentsEntity);
            }
            // Chuyển hướng kèm tham số
            response.sendRedirect(clientRedirectUrl + "?status=success&txnRef=" + transactionRef);
        } else { // Giao dịch thất bại
            response.sendRedirect(clientRedirectUrl + "?status=failed&txnRef=" + transactionRef);
        }
    }
}
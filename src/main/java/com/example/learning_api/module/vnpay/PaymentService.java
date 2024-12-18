package com.example.learning_api.module.vnpay;

import com.example.learning_api.dto.response.cart.PaymentsResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TransactionEntity;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final TransactionRepository paymentRepository;
    private final ClassRoomRepository classRoomRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, @RequestBody PaymentRequest body) {
        String bankCode = request.getParameter("bankCode");

        String transactionRef = UUID.randomUUID().toString();
        long totalAmount = 0;
        for (String classroomId : body.getClassroomIds()) {
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId).orElse(null);
            StudentEntity studentEntity = studentRepository.findByUserId(body.getUserId());
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId(studentEntity.getId(), classroomId);
            if (classRoomEntity!=null&&studentEnrollmentsEntity==null){
                TransactionEntity transactionEntity = paymentRepository.findByUserIdAndClassroomIdAndStatus(body.getUserId(), classroomId, "PENDING");
                if (transactionEntity != null) {
                    transactionEntity.setTransactionRef(transactionRef);
                    transactionEntity.setPaymentMethod(bankCode);
                    transactionEntity.setAmount(Long.valueOf(classRoomEntity.getPrice()));
                    paymentRepository.save(transactionEntity);
                    totalAmount += classRoomEntity.getPrice();
                    continue;
                }else{
                    transactionEntity = new TransactionEntity();
                }
                transactionEntity.setUserId(body.getUserId());
                transactionEntity.setClassroomId(classroomId);
                transactionEntity.setAmount(Long.valueOf(classRoomEntity.getPrice()));
                transactionEntity.setStatus("PENDING");
                transactionEntity.setTransactionRef(transactionRef);
                transactionEntity.setPaymentMethod(bankCode);
                transactionEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                transactionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                paymentRepository.save(transactionEntity);
                totalAmount += classRoomEntity.getPrice();
            }
        }
        long amount = totalAmount * 100L;



        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_TxnRef", transactionRef);
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }


    public PaymentsResponse getPaymentStatus(String transactionRef) {
        List<TransactionEntity> transactionEntities = paymentRepository.findByTransactionRef(transactionRef);
        long amountPaid = 0;
        for (TransactionEntity transactionEntity : transactionEntities) {
            amountPaid += transactionEntity.getAmount();
        }
        List<ClassRoomEntity> classRooms = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(transactionEntity.getClassroomId()).orElse(null);
            if (classRoomEntity != null) {
                classRooms.add(classRoomEntity);
            }
        }
        PaymentsResponse paymentsResponse = new PaymentsResponse();
        paymentsResponse.setAmountPaid(amountPaid);
        paymentsResponse.setClassRooms(classRooms);
        paymentsResponse.setPaymentMethod("VNPay");
        paymentsResponse.setTransactionRef(transactionRef);
        paymentsResponse.setDate(new Date().toString());
        return paymentsResponse;
    }


}
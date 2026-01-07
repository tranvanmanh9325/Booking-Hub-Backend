package com.example.booking.controller;

import com.example.booking.dto.PartnershipRequest;
import com.example.booking.service.EmailService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/partnership")
@CrossOrigin(origins = "*")
public class PartnershipController {

    private final EmailService emailService;

    public PartnershipController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitPartnershipRequest(
            @Valid @RequestBody PartnershipRequest request) {
        try {
            emailService.sendPartnershipEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getCompany(),
                    request.getPartnershipType(),
                    request.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message",
                    "Yêu cầu hợp tác đã được gửi thành công. Chúng tôi sẽ liên hệ với bạn sớm nhất có thể.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đã có lỗi xảy ra khi gửi yêu cầu. Vui lòng thử lại sau.");

            return ResponseEntity.status(500).body(response);
        }
    }
}
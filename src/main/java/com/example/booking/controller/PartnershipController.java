package com.example.booking.controller;

import com.example.booking.dto.PartnershipRequest;
import com.example.booking.service.EmailService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý các yêu cầu hợp tác.
 * Cho phép đối tác gửi yêu cầu liên hệ.
 */
@RestController
@RequestMapping("/api/v1/partnerships")
@CrossOrigin(origins = "*")
@Tag(name = "Partnerships", description = "Partnership request APIs")
public class PartnershipController {

    private final EmailService emailService;

    public PartnershipController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Gửi yêu cầu hợp tác.
     * 
     * @param request Thông tin yêu cầu hợp tác
     * @return ResponseEntity thông báo kết quả
     */
    @Operation(summary = "Submit partnership request", description = "Submits a request for partnership.")
    @ApiResponse(responseCode = "200", description = "Request submitted successfully")
    @ApiResponse(responseCode = "500", description = "Error sending request")
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
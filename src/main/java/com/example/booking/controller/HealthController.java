package com.example.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller kiểm tra trạng thái sức khỏe của hệ thống.
 * Dùng để monitoring và verify application status.
 */
@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*")
@Tag(name = "Health", description = "System health check API")
public class HealthController {

    /**
     * Kiểm tra trạng thái hệ thống.
     * 
     * @return ResponseEntity chứa trạng thái "UP" nếu hệ thống hoạt động bình
     *         thường
     */
    @Operation(summary = "Health check", description = "Returns the status of the application.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Booking API is running");
        return ResponseEntity.ok(response);
    }
}
package com.example.booking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.partnership.recipient-email:manhtrana1k45tl@gmail.com}")
    private String recipientEmail;
    
    public void sendPartnershipEmail(String name, String email, String phone, 
                                    String company, String partnershipType, String message) {
        try {
            if (mailSender == null) {
                log.error("JavaMailSender is null - email configuration may be missing");
                throw new RuntimeException("Email service is not configured properly");
            }
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Yêu Cầu Hợp Tác Mới - " + name);
            
            String emailContent = buildEmailContent(name, email, phone, company, partnershipType, message);
            helper.setText(emailContent, true);
            
            mailSender.send(mimeMessage);
            log.info("Partnership email sent successfully to {}", recipientEmail);
            
        } catch (MessagingException e) {
            log.error("Error sending partnership email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error in sendPartnershipEmail: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    private String buildEmailContent(String name, String email, String phone, 
                                    String company, String partnershipType, String message) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        content.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        content.append("<h2 style='color: #2563eb; border-bottom: 2px solid #2563eb; padding-bottom: 10px;'>Yêu Cầu Hợp Tác Mới</h2>");
        content.append("<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-top: 20px;'>");
        
        content.append("<p><strong>Họ và Tên:</strong> ").append(name).append("</p>");
        content.append("<p><strong>Email:</strong> <a href='mailto:").append(email).append("'>").append(email).append("</a></p>");
        content.append("<p><strong>Số Điện Thoại:</strong> <a href='tel:").append(phone).append("'>").append(phone).append("</a></p>");
        
        if (company != null && !company.trim().isEmpty()) {
            content.append("<p><strong>Tên Công Ty / Doanh Nghiệp:</strong> ").append(company).append("</p>");
        }
        
        content.append("<p><strong>Loại Hợp Tác:</strong> ").append(getPartnershipTypeName(partnershipType)).append("</p>");
        
        if (message != null && !message.trim().isEmpty()) {
            content.append("<div style='margin-top: 20px; padding-top: 20px; border-top: 1px solid #dee2e6;'>");
            content.append("<p><strong>Thông Điệp:</strong></p>");
            content.append("<p style='white-space: pre-wrap;'>").append(message).append("</p>");
            content.append("</div>");
        }
        
        content.append("</div>");
        content.append("<p style='margin-top: 20px; color: #6c757d; font-size: 12px;'>Email này được gửi tự động từ hệ thống Booking Hub.</p>");
        content.append("</div></body></html>");
        
        return content.toString();
    }
    
    private String getPartnershipTypeName(String type) {
        return switch (type) {
            case "hotel" -> "Khách Sạn & Resort";
            case "cinema" -> "Rạp Chiếu Phim";
            case "restaurant" -> "Nhà Hàng & Quán Ăn";
            case "attraction" -> "Khu Vui Chơi & Giải Trí";
            case "travel" -> "Công Ty Du Lịch";
            case "other" -> "Đối Tác Khác";
            default -> type;
        };
    }
}

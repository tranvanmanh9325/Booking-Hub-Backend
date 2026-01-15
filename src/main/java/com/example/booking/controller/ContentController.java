package com.example.booking.controller;

import com.example.booking.model.Content;
import com.example.booking.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.Authentication;
import com.example.booking.service.BookingService;
import com.example.booking.model.ContentBooking;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContentController {

    private final ContentService contentService;
    private final BookingService bookingService;

    @GetMapping
    public List<Content> getAllContent() {
        return contentService.getAllContent();
    }

    @PostMapping
    public Content addContent(@RequestBody @NonNull Content content, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        return contentService.addContent(content, email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(@PathVariable @NonNull Long id,
            @RequestBody @NonNull Content contentDetails) {
        return ResponseEntity.ok(contentService.updateContent(id, contentDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable @NonNull Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/partner/bookings")
    public ResponseEntity<List<ContentBooking>> getPartnerBookings(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getContentBookingsForPartner(authentication.getName()));
    }
}
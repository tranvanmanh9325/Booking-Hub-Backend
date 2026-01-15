package com.example.booking.controller;

import com.example.booking.model.Content;
import com.example.booking.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend access
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public List<Content> getAllContent() {
        return contentService.getAllContent();
    }

    @PostMapping
    public Content addContent(@RequestBody @NonNull Content content) {
        return contentService.addContent(content);
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
}
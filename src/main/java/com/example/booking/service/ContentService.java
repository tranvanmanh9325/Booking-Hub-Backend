package com.example.booking.service;

import com.example.booking.model.Content;
import com.example.booking.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Content addContent(@NonNull Content content) {
        return contentRepository.save(content);
    }

    public Content updateContent(@NonNull Long id, @NonNull Content contentDetails) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));

        content.setName(contentDetails.getName());
        content.setType(contentDetails.getType());
        content.setPrice(contentDetails.getPrice());
        content.setStatus(contentDetails.getStatus());

        return contentRepository.save(content);
    }

    public void deleteContent(@NonNull Long id) {
        if (!contentRepository.existsById(id)) {
            throw new RuntimeException("Content not found with id: " + id);
        }
        contentRepository.deleteById(id);
    }
}

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

    private final com.example.booking.repository.UserRepository userRepository;

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public List<Content> getContentByPartner(String email) {
        return contentRepository.findByOwnerEmail(email);
    }

    public Content addContent(@NonNull Content content, String ownerEmail) {
        if (ownerEmail != null) {
            com.example.booking.model.User owner = userRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + ownerEmail));
            content.setOwner(owner);
        }
        return contentRepository.save(content);
    }

    public Content updateContent(@NonNull Long id, @NonNull Content contentDetails) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));

        content.setName(contentDetails.getName());
        content.setType(contentDetails.getType());
        content.setPrice(contentDetails.getPrice());
        content.setDescription(contentDetails.getDescription());
        content.setThumbnail(contentDetails.getThumbnail());
        content.setImages(contentDetails.getImages());
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
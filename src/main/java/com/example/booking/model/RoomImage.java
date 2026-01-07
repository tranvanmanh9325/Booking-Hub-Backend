package com.example.booking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_images")
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPrimary == null) {
            isPrimary = false;
        }
    }

    public RoomImage() {
    }

    public RoomImage(Long id, Room room, String imageUrl, Boolean isPrimary, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoomImage roomImage = (RoomImage) o;
        return java.util.Objects.equals(id, roomImage.id) &&
                java.util.Objects.equals(room, roomImage.room) &&
                java.util.Objects.equals(imageUrl, roomImage.imageUrl) &&
                java.util.Objects.equals(isPrimary, roomImage.isPrimary) &&
                java.util.Objects.equals(createdAt, roomImage.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, room, imageUrl, isPrimary, createdAt);
    }

    @Override
    public String toString() {
        return "RoomImage{" +
                "id=" + id +
                ", room=" + room +
                ", imageUrl='" + imageUrl + '\'' +
                ", isPrimary=" + isPrimary +
                ", createdAt=" + createdAt +
                '}';
    }
}
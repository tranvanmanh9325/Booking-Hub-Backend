package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoogleAuthRequest {
    private String email;
    private String name;
    private String picture;
    private String googleId;

    public GoogleAuthRequest() {
    }

    public GoogleAuthRequest(String email, String name, String picture, String googleId) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.googleId = googleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GoogleAuthRequest that = (GoogleAuthRequest) o;
        return java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(picture, that.picture) &&
                java.util.Objects.equals(googleId, that.googleId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(email, name, picture, googleId);
    }

    @Override
    public String toString() {
        return "GoogleAuthRequest{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", googleId='" + googleId + '\'' +
                '}';
    }
}

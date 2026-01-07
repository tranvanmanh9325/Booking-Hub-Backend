package com.example.booking.dto;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
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

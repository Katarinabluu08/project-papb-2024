package com.example.profiluser;

public class Profil {
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private boolean privateProfile;
    private boolean notifications;
    private boolean receiveMessages; // Tambahkan atribut ini

    public Profil() {
        // Default constructor required for calls to DataSnapshot.getValue(Profil.class)
    }

    public Profil(String firstName, String lastName, String email, String bio, boolean privateProfile, boolean notifications) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.bio = bio;
        this.privateProfile = privateProfile;
        this.notifications = notifications;
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        this.privateProfile = privateProfile;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isReceiveMessages() {
        return receiveMessages;
    }

    public void setReceiveMessages(boolean receiveMessages) {
        this.receiveMessages = receiveMessages;
    }
}

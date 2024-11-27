package com.example.profiluser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfilDetail extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName, profileBio;
    private CheckBox profilePrivate, profileNotifications, profileMessages;
    private Button deleteButton, editButton;

    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_detail);

        // Initialize UI elements
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileBio = findViewById(R.id.profile_bio);
        profilePrivate = findViewById(R.id.profile_private);
        profileNotifications = findViewById(R.id.profile_notifications);
        profileMessages = findViewById(R.id.profile_messages);
        deleteButton = findViewById(R.id.delete_button);
        editButton = findViewById(R.id.edit_button);

        // Get profileId from Intent
        profileId = getIntent().getStringExtra("profileId");

        if (profileId != null) {
            loadProfileData(profileId);
        } else {
            Toast.makeText(this, "No profile data found", Toast.LENGTH_SHORT).show();
        }

        // Delete Button
        deleteButton.setOnClickListener(view -> deleteProfile());

        // Edit Button
        editButton.setOnClickListener(view -> editProfile());
    }

    // Load profile data from Firebase
    private void loadProfileData(String profileId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profiles").child(profileId);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Profil profile = task.getResult().getValue(Profil.class);
                if (profile != null) {
                    profileName.setText(profile.getFirstName() + " " + profile.getLastName());
                    profileBio.setText(profile.getBio());
                    profilePrivate.setChecked(profile.isPrivateProfile());
                    profileNotifications.setChecked(profile.isNotifications());
                    profileMessages.setChecked(profile.isReceiveMessages());
                } else {
                    Toast.makeText(this, "Profile data is missing", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfilDetail.this, "Profile not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete profile from Firebase
    private void deleteProfile() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profiles").child(profileId);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfilDetail.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open MainActivity to edit the profile
    private void editProfile() {
        Intent intent = new Intent(ProfilDetail.this, MainActivity.class);
        intent.putExtra("profileId", profileId); // Send profileId to MainActivity to edit
        intent.putExtra("isEditMode", true); // Tambahkan flag edit mode
        startActivity(intent);
    }
}

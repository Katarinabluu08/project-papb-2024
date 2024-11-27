package com.example.profiluser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText firstNameEdit, lastNameEdit, emailEdit, bioEdit;
    private Switch privateProfileSwitch, notificationsSwitch;
    private Button saveButton;

    private DatabaseReference databaseReference;
    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://pamc-72e72-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("profiles");

        // Initialize views
        firstNameEdit = findViewById(R.id.first_name);
        lastNameEdit = findViewById(R.id.last_name);
        emailEdit = findViewById(R.id.email);
        bioEdit = findViewById(R.id.bio);
        privateProfileSwitch = findViewById(R.id.private_profile);
        notificationsSwitch = findViewById(R.id.notifications);
        saveButton = findViewById(R.id.save_button);

        profileId = getIntent().getStringExtra("profileId");
        boolean isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            // Jika dalam mode edit, langsung muat data tanpa redirect
            if (profileId != null) {
                loadProfileData(profileId);
            }
        } else {
            // Jika bukan mode edit, cek apakah ada profil di Firebase
            checkIfProfileExists();
        }

        checkIfProfileExists();

        saveButton.setOnClickListener(view -> saveProfile());
    }

    private void checkIfProfileExists() {
        databaseReference.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && profileId == null) {
                    // Profile exists, redirect to ProfilDetail
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String profileId = snapshot.getKey();
                        Intent intent = new Intent(MainActivity.this, ProfilDetail.class);
                        intent.putExtra("profileId", profileId);
                        startActivity(intent);
                        finish(); // Close MainActivity
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to check profile: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load the existing profile data from Firebase
    private void loadProfileData(String profileId) {
        databaseReference.child(profileId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Profil profile = task.getResult().getValue(Profil.class);
                if (profile != null) {
                    firstNameEdit.setText(profile.getFirstName());
                    lastNameEdit.setText(profile.getLastName());
                    emailEdit.setText(profile.getEmail());
                    bioEdit.setText(profile.getBio());
                    privateProfileSwitch.setChecked(profile.isPrivateProfile());
                    notificationsSwitch.setChecked(profile.isNotifications());
                }
            } else {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save the profile data to Firebase
    private void saveProfile() {
        String firstName = firstNameEdit.getText().toString().trim();
        String lastName = lastNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String bio = bioEdit.getText().toString().trim();
        boolean privateProfile = privateProfileSwitch.isChecked();
        boolean notifications = notificationsSwitch.isChecked();

        if (firstName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // If profileId is available, update the existing profile
        if (profileId != null) {
            Profil updatedProfile = new Profil(firstName, lastName, email, bio, privateProfile, notifications);
            databaseReference.child(profileId).setValue(updatedProfile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the previous activity
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If there's no profileId, create a new profile
            String newProfileId = databaseReference.push().getKey();
            Profil newProfile = new Profil(firstName, lastName, email, bio, privateProfile, notifications);
            assert newProfileId != null;
            databaseReference.child(newProfileId).setValue(newProfile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ProfilDetail.class);
                    intent.putExtra("profileId", newProfileId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

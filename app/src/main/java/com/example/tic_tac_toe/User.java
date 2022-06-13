package com.example.tic_tac_toe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import user_connectivity.Invitation;

@IgnoreExtraProperties
public class User {
    private String id;
    private String email;
    private String fullName;
    private EUserStatus status;
    private boolean isVerified;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class) from Firebase
    }

   public User(String id, String email, String fullName, String status, boolean isVerified) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.status = EUserStatus.valueOf(status);
        this.isVerified = isVerified;
    }

    public User(String id, String email, String fullName, EUserStatus status, boolean isVerified) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.isVerified = isVerified;
    }

    // Usable for constructing offline users, e.g. the system user
    public User(String fullName) {
        this.id = fullName;
        this.email = "";
        this.fullName = fullName;
        this.status = EUserStatus.UNAVAILABLE;
        this.isVerified = true;
    }

    public User(User other){
        this.id = other.id;
        this.email = other.email;
        this.fullName = other.fullName;
        this.status = other.status;
        this.isVerified = other.isVerified;
    }

    public User(DataSnapshot snapshot){
        parseFromDataSnapshot(snapshot);
    }

    public User(Map<String, Object> userDataMap){
        parseUserDataFromHashMap(userDataMap);
    }


    public enum EUserStatus {
        AVAILABLE("AVAILABLE"),
        OFFLINE("OFFLINE"),
        UNAVAILABLE("UNAVAILABLE");
        private final String type;

        EUserStatus(final String type) {
            this.type = type;
        }
        @NonNull
        @Override
        public String toString() {
            return type;
        }
    }


    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(EUserStatus status) {
        this.status = status;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean verified) {
        isVerified = verified;
    }

    @Exclude
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("email", email);
        result.put("fullName", fullName);
        result.put("status", status);
        result.put("isVerified", isVerified);
        return result;
    }

    public void parseFromDataSnapshot(DataSnapshot dataSnapshot) {
        this.id = dataSnapshot.child("id").getValue(String.class);
        this.email = dataSnapshot.child("email").getValue(String.class);
        this.fullName = dataSnapshot.child("fullName").getValue(String.class);
        this.status = EUserStatus.valueOf(dataSnapshot.child("status").getValue(String.class));
        this.isVerified = dataSnapshot.child("isVerified").getValue(Boolean.class);
    }

    public void parseUserDataFromHashMap(Map<String, Object> userDataMap){
        this.id = String.valueOf(userDataMap.get("id"));
        this.email = String.valueOf(userDataMap.get("email"));
        this.fullName = String.valueOf(userDataMap.get("fullName"));
        this.status = EUserStatus.valueOf(String.valueOf(userDataMap.get("status")));
        this.isVerified = (Boolean) userDataMap.get("isVerified");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null)
            throw new NullPointerException();
        if(!(obj instanceof User))
            throw new IllegalArgumentException();
        User other = (User) obj;
        return this.getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                ", isVerified=" + isVerified +
                '}';
    }
}

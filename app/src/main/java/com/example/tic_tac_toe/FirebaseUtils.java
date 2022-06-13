package com.example.tic_tac_toe;

import android.content.res.Resources;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;


import user_connectivity.Invitation;

/** Firebase utils related to the app */
public class FirebaseUtils {
    private final String FIREBASE_URL = "PUT_YOUR_FIREBASE_URL_HERE";
    protected final DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference();
    protected final FirebaseAuth userAuthentication = FirebaseAuth.getInstance();


    /** Handle with first connection after signup (set 'isVerified' to true) too. */
    public void updateCurrentUserIsActiveInDatabase(String userId){
        if(userId == null)
            return;
        databaseReference.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.i("FirebaseUtils", "Error getting data", task.getException());
                }
                else {
                    if(task.getResult().getValue() == null) {
                        Log.e("FirebaseUtils", "Error fetching user from database");
                        return;
                    }
                    Map<String, Object> userDetailsMap = new HashMap<>((HashMap<String, Object>) task.getResult().getValue());
                    User user = new User(userDetailsMap);
                    Commons.setCurrentUser(user);

                    if(!Commons.getCurrentUser().getIsVerified())
                        Commons.getCurrentUser().setIsVerified(true);

                    updateUserInDB(Commons.getCurrentUser());
                    requestMakingUserUnavailableOnDisconnect(userId);
                }
            }
        });
    }

    public void updateUserInDB(User user){
        if(user == null)
            return;
        databaseReference.child("users").child(user.getId()).setValue(user);
    }

    public static void updateUserStatusInDB(User user, User.EUserStatus status){
        if(user == null || status == null)
            return;
        FirebaseUtils firebaseUtils = new FirebaseUtils();
        user.setStatus(status);
        firebaseUtils.updateUserInDB(user);
    }

    private void requestMakingUserUnavailableOnDisconnect(String userId){
        if(userId == null)
            return;
        databaseReference.child("users").child(userId).child("status").onDisconnect().setValue(User.EUserStatus.OFFLINE.toString());
    }

    public void addGameInvitation(Invitation invitation) {
        if(invitation == null)
            return;
        databaseReference.child("invitations").child(invitation.getGuest().getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue() == null && invitation.getHost().getId().equals(Commons.getCurrentUser().getId())) {
                    databaseReference.child("invitations").child(invitation.getGuest().getId()).setValue(invitation);
                }
            }
        });
    }

    public void updateGameInvitation(Invitation invitation) {
        if(invitation == null)
            return;
        databaseReference.child("invitations").child(invitation.getGuest().getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue() != null && invitation.getGuest().getId().equals(Commons.getCurrentUser().getId())) {
                    databaseReference.child("invitations").child(invitation.getGuest().getId()).setValue(invitation);
                }
            }
        });
    }

    public void removeGameInvitationFromDB(User guest) {
        if(guest != null)
            databaseReference.child("invitations").child(guest.getId()).removeValue();
    }


    public void applyLogOut(){
        if(userAuthentication != null)
            userAuthentication.signOut();
    }
}

package com.example.tic_tac_toe;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;


public class Commons {
    /** The user connected online */
    private final static MutableLiveData<User> currentUserLiveData = new MutableLiveData<User>();


    public static boolean isConnectedCurrentUserExist(){
        return currentUserLiveData.getValue() != null;
    }

    public static User getCurrentUser() {
        return currentUserLiveData.getValue();
    }

    public static void setCurrentUser(User user) {
        Commons.currentUserLiveData.setValue(user);
    }

    public static MutableLiveData<User> getCurrentUserLiveData(){
        return Commons.currentUserLiveData;
    }

    public static void updateCurrentUserStatus(User.EUserStatus status){
        User currentUser = Commons.getCurrentUser();
        currentUser.setStatus(status);
        FirebaseUtils.updateUserStatusInDB(currentUser, status);
    }

    public static void resetCurrentUser(){
        setCurrentUser(null);
    }


    public static Intent buildGameIntent(User host, User guest){
        Intent intent = new Intent();
        intent.putExtra("host", host.toHashMap());
        intent.putExtra("guest", guest.toHashMap());
        return intent;
    }



}

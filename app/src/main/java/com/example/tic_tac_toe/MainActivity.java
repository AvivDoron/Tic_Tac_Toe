package com.example.tic_tac_toe;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dialog_fragments.NetworkDisconnectedDialogFragment;
import tic_tac_toe_game.Game;

/** The "home screen" of the app */
public class MainActivity extends RootAppActivity {
    private final int requestIDAuthentication = 1;
    private final int requestIDPerformGame = 2;
    private final int requestIDConnectedRival = 3;
    private final int requestIDGamesHistory = 4;
    public static Context applicationContext;
    public static Context mainActivityContext;
    private NetworkDisconnectedDialogFragment networkDisconnectedDialogFragment;
    private boolean shouldBackPressAllowed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.shouldBackPressAllowed = true;
        this.mainActivityContext = this;
        this.applicationContext = getApplication().getApplicationContext();
        setContentView(R.layout.activity_main);
        initPlayOnlineBtn();
        initPlayOfflineBtn();
        initLoginLogOutBtn();
        initUiLoginStuff();
    }

    private void initPlayOnlineBtn(){
        ((Button) findViewById(R.id.PlayVersusPeopleBtn)).setOnClickListener(View -> {
            if (Commons.isConnectedCurrentUserExist())
                openConnectedUsersScreen();
            else
                popToast("You need to Connect online");
        });
    }

    private void initPlayOfflineBtn(){
        ((Button) findViewById(R.id.PlayVersusSystemBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User offlineUser = new User("You");
                User userSystem = new User("System");
                Intent intent = Commons.buildGameIntent(offlineUser, userSystem);
                intent.putExtra("gameType", Game.EGameType.OFFLINE.toString());
                intent.setAction("android.intent.action.game");
                startActivityForResult(intent, requestIDPerformGame);
            }
        });
    }

    private void initLoginLogOutBtn(){
        ((Button) findViewById(R.id.Main_loginBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.authentication");
                startActivityForResult(intent, requestIDAuthentication);
            }
        });
    }

    private void initUiLoginStuff(){
        Commons.getCurrentUserLiveData().observe(this, currentUser -> {
            TextView currentUserNameTV = (TextView) findViewById(R.id.Main_CurrentUserNameTV);
            boolean isUserConnected = Commons.isConnectedCurrentUserExist();
            int visibilityStatus = isUserConnected ? View.VISIBLE : View.INVISIBLE;
            Button loginSignupBtn = (Button)findViewById(R.id.Main_loginBtn);
            loginSignupBtn.setVisibility(isUserConnected ? View.INVISIBLE : View.VISIBLE);
            String textToShow = isUserConnected ? Commons.getCurrentUser().getFullName() : "";
            currentUserNameTV.setVisibility(visibilityStatus);
            currentUserNameTV.setText(textToShow);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(data != null) {
                switch(resultCode) {
                    case RESULT_OK:
                        onActivityResultOK(requestCode, data);
                        break;
                    case RESULT_CANCELED:
                        onActivityResultCanceled(requestCode, data);
                }
        }
    }

    private void onActivityResultOK(int requestCode, @Nullable Intent data){
        if(data == null)
            return;
        switch(requestCode){
            case requestIDConnectedRival:
                Log.i("Main Activity", "Connected rival result was achieved successfully");
                data.putExtra("gameType", Game.EGameType.ONLINE.toString());
                data.setAction("android.intent.action.game");
                startActivityForResult(data, requestIDPerformGame);
                break;
            case requestIDAuthentication:
                break;
            case requestIDPerformGame:
                break;
        }
    }

    private void onActivityResultCanceled(int requestCode, @Nullable Intent data){
        if(data == null)
            return;
        boolean userLoggedOut = isActivityResultLogout(data);
        boolean networkDisconnected = isActivityResultNetworkDisconnected(data);
        boolean userExited = isActivityResultExit(data);
        if (userLoggedOut || userExited) {
            logOutCurrentUser();
            if(userExited)
                terminateApplication();
        }
        switch(requestCode){
            case requestIDConnectedRival:
                break;
            case requestIDAuthentication:
                break;
            case requestIDPerformGame:
                break;
        }
    }


    private boolean isActivityResultLogout(Intent data){
        if(data == null || !data.hasExtra("userHasLoggedOut"))
            return false;
        return data.getBooleanExtra("userHasLoggedOut", true);
    }

    private boolean isActivityResultExit(Intent data) {
        if(data == null || !data.hasExtra("userHasExited"))
            return false;
        return data.getBooleanExtra("userHasExited", true);
    }

    private boolean isActivityResultNetworkDisconnected(Intent data) {
        if(data == null || !data.hasExtra("networkHasDisconnected"))
            return false;
        return data.getBooleanExtra("networkHasDisconnected", true);
    }

    public void openConnectedUsersScreen(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.connected_rivals");
        startActivityForResult(intent, requestIDConnectedRival);
    }

    public void openGamesHistoryScreen(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.games_history");
        startActivityForResult(intent, requestIDGamesHistory);
    }

    private void setShouldBackPressAllowed(boolean shouldBackPressAllowed){
        this.shouldBackPressAllowed = shouldBackPressAllowed;
    }

    private void logOutCurrentUser(){
        if(Commons.isConnectedCurrentUserExist()) {
            FirebaseUtils firebaseUtils = new FirebaseUtils();
            firebaseUtils.applyLogOut();
            Commons.updateCurrentUserStatus(User.EUserStatus.OFFLINE);
            Commons.resetCurrentUser();
        }
    }



    //-------------------------------------------- INetworkDisconnectedDialogListener ---------------------------------------------------------//


    private void openNetworkDisconnectedDialog(String title, String message) {
        if(networkDisconnectedDialogFragment != null)
            terminateDialog(networkDisconnectedDialogFragment);
            FragmentManager fragmentManager = getSupportFragmentManager();
            networkDisconnectedDialogFragment = NetworkDisconnectedDialogFragment.newInstance(title, message);
            networkDisconnectedDialogFragment.setCancelable(false);
            networkDisconnectedDialogFragment.show(fragmentManager, "network_disconnected");

    }




    //--------------------------------------- IExitConfirmationDialogListener -----------------------------------------------------//



    public void terminateApplication() {
        finish();
        System.exit(0);
    }

    @Override
    public void onExitConfirmationPositiveResult() {
        terminateApplication();
    }




    //--------------------------------------- ILogOutConfirmationDialogListener -----------------------------------------------------//


    @Override
    public void onLogOutConfirmationPositiveResult() {
        logOutCurrentUser();
    }





    //------------------------------------------------ NetworkReceiver -------------------------------------------------------------//

    @Override
    public void handleNetworkConnectedEvent() {
        if(networkDisconnectedDialogFragment != null){
            terminateDialog(networkDisconnectedDialogFragment);
            setShouldBackPressAllowed(true);
        }
    }

    @Override
    public void handleNetworkDisconnectedEvent() {
        logOutCurrentUser();
        openNetworkDisconnectedDialog("Network Disconnected", "Please check your network connection");
    }





    //---------------------------------------- Menu methods implementation-------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean creationRes = super.onCreateOptionsMenu(menu);
        MenuItem gamesHistoryItem = menu.findItem(R.id.optionsMenu_gamesHistory);
        gamesHistoryItem.setVisible(true);
        return creationRes;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.optionsMenu_gamesHistory:
                if(Commons.getCurrentUser() != null)
                    openGamesHistoryScreen();
                else
                    popToast("You need to Connect online");
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    //--------------------------------------------------------------------------------------------------------------------//





}
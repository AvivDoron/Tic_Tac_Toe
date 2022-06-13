package com.example.tic_tac_toe;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import dialog_fragments.AdaptableDialogFragment;
import dialog_fragments.ExitConfirmationDialogFragment;
import dialog_fragments.LogOutConfirmationDialogFragment;
import dialog_fragments.NetworkDisconnectedDialogFragment;
import helpers.NetworkReceiver;

/**
 * General commons behaviors of all the app activities.
 *  All activities are subclasses of it.
 */
public abstract class RootAppActivity extends AppCompatActivity implements NetworkReceiver.NetworkReceiverListener
        , NetworkDisconnectedDialogFragment.INetworkDisconnectedDialogListener
        , ExitConfirmationDialogFragment.IExitConfirmationDialogListener
        , LogOutConfirmationDialogFragment.ILogOutConfirmationDialogListener {


    private NetworkReceiver networkReceiver;
    protected ExitConfirmationDialogFragment exitConfirmationDialogFragment;
    protected LogOutConfirmationDialogFragment logOutConfirmationDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the app name in the action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        networkReceiver = new NetworkReceiver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }





    //-------------------------------------------- NetworkReceiver ---------------------------------------------------------//


    @Override
    public void handleNetworkConnectedEvent() {
        /**
         *  Required by NetworkReceiverListener interface.
         *  Nothing to do here, should be overridden
         */
    }

    @Override
    public void handleNetworkDisconnectedEvent() {
        /**
         *  Required by NetworkReceiverListener interface.
         *  Nothing to do here, should be overridden
         */
    }


    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }




    //-------------------------------------------------- Dialogs methods implementations ------------------------------------------------------------------//


    // Terminates an open dialog (general to all dialogs)
    protected void terminateDialog(AdaptableDialogFragment dialog){
        dialog.hide();
        dialog = null;
    }

    //------------------------------------- IExitConfirmationDialogListener ------------------------------------------------//


    public abstract void onExitConfirmationPositiveResult();    /** Nothing to do here, should be overridden */

    @Override
    public void onExitConfirmationNegativeResult() {
        terminateDialog(exitConfirmationDialogFragment);
    }





    //----------------------------------- ILogOutConfirmationDialogListener ------------------------------------------------//


    @Override
    public abstract void onLogOutConfirmationPositiveResult();    /** Nothing to do here, should be overridden */

    @Override
    public void onLogOutConfirmationNegativeResult(){
        terminateDialog(logOutConfirmationDialogFragment);
    }





//-------------------------------------------------- Main Menu methods implementation ------------------------------------------------------------------//



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        Commons.getCurrentUserLiveData().observe(this, currentUser -> {
            MenuItem logOutItem = menu.findItem(R.id.optionsMenu_logOut);
            boolean logOutItemShouldBeVisible = Commons.isConnectedCurrentUserExist();
            logOutItem.setVisible(logOutItemShouldBeVisible);

        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.optionsMenu_exit:
                openExitConfirmationDialog("Closing the application", "Are you sure?", "yes", "no");
                break;
            case R.id.optionsMenu_logOut:
                openLogOutConfirmationDialog("Logging out", "Are you sure?", "yes", "no");;
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    //---------------------------------------------------- General methods -------------------------------------------------------------------------//




    protected void openExitConfirmationDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        if(exitConfirmationDialogFragment != null)
            terminateDialog(exitConfirmationDialogFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        exitConfirmationDialogFragment = ExitConfirmationDialogFragment.newInstance(title, message, positiveBtnTxt, negativeBtnTxt);
        exitConfirmationDialogFragment.show(fragmentManager, "exit_confirmation");
    }


    protected void openLogOutConfirmationDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        if(logOutConfirmationDialogFragment != null)
            terminateDialog(logOutConfirmationDialogFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        logOutConfirmationDialogFragment = LogOutConfirmationDialogFragment.newInstance(title, message, positiveBtnTxt, negativeBtnTxt);
        logOutConfirmationDialogFragment.show(fragmentManager, "exit_confirmation");
    }



    protected void finishActivityWithResult(int resultId, Intent intent){
        setResult(resultId ,intent);
        finish();
    }


    protected Intent buildIntentForCanceledResult(String resultName, boolean res){
        Intent intent = new Intent();
        intent.putExtra(resultName, res);
        return intent;
    }


    // Makes toast that contains given text
    protected void popToast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }


}
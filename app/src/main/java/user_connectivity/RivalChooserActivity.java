package user_connectivity;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.RootAppActivity;
import com.example.tic_tac_toe.User;


public class RivalChooserActivity extends RootAppActivity implements ConnectedUsersFragment.ConnectedUsersFragmentListener, PendingInvitationDialogFragment.IPendingInvitationDialogListener, InvitationAchievedDialogFragment.IInvitationAchievedDialogListener {
    private ConnectedUsersFragment connectedUsersFragment;
    private PendingInvitationDialogFragment pendingInvitationDialogFragment;
    private InvitationAchievedDialogFragment invitationAchievedDialogFragment;
    private final String CONNECTED_USERS_FRAGMENT_TAG = "ConnectedUsers";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rival_chooser);
        initiateConnectedUsersFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Commons.isConnectedCurrentUserExist())
            Commons.updateCurrentUserStatus(User.EUserStatus.AVAILABLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Commons.isConnectedCurrentUserExist())
            Commons.updateCurrentUserStatus(User.EUserStatus.UNAVAILABLE);
    }




    private void initiateConnectedUsersFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.RivalChooser_connectedUsersFragmentContainer, ConnectedUsersFragment.class,null , CONNECTED_USERS_FRAGMENT_TAG).commit();
        getSupportFragmentManager().executePendingTransactions();
        this.connectedUsersFragment = (ConnectedUsersFragment) getSupportFragmentManager().findFragmentByTag(CONNECTED_USERS_FRAGMENT_TAG);
    }

    private void cancelPendingInvitation(){
        connectedUsersFragment.cancelPendingInvitation();
    }

    private void openInvitationAchievedDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        if(invitationAchievedDialogFragment != null)
            terminateDialog(invitationAchievedDialogFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        invitationAchievedDialogFragment = invitationAchievedDialogFragment.newInstance(title, message, positiveBtnTxt, negativeBtnTxt);
        invitationAchievedDialogFragment.show(fragmentManager, "invitation_Achieved");
    }

    public void openPendingInvitationDialog(String title, String message, String btnTxt) {
        if(pendingInvitationDialogFragment != null)
            terminateDialog(pendingInvitationDialogFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        pendingInvitationDialogFragment = pendingInvitationDialogFragment.newInstance(title, message, btnTxt);
        pendingInvitationDialogFragment.show(fragmentManager, "pending_for_online_game");
    }






    //-------------------- ConnectedUsersFragment interface methods implementation--------------------------//

    //
    @Override
    public void askToPerformOnlineGame(User host, User guest) {
        Intent intent = Commons.buildGameIntent(host, guest);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void openInvitationAchievedDialog(String hostFullName) {
        openInvitationAchievedDialog("Invitation achieved", hostFullName + " invited you to play. Will you take the challenge?", "yes", "no");
    }

    @Override
    public void terminateInvitationAchievedDialog() {
        terminateDialog(invitationAchievedDialogFragment);
    }

    public void openPendingInvitationDialog(){
        openPendingInvitationDialog("Pending", "Waiting for response...", "Cancel");
    }

    public void terminatePendingInvitationDialog(){
        terminateDialog(pendingInvitationDialogFragment);
    }


    //-------------------------------------------------- Dialogs methods implementations ------------------------------------------------------------------//

    //----------------------------------- IExitConfirmationDialogListener -------------------------------------------------//



    @Override
    public void onExitConfirmationPositiveResult() {
        cancelPendingInvitation();
        Intent intent = buildIntentForCanceledResult("userHasExited", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }



    //---------------------------------- ILogOutConfirmationDialogListener -------------------------------------------//


    @Override
    public void onLogOutConfirmationPositiveResult() {
        cancelPendingInvitation();
        Intent intent = buildIntentForCanceledResult("userHasLoggedOut", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }




    //--------------------------------- IPendingInvitationDialogListener ---------------------------------------------//



    @Override
    public void onCancelPendingInvitationClicked() {
        cancelPendingInvitation();
    }



    //--------------------------------- IInvitationAchievedDialogListener --------------------------------------------//


    @Override
    public void onInvitationAchievedPositiveResult() {
        connectedUsersFragment.confirmAchievedInvitation();
    }

    @Override
    public void onInvitationAchievedNegativeResult() {
        connectedUsersFragment.rejectAchievedInvitation();
    }


    //-------------------------------------------------- NetworkReceiver -------------------------------------------------------------//



    @Override
    public void handleNetworkDisconnectedEvent() {
        Intent intent = buildIntentForCanceledResult("networkHasDisconnected", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }



}
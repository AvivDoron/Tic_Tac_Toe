package user_connectivity;

import android.content.Intent;
import android.os.Bundle;


import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.RootAppActivity;


public class UserAuthenticationActivity extends RootAppActivity implements LoginFragment.LoginFragmentListener, SignupFragment.SignupFragmentListener {
    private final String SIGNUP_FRAG_TAG = "signup_frag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);
    }




    //-------------------------------------------- LoginFragmentListener ---------------------------------------------------------//



    @Override
    public void sendUserDetailsResults(){
        finishActivityWithResult(RESULT_OK, new Intent());
    }


    @Override
    public void openRegistrationScreen(){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.Authentication_FragmentContainer, SignupFragment.class, null,SIGNUP_FRAG_TAG)
                .addToBackStack(SIGNUP_FRAG_TAG)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }





    //-------------------------------------------- SignupFragmentListener ---------------------------------------------------------//


    @Override
    public void closeRegistrationScreen() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .remove(getSupportFragmentManager().findFragmentByTag(SIGNUP_FRAG_TAG))
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }




    //----------------------------------------------- NetworkReceiver -----------------------------------------------------------//


    @Override
    public void handleNetworkDisconnectedEvent() {
        Intent intent = buildIntentForCanceledResult("networkHasDisconnected", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }




    //--------------------------------------- IExitConfirmationDialogListener -----------------------------------------------------//

    @Override
    public void onExitConfirmationPositiveResult() {
        Intent intent = buildIntentForCanceledResult("userHasExited", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }




    //--------------------------------------- ILogOutConfirmationDialogListener -----------------------------------------------------//


    @Override
    public void onLogOutConfirmationPositiveResult() {
        Intent intent = buildIntentForCanceledResult("userHasLoggedOut", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }



    //------------------------------------------------------------------------------------------------------------------------//


}
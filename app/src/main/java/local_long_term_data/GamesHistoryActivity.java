package local_long_term_data;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.RootAppActivity;


public class GamesHistoryActivity extends RootAppActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_history);
    }



    //------------------------------------------------ NetworkReceiver -------------------------------------------------------------//

    @Override
    public void handleNetworkDisconnectedEvent() {
        Intent intent = buildIntentForCanceledResult("networkHasDisconnected", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }

    //---------------------------------------- IExitConfirmationDialogListener ----------------------------------------------------//


    @Override
    public void onExitConfirmationPositiveResult() {
        Intent intent = buildIntentForCanceledResult("userHasExited", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }




    //---------------------------------------- ILogOutConfirmationDialogListener ----------------------------------------------------//




    @Override
    public void onLogOutConfirmationPositiveResult() {
        Intent intent = buildIntentForCanceledResult("userHasLoggedOut", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }


}
package tic_tac_toe_game;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.fragment.app.FragmentManager;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.RootAppActivity;
import com.example.tic_tac_toe.User;

import dialog_fragments.ExitConfirmationDialogFragment;

import java.util.HashMap;

import helpers.GameSupervisorService;
import helpers.GameSupervisorUpdatesReceiver;


public class GameActivity extends RootAppActivity implements GameFragment.GameFragmentListener
        , ExitConfirmationDialogFragment.IExitConfirmationDialogListener
        , GiveUpConfirmationDialogFragment.IGiveUpConfirmationDialogListener
        , GameSupervisorUpdatesReceiver.GameSupervisorUpdatesReceiverListener {

    private Intent receivedRivalsIntent;
    private User host;
    private User guest;
    private Game.EGameType gameType;
    private GameSupervisorService mService;
    private boolean mBound = false;
    private GameFragment gameFragment;
    private final String GAME_FRAGMENT_TAG = "GameFragment";
    private GameSupervisorUpdatesReceiver gameSupervisorUpdatesReceiver;
    private GiveUpConfirmationDialogFragment giveUpConfirmationDialogFragment;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.receivedRivalsIntent = getIntent();
        this.host = extractUserFromIntent(receivedRivalsIntent, "host");
        this.guest = extractUserFromIntent(receivedRivalsIntent, "guest");
        this.gameType = extractGameTypeFromIntent();

        initiateGameFragment();
        startGameSupervisorService();
        if(gameSupervisorUpdatesReceiver == null)
            gameSupervisorUpdatesReceiver = new GameSupervisorUpdatesReceiver(this);

        initGameSupervisorServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = buildGameSupervisorServiceIntent();
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.tic_tac_toe.PLAYER_IS_ABSENT");
        registerReceiver(gameSupervisorUpdatesReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateServiceGameFinished();
        unbindService(connection);
        mBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        terminateSupervisorServiceInteraction();
    }

    @Override
    public void onBackPressed() {
        openGiveUpConfirmationDialog();
    }

    private void initiateGameFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.Game_fragmentContainer, GameFragment.class,null , GAME_FRAGMENT_TAG).commit();
        getSupportFragmentManager().executePendingTransactions();
        this.gameFragment = (GameFragment) getSupportFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);
    }

    private Game.EGameType extractGameTypeFromIntent() {
        String gameType = receivedRivalsIntent.getStringExtra("gameType");
        return Game.EGameType.valueOf(gameType);
    }

    private User extractUserFromIntent(Intent intent, String key){
        HashMap<String, Object> userMap = (HashMap<String, Object>) intent.getSerializableExtra(key);
        return new User(userMap);
    }





    public void openGiveUpConfirmationDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        if(giveUpConfirmationDialogFragment != null)
            terminateDialog(giveUpConfirmationDialogFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        giveUpConfirmationDialogFragment = GiveUpConfirmationDialogFragment.newInstance(title, message, positiveBtnTxt, negativeBtnTxt);
        giveUpConfirmationDialogFragment.show(fragmentManager, "give_up_a_game");
    }


    //--------------------------------------------- GameFragmentListener -------------------------------------------------------//



    @Override
    public User getGameHost() {
        return host;
    }

    @Override
    public User getGameGuest() {
        return guest;
    }


    @Override
    public Game.EGameType getGameType() {
        return gameType;
    }

    @Override
    public void handleRivalLeftEvent() {
        updateServiceGameFinished();
        finishActivityWithResult(RESULT_OK, new Intent());
    }

    private void updateServiceGameFinished() {
        if(mBound)
            mService.handleGameFinished();
    }

    @Override
    public void informCurrentPlayerTurnArrived() {
        if(mBound)
            mService.startCountingTime();
    }

    @Override
    public void informCurrentPlayerTurnFinished() {
        if(mBound)
            mService.stopCountingTime();
    }

    @Override
    public void openGiveUpConfirmationDialog() {
        openGiveUpConfirmationDialog("Give up", "Are you sure?", "Yes", "No");
    }




    //------------------------------------------ IExitConfirmationDialogListener ------------------------------------------------------//

    @Override
    public void onExitConfirmationPositiveResult() {
        //applyGameLeaving();
        gameFragment.applyPlayerGiveUpProcedure();
        updateServiceGameFinished();
        terminateSupervisorServiceInteraction();
        Intent intent = buildIntentForCanceledResult("userHasExited", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }



    //----------------------------------------- IGiveUpConfirmationDialogListener -----------------------------------------------------//


    @Override
    public void onGiveUpConfirmationPositiveResult() {
        gameFragment.applyPlayerGiveUpProcedure();
        updateServiceGameFinished();
        finishActivityWithResult(RESULT_OK, new Intent());
    }

    @Override
    public void onGiveUpConfirmationNegativeResult() {
        terminateDialog(giveUpConfirmationDialogFragment);
    }




    //-------------------------------------- ILogOutConfirmationDialogListener --------------------------------------------------------//


    @Override
    public void onLogOutConfirmationPositiveResult() {
        gameFragment.applyPlayerGiveUpProcedure();
        updateServiceGameFinished();
        Intent intent = buildIntentForCanceledResult("userHasLoggedOut", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }



    //-------------------------------------------------- NetworkReceiver -------------------------------------------------------------//

    @Override
    public void handleNetworkDisconnectedEvent() {
        gameFragment.giveRivalExtraPoint();
        Intent intent = buildIntentForCanceledResult("networkHasDisconnected", true);
        finishActivityWithResult(RESULT_CANCELED, intent);
    }





    //-------------------------------------- GameSupervisorUpdatesReceiverListener -------------------------------------------------//


    @Override
    public void onTurnTimeOut() {
        gameFragment.handleTurnTimeOutEvent();
        updateServiceGameFinished();
        finishActivityWithResult(RESULT_OK, new Intent());

    }




    //--------------------------------------------- GameSupervisorService ---------------------------------------------------------//


    private void initGameSupervisorServiceConnection(){
        this.connection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                GameSupervisorService.LocalBinder binder = (GameSupervisorService.LocalBinder) iBinder;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBound = false;
            }
        };
    }

    private void startGameSupervisorService(){
        if (!isGameSupervisorServiceRunning(GameSupervisorService.class)){
            Intent intent = new Intent(this, GameSupervisorService.class);
            startService(intent);
        }
    }

    private Intent buildGameSupervisorServiceIntent(){
        Intent intent = new Intent(this, GameSupervisorService.class);
        if(gameType.toString().equals(Game.EGameType.OFFLINE.toString())) {
            intent.putExtra("isCurrentUserFirstTurn", true);
        }
        else
            intent.putExtra("isCurrentUserFirstTurn", host.equals(Commons.getCurrentUser()));
        return intent;
    }

    private void terminateSupervisorServiceInteraction(){
        unregisterReceiver(gameSupervisorUpdatesReceiver);
        Intent intent = new Intent(this,
                GameSupervisorService.class);
        stopService(intent);
    }

    private boolean isGameSupervisorServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
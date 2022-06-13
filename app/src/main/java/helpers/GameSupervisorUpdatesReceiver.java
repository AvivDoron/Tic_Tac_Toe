package helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GameSupervisorUpdatesReceiver extends BroadcastReceiver {
    private GameSupervisorUpdatesReceiverListener listener;

    public GameSupervisorUpdatesReceiver(GameSupervisorUpdatesReceiverListener listener) {
        this.listener = listener;
    }

    public GameSupervisorUpdatesReceiver(){
        // Required empty public constructor
    }


    public interface GameSupervisorUpdatesReceiverListener{
        void onTurnTimeOut();
    }




    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedAction = intent.getAction();
        switch(receivedAction) {
            case "com.example.tic_tac_toe.PLAYER_IS_ABSENT":
                boolean isCurrentPlayerAbsent = intent.getBooleanExtra("isCurrentPlayerAbsent", true);
                if(isCurrentPlayerAbsent) {
                    listener.onTurnTimeOut();
                }
                break;
        }
    }
}

package helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.tic_tac_toe.R;
import tic_tac_toe_game.GameActivity;

/**
 * A bound service.
 * Counts the turn time of each player.
 * In case of non-response of a player, reminds to play via notification.
 * If there is still no response, sends a broadcast
 */
public class GameSupervisorService extends Service {
    public static final String CHANNEL_ID = "my_channel_01";
    public static final int NOTIFICATION_ID1 = 1;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    private TimeCounter timeCounter;
    private boolean isCurrentUserFirstTurn;
    private Object monitor = new Object();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.isCurrentUserFirstTurn = intent.getBooleanExtra("isCurrentUserFirstTurn", true);
        timeCounter.setIsCurrentPlayerTurn(isCurrentUserFirstTurn);
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.timeCounter = new TimeCounter();
        initForeground(CHANNEL_ID, NOTIFICATION_ID1);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int res = super.onStartCommand(intent, flags, startId);
        timeCounter.start();
        return res;
    }



    private void initForeground(String channelId, int notificationId){
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId,
                "My main channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder = new Notification.Builder(this, channelId)
                .setContentTitle("Tic Tac Toe")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle());
        startForeground(notificationId, updateNotification(notificationId, "my check notification"));
    }



    public Notification updateNotification(int notificationId, String details){
        notificationBuilder.setContentText(details).setOnlyAlertOnce(false);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(notificationId, notification);
        return notification;
    }





    private class TimeCounter extends Thread{
        private final int NON_INTERACTION_ALLOWED_TIME = 20;
        private final int TIME_TO_NOTIFY_USER = NON_INTERACTION_ALLOWED_TIME >> 1;
        private final String currentPlayerAbsentMsg =  String.format("You are absent for too long. The game will be over in %d seconds and your rival gets an extra point of winning", TIME_TO_NOTIFY_USER);
        private final String rivalIsAbsentMsg =  String.format("Your rival is absent for too long. The game will be in %d seconds and you gets an extra point of winning", TIME_TO_NOTIFY_USER);
        private Timer timer;
        private boolean serviceStillRunning;
        private boolean isCurrentPlayerTurn;

        public TimeCounter(){
            this.timer = new Timer(NON_INTERACTION_ALLOWED_TIME);
            this.serviceStillRunning = true;
        }

        public void resetTimeCounting(){
            timer.reset();
        }


        public void setServiceRunningStatus(boolean runningStatus){
            this.serviceStillRunning = runningStatus;
        }

        public void setIsCurrentPlayerTurn(boolean isCurrentPlayerTurn){
            this.isCurrentPlayerTurn = isCurrentPlayerTurn;
        }

        public void updateTurn(boolean isCurrentPlayerTurn){
            setIsCurrentPlayerTurn(isCurrentPlayerTurn);
            resetTimeCounting();
        }

        private void updateUserTimeIsRunningOut(){
            String requiredContent = isCurrentPlayerTurn ? currentPlayerAbsentMsg : rivalIsAbsentMsg;
            updateNotification(NOTIFICATION_ID1, requiredContent);
        }


        @Override
        public void run() {
            super.run();
            timer.start();
                while (serviceStillRunning) {
                    if (!timer.doesTimeHasElapsed()) {
                        if(timer.getTimePassed() == TIME_TO_NOTIFY_USER){
                            updateUserTimeIsRunningOut();
                        }
                        try {
                        Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        timer.increaseSecondsCounter();
                    }
                    else{
                        sendBroadcastUpdate(isCurrentPlayerTurn ? true : false);
                        synchronized(monitor) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        }
    }


    private void sendBroadcastUpdate(boolean isCurrentPlayerAbsent){
        Intent intent = new Intent("com.example.tic_tac_toe.PLAYER_IS_ABSENT");
        intent.putExtra("isCurrentPlayerAbsent", isCurrentPlayerAbsent);
        sendBroadcast(new Intent("com.example.tic_tac_toe.PLAYER_IS_ABSENT"));
    }

    public void startCountingTime(){
        timeCounter.updateTurn(true);
    }

    public void stopCountingTime(){
        timeCounter.updateTurn(false);
    }

    public void handleGameFinished(){
        timeCounter.setServiceRunningStatus(false);
        synchronized (monitor){
            monitor.notifyAll();
        }
    }

    public class LocalBinder extends Binder {
        public GameSupervisorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GameSupervisorService.this;
        }
    }


}

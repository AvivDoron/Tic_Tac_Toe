package helpers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class NetworkReceiver extends BroadcastReceiver {
    private NetworkReceiverListener listener;


    public NetworkReceiver(){
        // Required empty public constructor
    }

    public NetworkReceiver(NetworkReceiverListener listener) {
        this.listener = listener;
    }

    public interface NetworkReceiverListener{
        void handleNetworkConnectedEvent();
        void handleNetworkDisconnectedEvent();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            listener.handleNetworkConnectedEvent();
        }
        else {
            Log.e("NetworkReceiver", "The device network is disconnected");
            listener.handleNetworkDisconnectedEvent();
        }
    }


}
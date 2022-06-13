package dialog_fragments;

import android.content.Context;
import android.os.Bundle;
import dialog_fragments.NetworkDisconnectedDialogFragment.INetworkDisconnectedDialogListener;
import androidx.annotation.NonNull;

public class NetworkDisconnectedDialogFragment extends AdaptableDialogFragment<INetworkDisconnectedDialogListener> {



    public interface INetworkDisconnectedDialogListener extends IAdaptableDialogResultListener{
        // Required by AdaptableDialogFragment - Nothing to do here
    }


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (INetworkDisconnectedDialogListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'INetworkDisconnectedDialogListener'");
        }
        super.onAttach(context);
    }


    public static NetworkDisconnectedDialogFragment newInstance(String title, String message) {
        NetworkDisconnectedDialogFragment frag = new NetworkDisconnectedDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }


    @Override
    protected void informOnDialogResult(String id, boolean isPositiveResult) {
        // Required by AdaptableDialogFragment - Nothing to do here
    }

}

package dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.tic_tac_toe.R;

import dialog_fragments.AdaptableDialogFragment.IAdaptableDialogResultListener;

/**
 * Abstract adaptable dialog fragment.
 * All other dialog fragments are subclasses of it
 * @param <T>
 */
public abstract class AdaptableDialogFragment <T extends IAdaptableDialogResultListener> extends DialogFragment {
    protected T listener;
    protected AlertDialog alertDialog;


    /** Should be implemented by interfaces in subclasses */
    public interface IAdaptableDialogResultListener {
        //  Nothing to to here
    }

    /** Informs the listeners on the dialog result (i.e. positive/negative button clicked) */
    protected abstract void informOnDialogResult(String id, boolean isPositiveResult);


    /** Hides an open dialog */
    public void hide(){
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String positiveBtnTxt = getArguments().getString("positiveBtnTxt");
        String negativeBtnTxt = getArguments().getString("negativeBtnTxt");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((Context) listener);

        if(title != null)
            alertDialogBuilder.setTitle(title);
        if(message != null) {
            if(message.equals("Waiting for response...")) {
                final FragmentActivity fragmentActivity = (FragmentActivity) listener;
                View progressBarContainer = fragmentActivity.getLayoutInflater().inflate(R.layout.layout_progress_bar, null);
                TextView messageTV = ((TextView)progressBarContainer.findViewById(R.id.layout_progressBar_messageTV));
                messageTV.setText(message);
                alertDialogBuilder.setView(progressBarContainer);
            }
            else
                alertDialogBuilder.setMessage(message);
        }
        if(positiveBtnTxt != null) {
            alertDialogBuilder.setPositiveButton(positiveBtnTxt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        informOnDialogResult(title, true);
                    }
                }
            });
        }
        if(negativeBtnTxt != null) {
            alertDialogBuilder.setNegativeButton(negativeBtnTxt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        informOnDialogResult(title, false);
                        dialog.dismiss();
                    }
                }

            });
        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }







}

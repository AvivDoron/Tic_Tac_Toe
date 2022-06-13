package tic_tac_toe_game;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import dialog_fragments.AdaptableDialogFragment;
import tic_tac_toe_game.GiveUpConfirmationDialogFragment.IGiveUpConfirmationDialogListener;


public class GiveUpConfirmationDialogFragment extends AdaptableDialogFragment<IGiveUpConfirmationDialogListener> {



    public interface IGiveUpConfirmationDialogListener extends IAdaptableDialogResultListener{
        void onGiveUpConfirmationPositiveResult();
        void onGiveUpConfirmationNegativeResult();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (IGiveUpConfirmationDialogListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'IGiveUpConfirmationDialogListener'");
        }
        super.onAttach(context);
    }

    public static GiveUpConfirmationDialogFragment newInstance(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        GiveUpConfirmationDialogFragment frag = new GiveUpConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positiveBtnTxt", positiveBtnTxt);
        args.putString("negativeBtnTxt", negativeBtnTxt);
        frag.setArguments(args);
        return frag;
    }

    @Override
    protected void informOnDialogResult(String id, boolean isPositiveResult) {
        if(isPositiveResult)
            listener.onGiveUpConfirmationPositiveResult();
        else
            listener.onGiveUpConfirmationNegativeResult();
    }
}

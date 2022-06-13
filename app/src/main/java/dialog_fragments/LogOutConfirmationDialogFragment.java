package dialog_fragments;

import android.content.Context;
import android.os.Bundle;
import dialog_fragments.LogOutConfirmationDialogFragment.ILogOutConfirmationDialogListener;
import androidx.annotation.NonNull;

public class LogOutConfirmationDialogFragment extends AdaptableDialogFragment<ILogOutConfirmationDialogListener> {


    public interface ILogOutConfirmationDialogListener extends IAdaptableDialogResultListener{
        void onLogOutConfirmationPositiveResult();
        void onLogOutConfirmationNegativeResult();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (ILogOutConfirmationDialogListener)context;

        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'ILogOutConfirmationDialogListener'");
        }
        super.onAttach(context);
    }


    public static LogOutConfirmationDialogFragment newInstance(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        LogOutConfirmationDialogFragment frag = new LogOutConfirmationDialogFragment();
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
            listener.onLogOutConfirmationPositiveResult();
        else
            listener.onLogOutConfirmationNegativeResult();
    }
}

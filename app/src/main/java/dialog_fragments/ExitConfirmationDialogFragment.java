package dialog_fragments;


import android.content.Context;
import android.os.Bundle;
import dialog_fragments.ExitConfirmationDialogFragment.IExitConfirmationDialogListener;
import androidx.annotation.NonNull;


public class ExitConfirmationDialogFragment extends AdaptableDialogFragment<IExitConfirmationDialogListener> {


    public interface IExitConfirmationDialogListener extends IAdaptableDialogResultListener{
        void onExitConfirmationPositiveResult();
        void onExitConfirmationNegativeResult();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (IExitConfirmationDialogListener)context;

        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'IExitConfirmationDialogListener'");
        }
        super.onAttach(context);
    }


    public static ExitConfirmationDialogFragment newInstance(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        ExitConfirmationDialogFragment frag = new ExitConfirmationDialogFragment();
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
            listener.onExitConfirmationPositiveResult();
        else
            listener.onExitConfirmationNegativeResult();
    }
}

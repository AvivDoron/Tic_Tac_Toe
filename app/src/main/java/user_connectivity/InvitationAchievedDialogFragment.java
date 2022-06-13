package user_connectivity;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import dialog_fragments.AdaptableDialogFragment;
import user_connectivity.InvitationAchievedDialogFragment.IInvitationAchievedDialogListener;


public class InvitationAchievedDialogFragment extends AdaptableDialogFragment<IInvitationAchievedDialogListener> {


    public interface IInvitationAchievedDialogListener extends IAdaptableDialogResultListener {
        void onInvitationAchievedPositiveResult();
        void onInvitationAchievedNegativeResult();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (IInvitationAchievedDialogListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'IInvitationAchievedDialogListener'");
        }
        super.onAttach(context);
    }


    public static InvitationAchievedDialogFragment newInstance(String title, String message, String positiveBtnTxt, String negativeBtnTxt) {
        InvitationAchievedDialogFragment frag = new InvitationAchievedDialogFragment();
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
            listener.onInvitationAchievedPositiveResult();
        else
            listener.onInvitationAchievedNegativeResult();
    }
}

package user_connectivity;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import dialog_fragments.AdaptableDialogFragment;
import user_connectivity.PendingInvitationDialogFragment.IPendingInvitationDialogListener;

public class PendingInvitationDialogFragment extends AdaptableDialogFragment<IPendingInvitationDialogListener> {


    public interface IPendingInvitationDialogListener extends IAdaptableDialogResultListener{
        void onCancelPendingInvitationClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            listener = (IPendingInvitationDialogListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'IPendingInvitationDialogListener'");
        }
        super.onAttach(context);
    }

    public static PendingInvitationDialogFragment newInstance(String title, String message, String positiveBtnTxt) {
        PendingInvitationDialogFragment frag = new PendingInvitationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positiveBtnTxt", positiveBtnTxt);
        frag.setArguments(args);
        return frag;
    }


    @Override
    protected void informOnDialogResult(String id, boolean isPositiveResult) {
        listener.onCancelPendingInvitationClicked();
    }

}

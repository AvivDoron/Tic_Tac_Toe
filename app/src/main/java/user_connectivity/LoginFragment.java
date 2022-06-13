package user_connectivity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.FirebaseUtils;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginFragment extends Fragment {
    private String email;
    private String password;
    private EditText emailET;
    private EditText passwordET;
    private TextView errorTV;
    private LoginFragmentListener listener;
    private FirebaseLoginUtils firebaseLoginUtils;


    public LoginFragment() {
        this.email = "";
        this.password = "";
        firebaseLoginUtils = new FirebaseLoginUtils();
    }


    // For communication with the listener activity
    public interface LoginFragmentListener{
        void sendUserDetailsResults();
        void openRegistrationScreen();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (LoginFragmentListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'LoginListener'");
        }
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Commons.resetCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailET = (EditText) view.findViewById(R.id.Login_EmailET);
        passwordET = (EditText) view.findViewById(R.id.Login_PasswordET);
        errorTV = (TextView) view.findViewById(R.id.Login_ErrorTV);
        setButtonsListeners(view);
    }




    private void setButtonsListeners(View view){
        setLoginBtnListener(view);
        setSignupBtnListener(view);
        setForgotPassBtnListener(view);
    }



    private void setLoginBtnListener(View view){
        ((Button) view.findViewById(R.id.Login_LoginBtn)).setOnClickListener(btnView -> {
            email = getTextFromEditText(emailET);
            password = getTextFromEditText(passwordET);

            if(email.isEmpty()) {
                emailET.setError("Please enter your email");
                emailET.requestFocus();
                return;
            }

            if(password.isEmpty()) {
                passwordET.setError("Please enter your password");
                passwordET.requestFocus();
                return;
            }

            firebaseLoginUtils.askToSignIn(email, password);
        });
    }


    private void setSignupBtnListener(View view){
        ((Button) view.findViewById(R.id.Login_signupBtn)).setOnClickListener(btnView -> {
            listener.openRegistrationScreen();
        });
    }

    private void setForgotPassBtnListener(View view){
        ((Button) view.findViewById(R.id.Login_ForgotPassBtn)).setOnClickListener(btnView -> {
            firebaseLoginUtils.askToResetPassword();

        });
    }



    // Get text from editText by given id
    private String getTextFromEditText(EditText editText){
        String content = editText.getText().toString();
        return content;
    }


    // Makes toast that contains given text
    private void popToast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(requireContext(), text, duration);
        toast.show();
    }



    //----------------------------------------------------- FirebaseLoginUtils class -----------------------------------------------------//


    private class FirebaseLoginUtils extends FirebaseUtils {


        public void askToSignIn(String email, String password){
            userAuthentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isCanceled()) {
                            popToast("Something went wrong, please try again");
                        }
                        else if (task.isSuccessful()) {
                            FirebaseUser currentAuth = userAuthentication.getCurrentUser();
                            if(!currentAuth.isEmailVerified()) {
                                popToast("Please verify your account via email before you could login");
                            } else {
                                if(Commons.getCurrentUser() == null || !Commons.getCurrentUser().getIsVerified())
                                    updateCurrentUserIsActiveInDatabase(currentAuth.getUid());

/*                                else if()
                                    Commons.updateCurrentUserStatus("available");*/
                                listener.sendUserDetailsResults();
                            }
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            errorTV.setVisibility(View.VISIBLE);
                        }
                    }
                });
        }

        public void askToResetPassword(){
            String email = getTextFromEditText(emailET);
            if(!email.isEmpty()) {
                userAuthentication.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                popToast("An email message sent to your address for resetting your password");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            popToast("There is no user record corresponding to this email address.");
                        }
                });
            }
            else {
                // If reset password fails, display a message to the user.
                popToast("Please type your email and try again");
            }
        }


    }

    //-------------------------------------------------------------------------------------------------------------------------------------//



}
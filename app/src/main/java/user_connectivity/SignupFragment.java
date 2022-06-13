package user_connectivity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.FirebaseUtils;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


public class SignupFragment extends Fragment {
    private String fullName;
    private String email;
    private String password;
    private EditText emailET;
    private EditText passwordET;
    private EditText fullNameET;
    private SignupFragmentListener listener;
    private FirebaseSignUpUtils firebaseSignUpUtils;


    public SignupFragment() {
        this.fullName = "";
        this.email = "";
        this.password = "";
        firebaseSignUpUtils = new FirebaseSignUpUtils();
    }


    // For communication with the listener activity
    public interface SignupFragmentListener{
        void closeRegistrationScreen();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (SignupFragmentListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'SignupFragmentListener'");
        }
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullNameET = (EditText) view.findViewById(R.id.Signup_FullNameET);
        emailET = (EditText) view.findViewById(R.id.Signup_EmailET);
        passwordET = (EditText) view.findViewById(R.id.Signup_PasswordET);
        setRegisterBtnListener(view);
    }




    private void setRegisterBtnListener(View view){
        ((Button) view.findViewById(R.id.Signup_RegisterBtn)).setOnClickListener(btnView -> {
            fullName = getTextFromEditText(fullNameET);
            email = getTextFromEditText(emailET);
            password = getTextFromEditText(passwordET);

            if(fullName.isEmpty()) {
                fullNameET.setError("Please enter your full name");
                fullNameET.requestFocus();
                return;
            }

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
            firebaseSignUpUtils.askToCreateNewUser(email, fullName, password);
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


    //----------------------------------------------------- FirebaseSignUpUtils class -----------------------------------------------------------//

    private class FirebaseSignUpUtils extends FirebaseUtils {

        public void askToCreateNewUser(String email, String fullName, String password){
            userAuthentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            String currentUserId = userAuthentication.getCurrentUser().getUid();
                            Commons.setCurrentUser(new User(currentUserId, email, fullName, User.EUserStatus.OFFLINE, false));

                            updateUserInDB(Commons.getCurrentUser());
                            sendVerificationEmail();

                            listener.closeRegistrationScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            popToast("Authentication failed.");
                        }
                    }
                });
        }

        public void sendVerificationEmail(){
            FirebaseUser currentUser = userAuthentication.getCurrentUser();
            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    popToast("Verification email was sent to your email address. Please check your inbox to verify your signup");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    popToast("An error occured while trying to send you a Verification email, please try again");
                }
            });
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------//

}
package user_connectivity;

import androidx.annotation.NonNull;
import com.example.tic_tac_toe.User;
import com.google.firebase.database.DataSnapshot;

public class Invitation {
    private User host;
    private User guest;
    private EInvitationStatus status;


    public Invitation(User host, User guest, EInvitationStatus status) {
        this.host = host;
        this.guest = guest;
        this.status = status;
    }


    public Invitation(DataSnapshot snapshot){
        this.host = new User(snapshot.child("host"));
        this.guest = new User(snapshot.child("guest"));
        this.status = EInvitationStatus.valueOf(snapshot.child("status").getValue(String.class));
    }


    public enum EInvitationStatus{
        PENDING("PENDING"),
        CONFIRMED("CONFIRMED"),
        REJECTED("REJECTED"),
        CANCELED("CANCELED");
        private final String status;

        EInvitationStatus(final String type) {
            this.status = type;
        }
        public boolean equals(EInvitationStatus status1, EInvitationStatus status2) {return status1.toString().equals(status2);}
        @NonNull
        @Override
        public String toString() {
            return status;
        }
    }



    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(EInvitationStatus status) {
        this.status = status;
    }


    public boolean wasConfirmed(){
        if(status != null){
            return status.equals(EInvitationStatus.CONFIRMED);
        }
        throw new NullPointerException("Class Invitation: {In method: isNew}: attribute status is null");
    }


    public boolean wasRejected(){
        if(status != null){
            return status.equals(EInvitationStatus.REJECTED);
        }
        throw new NullPointerException("Class Invitation: {In method: isNew}: attribute status is null");
    }


    public boolean wasCanceled(){
        if(status != null){
            return status.equals(EInvitationStatus.CANCELED);
        }
        throw new NullPointerException("Class Invitation: {In method: isNew}: attribute status is null");
    }



}

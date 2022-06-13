package user_connectivity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.FirebaseUtils;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConnectedUsersFragment extends Fragment {
    private static final int VERTICAL_ITEM_SPACE = 35;
    private ConnectedUsersFragmentListener listener;
    private User associatedUser;
    private Invitation achievedInvitation;
    private Invitation deliveredInvitation;
    private LifeCycleListener lifeCycleListener;

    public ConnectedUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (ConnectedUsersFragmentListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'ConnectedUsersFragmentListener'");
        }
        super.onAttach(context);
    }


    public interface ConnectedUsersFragmentListener{
        void askToPerformOnlineGame(User host, User guest);
        void openPendingInvitationDialog();
        void terminatePendingInvitationDialog();
        void openInvitationAchievedDialog(String hostFullName);
        void terminateInvitationAchievedDialog();
    }

    public interface LifeCycleListener{
        void stopAllAsynchronousTasks();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void cancelPendingInvitation(){
        FirebaseUtils firebaseUtils = new FirebaseUtils();
        deliveredInvitation = null;
        firebaseUtils.removeGameInvitationFromDB(associatedUser);
        Commons.updateCurrentUserStatus(User.EUserStatus.AVAILABLE);
        FirebaseUtils.updateUserStatusInDB(associatedUser, User.EUserStatus.AVAILABLE);
        associatedUser = null;
    }

    public void confirmAchievedInvitation(){
        FirebaseUtils firebaseUtils = new FirebaseUtils();
        achievedInvitation.setStatus(Invitation.EInvitationStatus.CONFIRMED);
        firebaseUtils.updateGameInvitation(achievedInvitation);
        achievedInvitation = null;
        listener.askToPerformOnlineGame(associatedUser, Commons.getCurrentUser());
    }

    public void rejectAchievedInvitation(){
        FirebaseUtils firebaseUtils = new FirebaseUtils();
        achievedInvitation.setStatus(Invitation.EInvitationStatus.REJECTED);
        firebaseUtils.updateGameInvitation(achievedInvitation);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_connected_users, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView connectedUsersRecyclerView =  getActivity().findViewById(R.id.ConnectedUsers_recyclerView);
        ConnectedUsersAdapter CountriesAdapter = new ConnectedUsersAdapter(getContext());
        // Attach the adapter to the recyclerview to populate items
        connectedUsersRecyclerView.setAdapter(CountriesAdapter);
        // Set layout manager to position the items
        connectedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        connectedUsersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.bottom = VERTICAL_ITEM_SPACE;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifeCycleListener.stopAllAsynchronousTasks();
    }


    //-------------------------------------------------- ConnectedUsersAdapter class ---------------------------------------------------------//


    public class ConnectedUsersAdapter extends RecyclerView.Adapter<ConnectedUsersAdapter.ConnectedUserViewHolder> {
        private List<User> connectedUsersList;
        FirebaseConnectedUsersUtils firebaseConnectedUsersUtils;


        public ConnectedUsersAdapter(Context context) {
            this.connectedUsersList = new ArrayList<>();
            firebaseConnectedUsersUtils = new FirebaseConnectedUsersUtils();
            firebaseConnectedUsersUtils.addConnectedUsersListener();
            firebaseConnectedUsersUtils.addReceivedInvitationsListener();
            firebaseConnectedUsersUtils.addInvitationsResponseListener();
        }


        private void updateConnectedUsersList(Map<String, Object> usersMap){
            List<User> currentConnectedUsers = new ArrayList<>();
            for(Map.Entry<String, Object> current : usersMap.entrySet()) {
                Map<String, Object> currentUserMap = new HashMap<>((HashMap<String, Object>) current.getValue());
                User user = new User(currentUserMap);
                if(!user.getId().equals(Commons.getCurrentUser().getId()))
                    currentConnectedUsers.add(user);
            }
            this.connectedUsersList = currentConnectedUsers;
            notifyDataSetChanged();
        }



        private void updateConnectedUsersList(DataSnapshot snapshot) {
            List<User> currentConnectedUsers = new ArrayList<>();
            for (DataSnapshot currentRecord : snapshot.getChildren()) {
                User user = new User(currentRecord);
                if(!user.equals(Commons.getCurrentUser()))
                    currentConnectedUsers.add(user);
            }
            this.connectedUsersList = currentConnectedUsers;
            notifyDataSetChanged();
        }




        //-------------------------------------------- ConnectedUserViewHolder class ------------------------------------------------//


        public class ConnectedUserViewHolder extends RecyclerView.ViewHolder{
            public View itemView;
            public TextView userNameTV;
            public TextView emailTV;

            public ConnectedUserViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                this.userNameTV = itemView.findViewById(R.id.connectedUserNameTV);
                this.emailTV =itemView.findViewById(R.id.connectedUserEmailTV);
            }

            public void fillData(int index){
                User user = connectedUsersList.get(index);
                userNameTV.setText(user.getFullName());
                emailTV.setText(user.getEmail());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        associatedUser = user;
                        Commons.updateCurrentUserStatus(User.EUserStatus.UNAVAILABLE);
                        FirebaseUtils.updateUserStatusInDB(associatedUser, User.EUserStatus.UNAVAILABLE);
                        deliveredInvitation = new Invitation(Commons.getCurrentUser(), associatedUser, Invitation.EInvitationStatus.PENDING);
                        firebaseConnectedUsersUtils.addGameInvitation(deliveredInvitation);
                        listener.openPendingInvitationDialog();
                    }
                });
            }

        }



        @NonNull
        @Override
        public ConnectedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View connectedUserView = inflater.inflate(R.layout.item_connected_user, parent, false);

            // Return a new holder instance
            ConnectedUsersAdapter.ConnectedUserViewHolder viewHolder = new ConnectedUsersAdapter.ConnectedUserViewHolder(connectedUserView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ConnectedUserViewHolder holder, int position) {
            holder.fillData(position);
        }

        @Override
        public int getItemCount() {
            return connectedUsersList.size();
        }









        //--------------------------------------------------- FirebaseConnectedUsersUtils class -----------------------------------------------------//




        private class FirebaseConnectedUsersUtils extends FirebaseUtils implements LifeCycleListener{
            private ValueEventListener connectedUsersListener;
            private ValueEventListener invitationsResponseListener;
            private ValueEventListener receivedInvitationsListener;
            private DatabaseReference invitationsEntryReference, usersEntryReference;


            public FirebaseConnectedUsersUtils() {
                this.invitationsEntryReference = databaseReference.child("invitations");
                this.usersEntryReference = databaseReference.child("users");
                this.connectedUsersListener = initConnectedUsersListener();
                this.receivedInvitationsListener = initReceivedInvitationsListener();
                this.invitationsResponseListener = initInvitationsResponseListener();
                lifeCycleListener = this;
            }

            @Override
            public void stopAllAsynchronousTasks() {
                removeAllListeners();
            }


            public void addConnectedUsersListener() {
                usersEntryReference.orderByChild("status").equalTo(User.EUserStatus.AVAILABLE.toString()).addValueEventListener(connectedUsersListener);
            }

            public void removeConnectedUsersListener() {
                usersEntryReference.orderByChild("status").equalTo(User.EUserStatus.AVAILABLE.toString()).removeEventListener(connectedUsersListener);
            }

            public void addReceivedInvitationsListener() {
                invitationsEntryReference.addValueEventListener(receivedInvitationsListener);

            }

            public void removeReceivedInvitationsListener() {
                invitationsEntryReference.removeEventListener(receivedInvitationsListener);
            }

            public void addInvitationsResponseListener() {
                invitationsEntryReference.addValueEventListener(invitationsResponseListener);
            }

            public void removeInvitationsResponseListener() {
                invitationsEntryReference.removeEventListener(invitationsResponseListener);
            }

            public void removeAllListeners(){
                removeConnectedUsersListener();
                removeReceivedInvitationsListener();
                removeInvitationsResponseListener();
            }


            private ValueEventListener initConnectedUsersListener(){
                return new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        updateConnectedUsersList(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w("FirebaseConnectedUsersUtils", "Failed to read value.", error.toException());
                    }
                };
            }


            private ValueEventListener initReceivedInvitationsListener(){
                return new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        DataSnapshot currentUserInvitations = snapshot.child(Commons.getCurrentUser().getId()); // Invitations i got
                        if (currentUserInvitations.exists()) {
                            if (associatedUser == null) {
                                User host = new User(currentUserInvitations.child("host"));
                                User guest = new User(currentUserInvitations.child("guest"));
                                String status = currentUserInvitations.child("status").getValue(String.class);
                                associatedUser = host; // Saves locally the user that sent me the invitation
                                if (achievedInvitation == null) {
                                    achievedInvitation = new Invitation(host, guest, Invitation.EInvitationStatus.valueOf(status));
                                    listener.openInvitationAchievedDialog(host.getFullName());
                                }
                            }
                        } else if (associatedUser != null && achievedInvitation != null && achievedInvitation.getGuest().getId().equals(Commons.getCurrentUser().getId())) {
                            Commons.updateCurrentUserStatus(User.EUserStatus.AVAILABLE);
                            updateUserStatusInDB(associatedUser, User.EUserStatus.AVAILABLE);
                            if(!achievedInvitation.getStatus().equals(Invitation.EInvitationStatus.REJECTED))
                                Toast.makeText(requireContext(), "The invitation was canceled by " + associatedUser.getFullName(), Toast.LENGTH_SHORT).show();
                            achievedInvitation = null;
                            associatedUser = null;
                            listener.terminateInvitationAchievedDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FirebaseConnectedUsersUtils", "Failed to read value.", error.toException());
                    }
                };
            }


            private ValueEventListener initInvitationsResponseListener() {
                return new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        DataSnapshot associatedUserInvitations; // Invitations i sent
                        if (associatedUser != null) { // if this is not the initialization of this class
                            associatedUserInvitations = dataSnapshot.child(associatedUser.getId());
                            if (associatedUserInvitations.exists()) {
                                String status = associatedUserInvitations.child("status").getValue(String.class);
                                if (!Invitation.EInvitationStatus.valueOf(status).equals(Invitation.EInvitationStatus.PENDING)) {
                                    deliveredInvitation = null;
                                    removeGameInvitationFromDB(associatedUser);
                                    if (Invitation.EInvitationStatus.valueOf(status).equals(Invitation.EInvitationStatus.CONFIRMED)) {
                                        listener.askToPerformOnlineGame(Commons.getCurrentUser(), associatedUser);
                                    } else if (Invitation.EInvitationStatus.valueOf(status).equals(Invitation.EInvitationStatus.REJECTED)) {
                                        Toast.makeText(requireContext(), associatedUser.getFullName() + " rejected your invitation.", Toast.LENGTH_SHORT).show();
                                        associatedUser = null;
                                        listener.terminatePendingInvitationDialog();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("FirebaseConnectedUsersUtils", "Failed to read value.", error.toException());
                    }
                };
            }

        }

    }

}

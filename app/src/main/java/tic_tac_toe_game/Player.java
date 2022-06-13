package tic_tac_toe_game;

import androidx.annotation.Nullable;

import com.example.tic_tac_toe.User;

public class Player extends User {
    private int winningsNum;
    private boolean gameHost;
    private boolean activeInGame;

    public Player() {
        super();
        // Default constructor required for calls to DataSnapshot.getValue(User.class) from Firebase
    }

    public Player(String id, String email, String fullName, String status, boolean isVerified, boolean gameHost) {
        super(id, email, fullName, status, isVerified);
        this.gameHost = gameHost;
        this.winningsNum = 0;
    }

    public int getWinningsNum() {
        return winningsNum;
    }

    public void setWinningsNum(int winningsNum) {
        this.winningsNum = winningsNum;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null)
            throw new NullPointerException();
        if(!(obj instanceof Player))
            throw new IllegalArgumentException();
        Player other = (Player) obj;
        return this.getId().equals(other.getId());
    }


    public boolean represents(User user){
        return getId().equals(user.getId());
    }


    public Player(User user, boolean isHost){
        this(user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                user.getIsVerified(),
                isHost);
    }


    public boolean isGameHost() {
        return gameHost;
    }
}

package tic_tac_toe_game;

import androidx.annotation.NonNull;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.User;

import user_connectivity.Invitation;


public class Game {
    private User host;
    private User guest;
    private EGameType gameType;
    private Board board;
    private int hostWinningsNum;
    private int guestWinningsNum;

    public Game(Player host, Player guest, Board board, EGameType gameType) {
        this.host = host;
        this.guest = guest;
        this.board = board;
        this.gameType = gameType;
        this.hostWinningsNum = 0;
        this.guestWinningsNum = 0;
    }

    public Game(User host, User guest, Board board, EGameType gameType){
        this.host = host;
        this.guest = guest;
        this.board = board;
        this.gameType = gameType;
        this.hostWinningsNum = 0;
        this.guestWinningsNum = 0;
    }


    public enum EGameType{
        OFFLINE("OFFLINE"),
        ONLINE("ONLINE");
        private final String type;

        EGameType(final String type) {
            this.type = type;
        }
        public boolean equals(EGameType type1, EGameType type2) {return type1.toString().equals(type2);}
        @NonNull
        @Override
        public String toString() {
            return type;
        }
    }





    public User getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(Player guest) {
        this.guest = guest;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public EGameType getGameType() {
        return gameType;
    }

    public void setGameType(EGameType gameType) {
        this.gameType = gameType;
    }

    public int getHostWinningsNum() {
        return hostWinningsNum;
    }

    public void setHostWinningsNum(int hostWinningsNum) {
        this.hostWinningsNum = hostWinningsNum;
    }

    public int getGuestWinningsNum() {
        return guestWinningsNum;
    }

    public void setGuestWinningsNum(int guestWinningsNum) {
        this.guestWinningsNum = guestWinningsNum;
    }

    public void increaseHostWinningsNum(){
        hostWinningsNum++;
    }

    public void increaseGuestWinningsNum(){
        guestWinningsNum++;
    }

}

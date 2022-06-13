package tic_tac_toe_game;

public class FirebaseGameEntry {
    private String gameId;
    private String hostId;
    private String guestId;
    private String hostPlayingStatus;
    private String guestPlayingStatus;
    private Mark.EMark hostMarkSign;
    private Mark.EMark guestMarkSign;
    private Integer lastMoveRowIdx;
    private Integer lastMoveColIdx;
    private String currentTurnUserId;


    public FirebaseGameEntry(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class) from Firebase
    }

    public FirebaseGameEntry(String gameId, String hostId, String guestId, Mark.EMark hostMarkSign, Mark.EMark guestMarkSign, String currentTurnUserId) {
        this.gameId = gameId;
        this.hostId = hostId;
        this.guestId = guestId;
        this.hostPlayingStatus = "active";
        this.guestPlayingStatus = "active";
        this.hostMarkSign = hostMarkSign;
        this.guestMarkSign = guestMarkSign;
        //this.board = boardTemplate;
        this.lastMoveRowIdx = null;
        this.lastMoveColIdx = null;
        this.currentTurnUserId = currentTurnUserId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getHostPlayingStatus() {
        return hostPlayingStatus;
    }

    public void setHostPlayingStatus(String hostPlayingStatus) {
        this.hostPlayingStatus = hostPlayingStatus;
    }

    public String getGuestPlayingStatus() {
        return guestPlayingStatus;
    }

    public void setGuestPlayingStatus(String guestPlayingStatus) {
        this.guestPlayingStatus = guestPlayingStatus;
    }

    public void handlePlayerLeaving(Player player){
        if (player.isGameHost()) {
            setHostPlayingStatus("left");
        } else {
            setGuestPlayingStatus("left");
        }
    }

    public Mark.EMark getHostMarkSign() {
        return hostMarkSign;
    }

    public void setHostMarkSign(Mark.EMark hostMarkSign) {
        this.hostMarkSign = hostMarkSign;
    }

    public Mark.EMark getGuestMarkSign() {
        return guestMarkSign;
    }

    public void setGuestMarkSign(Mark.EMark guestMarkSign) {
        this.guestMarkSign = guestMarkSign;
    }

    public String getCurrentTurnUserId() {
        return currentTurnUserId;
    }

    public void setCurrentTurnUserId(String currentTurnUserId) {
        this.currentTurnUserId = currentTurnUserId;
    }

    public Integer getLastMoveRowIdx() {
        return lastMoveRowIdx;
    }

    public void setLastMoveRowIdx(Integer lastMoveRowIdx) {
        this.lastMoveRowIdx = lastMoveRowIdx;
    }

    public Integer getLastMoveColIdx() {
        return lastMoveColIdx;
    }

    public void setLastMoveColIdx(Integer lastMoveColIdx) {
        this.lastMoveColIdx = lastMoveColIdx;
    }

}

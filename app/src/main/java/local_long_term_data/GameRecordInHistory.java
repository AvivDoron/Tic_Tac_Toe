package local_long_term_data;


import helpers.DateAndTime;

public class GameRecordInHistory {
    private String gameId;
    private Long dateAndTimeInMilliSecs;
    private String hostFullName;
    private String guestFullName;
    private int hostWinningsNum;
    private int guestWinningsNum;


    public GameRecordInHistory(String gameId, Long dateAndTimeInMilliSecs, String hostFullName, String guestFullName, int hostWinningsNum, int guestWinningsNum) {
        this.gameId = gameId;
        this.dateAndTimeInMilliSecs = dateAndTimeInMilliSecs;
        this.hostFullName = hostFullName;
        this.guestFullName = guestFullName;
        this.hostWinningsNum = hostWinningsNum;
        this.guestWinningsNum = guestWinningsNum;
    }


    public GameRecordInHistory(String hostFullName, String guestFullName) {
        this.gameId = "";
        this.dateAndTimeInMilliSecs = DateAndTime.getCurrentDateAndTimeInMillis();
        this.hostFullName = hostFullName;
        this.guestFullName = guestFullName;
        this.hostWinningsNum = 0;
        this.guestWinningsNum = 0;
    }



    public GameRecordInHistory(String hostFullName, String guestFullName, int hostWinningsNum, int guestWinningsNum) {
        this.gameId = "";
        this.hostFullName = hostFullName;
        this.guestFullName = guestFullName;
        this.hostWinningsNum = hostWinningsNum;
        this.guestWinningsNum = guestWinningsNum;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Long getDateAndTimeInMilliSecs() {
        return dateAndTimeInMilliSecs;
    }

    public void setDateAndTimeInMilliSecs(Long dateAndTimeInMilliSecs) {
        this.dateAndTimeInMilliSecs = dateAndTimeInMilliSecs;
    }

    public String getHostFullName() {
        return hostFullName;
    }

    public void setHostFullName(String hostFullName) {
        this.hostFullName = hostFullName;
    }

    public String getGuestFullName() {
        return guestFullName;
    }

    public void setGuestFullName(String guestFullName) {
        this.guestFullName = guestFullName;
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

}

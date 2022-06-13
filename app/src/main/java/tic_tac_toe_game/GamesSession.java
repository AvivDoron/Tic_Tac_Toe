package tic_tac_toe_game;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class GamesSession implements IGameManager{
    protected Player player, rival;
    protected IGameManagerListener iGameManagerListener;
    protected Map<Integer, String> turnsByIDMap;
    protected int turnIdx;
    protected Map<String, Mark.EMark> markSignsByIDMap;
    protected Board board;
    //protected boolean gameIsActive;


    public GamesSession(Game game, IGameManagerListener iGameManagerListener){
        User currentUser;
        User rivalUser;

        if(game.getGameType().equals(Game.EGameType.ONLINE)) {
            currentUser = Commons.getCurrentUser();
            rivalUser = getRival(game);
            this.player =  new Player(currentUser, isUserHost(game, currentUser.getId()));
            this.rival = new Player(rivalUser, isUserHost(game, rivalUser.getId()));
        }
        else{
            this.player =  new Player(game.getHost(), true);
            this.rival = new Player(game.getGuest(), false);
        }
        this.iGameManagerListener = iGameManagerListener;
        this.board = game.getBoard();
        this.turnsByIDMap = new HashMap<>();
        this.turnsByIDMap.put(0, player.getId());
        this.turnsByIDMap.put(1, rival.getId());
        this.turnIdx = 0;
        //this.gameIsActive = true;

    }


    public abstract Mark.EMark getPlayerMark(String id);


    private boolean isUserHost(Game game, String id){
        return id.equals(game.getHost().getId());
    }


    protected Map<String, Mark.EMark> initRandomMarkSignsByIDMap(){
        Map<String, Mark.EMark> randomMarkSignsByIDMap = new HashMap<>();
        Mark.EMark randomizedMarkSign = randomBetweenTwo(Mark.EMark.X, Mark.EMark.O);
        Mark.EMark otherMarkSign = randomizedMarkSign.equals(Mark.EMark.X) ? Mark.EMark.O : Mark.EMark.X;
        randomMarkSignsByIDMap.put(player.getId(), randomizedMarkSign);
        randomMarkSignsByIDMap.put(rival.getId(), otherMarkSign);
        return randomMarkSignsByIDMap;
    }


    private <T> T randomBetweenTwo(T first, T second){
        Random random = new Random();
        Object[] arr = {first,second};
        T[] marksCharArr = (T[]) arr;
        T randomized = marksCharArr[random.nextInt(marksCharArr.length)];
        return randomized;
    }


    public User getRival(Game game) {
        User host = game.getHost();
        User guest = game.getGuest();
        return Commons.getCurrentUser().getId().equals(host.getId()) ? guest : host;
    }


    public void handleTurnTimeOutEvent(){
        if(isItCurrentPlayerTurn())
            increaseWinningsAmount(rival);
        else
            increaseWinningsAmount(player);
    }

    public void applyPlayerGiveUpProcedure() {
        increaseWinningsAmount(rival);
    }

    public void handleRivalLeftEvent(){
        giveCurrentPlayerExtraPoint();
    }


    public void clearAllBoardMarks(Board board){
        board.clearAll();
    }


    public Player getCurrentTurnPlayer(){
        return (getTurnsByIDMap().get(getTurnIdx()).equals(player.getId()) ? player : rival);
    }


    public boolean isItCurrentPlayerTurn(){
        Player currentTurnPlayer = getCurrentTurnPlayer();
        return currentTurnPlayer.equals(player);
    }


    public void handleTurnConsequences(Board board, Player currentTurnPlayer) {
        boolean currentTurnPlayerWon = isPlayerWon(board, currentTurnPlayer);
        boolean boardFull = board.isFull();

        if(currentTurnPlayerWon || boardFull){
            if(currentTurnPlayerWon) {
                increaseWinningsAmount(currentTurnPlayer);
            }
            initializeBoard(board);
        }
        finishTurn();
    }


    public void finishTurn() {
        if(isItCurrentPlayerTurn())
            iGameManagerListener.handleCurrentPlayerTurnFinished();
        else
            iGameManagerListener.handleCurrentPlayerTurnArrived();

        proceedToNextTurn();
    }


    private void proceedToNextTurn(){
        int turnIdx = getTurnIdx();
        setTurnIdx((turnIdx + 1) % 2);
    }


    public void giveRivalExtraPoint(){
        increaseWinningsAmount(rival);
    }


    public void giveCurrentPlayerExtraPoint(){
        increaseWinningsAmount(player);
    }


    @Override
    public void increaseWinningsAmount(Player player) {
        int playerWinnings = player.getWinningsNum();
        player.setWinningsNum(++playerWinnings);
        iGameManagerListener.updatePlayerWinningsAmount(player);
    }

    @Override
    public boolean isPlayerWon(Board board, Player player) {
        for(int i = 0 ; i < board.getDimension() ; i++) {
            if (board.colContainsNumOfMarks(i, getPlayerMark(player.getId()), board.getDimension()) ||
                    board.rowContainsNumOfMarks(i, getPlayerMark(player.getId()), board.getDimension())) {
                return true;
            }
        }
        if(board.mainDiagContainsNumOfMarks(getPlayerMark(player.getId()), board.getDimension()) ||
                board.secondDiagContainsNumOfMarks(getPlayerMark(player.getId()), board.getDimension())) {
            return true;
        }
        return false;
    }

    protected Map<Integer, String> getTurnsByIDMap() {
        return turnsByIDMap;
    }

    protected int getTurnIdx() {
        return turnIdx;
    }

    protected void setTurnIdx(int turnIdx) {
        this.turnIdx = turnIdx;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getRival() {
        return rival;
    }

}



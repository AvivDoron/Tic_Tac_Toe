package tic_tac_toe_game;



public interface IGameManager {
    void increaseWinningsAmount(Player player);
    boolean isPlayerWon(Board board, Player player);
    void makeMove(int i, int j);
    void initializeBoard(Board board);
    void handleTurnConsequences(Board board, Player player);
    void finishTurn();
    Player getCurrentTurnPlayer();
    Player getPlayer();
    Player getRival();
    boolean isItCurrentPlayerTurn();
    Mark.EMark getPlayerMark(String id);
    void handleTurnTimeOutEvent();
    void applyPlayerGiveUpProcedure();
    void giveRivalExtraPoint();
    void giveCurrentPlayerExtraPoint();
    void handleRivalLeftEvent();


    interface IGameManagerListener{
        void updatePlayerWinningsAmount(Player player);
        void handleCurrentPlayerTurnArrived();
        void handleCurrentPlayerTurnFinished();
        void handleRivalLeftEvent();
    }
}

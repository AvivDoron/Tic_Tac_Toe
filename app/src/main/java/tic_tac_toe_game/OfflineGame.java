package tic_tac_toe_game;

import com.example.tic_tac_toe.User;

import java.util.Random;


public class OfflineGame extends GamesSession {
    SystemPlayer rival;

    public OfflineGame(Game game, IGameManagerListener iGameManagerListener) {
        super(game, iGameManagerListener);
        this.rival = new SystemPlayer(game.getGuest());
        this.turnIdx = 0;
        this.markSignsByIDMap = initRandomMarkSignsByIDMap();
    }

    @Override
    public Mark.EMark getPlayerMark(String id) {
        return markSignsByIDMap.get(id);
    }

    @Override
    public void handleTurnTimeOutEvent() {
        super.handleTurnTimeOutEvent();
    }

    @Override
    public void applyPlayerGiveUpProcedure() {
        super.applyPlayerGiveUpProcedure();
    }

    @Override
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

    @Override
    public void makeMove(int i, int j) {
        board.set(i, j, getPlayerMark(player.getId()));
        handleTurnConsequences(board, player);
        rival.response(board);
        handleTurnConsequences(board, rival);
    }

    @Override
    public void initializeBoard(Board board) {
        clearAllBoardMarks(board);
    }

    @Override
    public SystemPlayer getRival() {
        return this.rival;
    }


    private class SystemPlayer extends Player{

        public SystemPlayer(User user){
            super(user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getStatus(),
                    user.getIsVerified(),
                    false);
        }

        public void response(Board board){

            if(checkMajorityInMainDiag(board, getPlayerMark(getId()), getPlayerMark(player.getId()))) {
                putMarkInMainDiag(board);
                return;
            }

            if((checkMajorityInSecondDiag(board, getPlayerMark(getId()), getPlayerMark(player.getId())))) {
                putMarkInSecondDiag(board);
                return;
            }

            for(int i = 0 ; i < board.getDimension() ; i++){
                if(checkMajorityInCol(board, i, getPlayerMark(getId()), getPlayerMark(player.getId()))) {
                    putMarkInCol(board, i);
                    return;
                }
                if(checkMajorityInRow(board, i, getPlayerMark(getId()), getPlayerMark(player.getId()))) {
                    putMarkInRow(board, i);
                    return;
                }
            }

            if(checkMajorityInMainDiag(board, getPlayerMark(player.getId()), getPlayerMark(getId()))) {
                putMarkInMainDiag(board);
                return;
            }

            if((checkMajorityInSecondDiag(board, getPlayerMark(player.getId()), getPlayerMark(getId())))) {
                putMarkInSecondDiag(board);
                return;
            }

            for(int i = 0 ; i < board.getDimension() ; i++){
                if(checkMajorityInCol(board, i, getPlayerMark(player.getId()), getPlayerMark(getId()))) {
                    putMarkInCol(board, i);
                    return;
                }
                if(checkMajorityInRow(board, i, getPlayerMark(player.getId()), getPlayerMark(getId()))) {
                    putMarkInRow(board, i);
                    return;
                }
            }

            putMarkRandomly(board);
        }

        private void putMarkRandomly(Board board){
            Random random = new Random();
            int rowIdx = -1, colIdx = -1;

            while(rowIdx == -1 || colIdx == -1 || !board.isEmpty(rowIdx, colIdx)){
                rowIdx = random.nextInt(board.getDimension());
                colIdx = random.nextInt(board.getDimension());
            }

            board.set(rowIdx, colIdx, getPlayerMark(getId()));
        }


        private boolean putMarkInCol(Board board, int colIdx){
            for(int rowIdx = 0 ; rowIdx < board.getDimension() ; rowIdx++)
                if (board.isEmpty(rowIdx, colIdx)) {
                    board.set(rowIdx, colIdx, getPlayerMark(getId()));
                    return true;
                }
            return false;
        }

        private boolean putMarkInRow(Board board, int rowIdx){
            for(int colIdx = 0 ; colIdx < board.getDimension() ; colIdx++)
                if (board.isEmpty(rowIdx, colIdx)) {
                    board.set(rowIdx, colIdx, getPlayerMark(getId()));
                    return true;
                }
            return false;
        }

        private boolean putMarkInMainDiag(Board board){
            for(int idx = 0 ; idx < board.getDimension() ; idx++)
                if (board.isEmpty(idx, idx)) {
                    board.set(idx, idx, getPlayerMark(getId()));
                    return true;
                }
            return false;
        }


        private boolean putMarkInSecondDiag(Board board){
            for(int idx = 0 ; idx < board.getDimension() ; idx++)
                    if (board.isEmpty(idx, board.getDimension() - idx - 1)) {
                        board.set(idx, board.getDimension() - idx - 1, getPlayerMark(getId()));
                        return true;
                    }
            return false;
        }


        private boolean checkMajorityInCol(Board board, int colIdx, Mark.EMark checkedMark, Mark.EMark otherMark){
            boolean colContainsTwoOwnedMarks =  board.colContainsNumOfMarks(colIdx, checkedMark, 2);
            if(colContainsTwoOwnedMarks){
                boolean colNotContainsOtherMarks = board.colContainsNumOfMarks(colIdx, otherMark, 0);
                if(colNotContainsOtherMarks)
                    return true;
            }
            return false;
        }


        private boolean checkMajorityInRow(Board board, int rowIdx, Mark.EMark checkedMark, Mark.EMark otherMark){
            boolean rowContainsTwoOwnedMarks =  board.rowContainsNumOfMarks(rowIdx, checkedMark, 2);
            if(rowContainsTwoOwnedMarks){
                boolean rowNotContainsOtherMarks = board.rowContainsNumOfMarks(rowIdx, otherMark, 0);
                if(rowNotContainsOtherMarks)
                    return true;
            }
            return false;
        }


        private boolean checkMajorityInMainDiag(Board board, Mark.EMark checkedMark, Mark.EMark otherMark){
            boolean mainDiagContainsTwoOwnedMarks =  board.mainDiagContainsNumOfMarks(checkedMark, 2);
            if(mainDiagContainsTwoOwnedMarks){
                boolean mainDiagNotContainsOtherMarks = board.mainDiagContainsNumOfMarks(otherMark, 0);
                if(mainDiagNotContainsOtherMarks)
                    return true;
            }
            return false;
        }


        private boolean checkMajorityInSecondDiag(Board board, Mark.EMark checkedMark, Mark.EMark otherMark){
            boolean secondDiagContainsTwoOwnedMarks =  board.secondDiagContainsNumOfMarks(checkedMark, 2);
            if(secondDiagContainsTwoOwnedMarks){
                boolean secondDiagNotContainsOtherMarks = board.secondDiagContainsNumOfMarks(otherMark, 0);
                if(secondDiagNotContainsOtherMarks)
                    return true;
            }
            return false;
        }
    }
}

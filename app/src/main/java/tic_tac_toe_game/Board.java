package tic_tac_toe_game;


/** Board of a single game to play with */
public class Board {
    private Mark[][] board;
    private int dimension;
    private int boardSize;


    public Board(int dimension){
        this.dimension = dimension;
        board = new Mark[dimension][dimension];
    }

    /** init place on board with an empty mark */
    public boolean initMark(int i, int j, Mark mark) {
        if (isEmpty(i, j)) {
            board[i][j] = mark;
            return true;
        }
        return false;
    }

    /** set mark in a place on the board */
    public boolean set(int i, int j, Mark.EMark markSign) {
        if (isEmpty(i, j)) {
            board[i][j].set(markSign);
            this.boardSize++;
            return true;
        }
        return false;
    }

    /** Get the mark placed on the board */
    public Mark get(int i, int j) {
        return board[i][j];
    }

    /** Remove all the mark from the board */
    public void clearAll(){
        initSize();
        for(int i = 0 ; i < dimension ; i++)
            for(int j = 0 ; j < dimension ; j++) {
                board[i][j].clear();
            }
    }

    /** Checks if given column contains given number of the same kind of given mark */
    public boolean colContainsNumOfMarks(int colIdx, Mark.EMark markChar, int amount){
        int appearanceCnt = 0;

        for(int rowIdx = 0 ; rowIdx < dimension ; rowIdx++){
            if (!isEmpty(rowIdx, colIdx)) {
                Mark.EMark currentMarkChar = get(rowIdx, colIdx).getMarkSign();
                appearanceCnt += currentMarkChar.equals(markChar) ? 1 : 0;
            }
        }
        return appearanceCnt == amount;
    }

    /** Checks if given row contains given number of the same kind of given mark */
    public boolean rowContainsNumOfMarks(int rowIdx, Mark.EMark markChar, int amount) {
        int appearanceCnt = 0;

        for(int colIdx = 0 ; colIdx < dimension ; colIdx++){
            if (!isEmpty(rowIdx, colIdx)) {
                Mark.EMark currentMarkChar = get(rowIdx, colIdx).getMarkSign();
                appearanceCnt += currentMarkChar.equals(markChar) ? 1 : 0;
            }
        }
        return appearanceCnt == amount;
    }

    /** Checks if the main diagonal contains given number of the same kind of given mark */
    public boolean mainDiagContainsNumOfMarks(Mark.EMark markChar, int amount) {
        int appearanceCnt = 0;

        for(int idx = 0 ; idx < dimension ; idx++){
            if (!isEmpty(idx, idx)) {
                Mark.EMark currentMarkChar = get(idx, idx).getMarkSign();
                appearanceCnt += currentMarkChar.equals(markChar) ? 1 : 0;
            }
        }
        return appearanceCnt == amount;
    }

    /** Checks if the second diagonal contains given number of the same kind of given mark */
    public boolean secondDiagContainsNumOfMarks(Mark.EMark markChar, int amount) {
        int appearanceCnt = 0;

        for(int idx = 0 ; idx < dimension ; idx++){
            if (!isEmpty(idx, dimension - idx - 1)) {
                Mark.EMark currentMarkChar = get(idx, dimension - idx - 1).getMarkSign();
                appearanceCnt += currentMarkChar.equals(markChar) ? 1 : 0;
            }
        }
        return appearanceCnt == amount;
    }

    public int getDimension(){
        return this.dimension;
    }

    public boolean isEmpty(int i, int j) {
        if(!(isLegalIndex(i) && isLegalIndex(j)))
            return false;
        return (board[i][j] == null || !board[i][j].isMarked());
    }

    private boolean isLegalIndex(int index){
        return index >= 0 && index < board.length;
    }

    /** Checks if The board full of marks */
    public boolean isFull() {
        return (boardSize == dimension * dimension);
    }

    /** Initiate the number of marks set on the board */
    private void initSize(){
        boardSize = 0;
    }

}

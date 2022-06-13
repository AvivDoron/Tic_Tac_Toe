package tic_tac_toe_game;


import android.util.Log;
import androidx.annotation.NonNull;
import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.FirebaseUtils;
import com.example.tic_tac_toe.MainActivity;
import com.example.tic_tac_toe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import local_long_term_data.GameRecordInHistory;
import local_long_term_data.GamesHistorySharedPreferences;
import local_long_term_data.IGamesHistoryManager;


public class OnlineGame extends GamesSession {
    private FirebaseGameUtils firebaseGameUtils;
    private FirebaseGameEntry gameEntry;
    private IGamesHistoryManager iGamesHistoryManager;
    private GameRecordInHistory gameRecordInHistory;


    public OnlineGame(Game game, IGameManagerListener iGameManagerListener) {
        super(game, iGameManagerListener);
        this.turnsByIDMap.put(0, player.getId());
        this.turnsByIDMap.put(1, rival.getId());
        this.turnIdx = Commons.getCurrentUser().getId().equals(game.getHost().getId()) ? 0 : 1;
        firebaseGameUtils = new FirebaseGameUtils();
        this.iGamesHistoryManager = new GamesHistorySharedPreferences(MainActivity.mainActivityContext, Commons.getCurrentUser().getId());
        initiateGameRecordInHistory(game);
        if(player.isGameHost()) {
            this.markSignsByIDMap = initRandomMarkSignsByIDMap();
            firebaseGameUtils.addGame(game);
            firebaseGameUtils.addGameChangedListener(board);
        }
        else
            firebaseGameUtils.listenToGameCreation(board);
    }


    protected Map<String, Mark.EMark> initMarkSignsByIDMap(String hostId, Mark.EMark hostMarkSign, String guestId, Mark.EMark guestMarkSign){
        Map<String, Mark.EMark> randomMarkSignsByIDMap = new HashMap<>();
        randomMarkSignsByIDMap.put(hostId, hostMarkSign);
        randomMarkSignsByIDMap.put(guestId, guestMarkSign);
        return randomMarkSignsByIDMap;
    }


    @Override
    public Mark.EMark getPlayerMark(String id) {
        return markSignsByIDMap.get(id);
    }


    @Override
    public void giveRivalExtraPoint(){
        this.increaseWinningsAmount(rival);
    }

    @Override
    public void giveCurrentPlayerExtraPoint(){
        this.increaseWinningsAmount(player);
    }


    @Override
    public void handleTurnTimeOutEvent() {
        if(isItCurrentPlayerTurn())
            giveRivalExtraPoint();
        else
            giveCurrentPlayerExtraPoint();
        firebaseGameUtils.removeGame();
    }


    public void onRivalLeft(){
        super.handleRivalLeftEvent();
        firebaseGameUtils.removeGame();
        iGameManagerListener.handleRivalLeftEvent();
    }


    public void applyPlayerGiveUpProcedure(){

        gameEntry.handlePlayerLeaving(player);
        firebaseGameUtils.updateInDBCurrentPlayerLeaving(gameEntry);
        giveRivalExtraPoint();
    }


    private void applyLeavingProcedure(){
        firebaseGameUtils.removeGame();
        iGameManagerListener.handleRivalLeftEvent();
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

    }


    @Override
    public void makeMove(int i, int j) {
        if(isItCurrentPlayerTurn()) {
            boolean Succeeded = board.set(i, j, getPlayerMark(player.getId()));
            if (Succeeded) {
                gameEntry.setLastMoveRowIdx(i);
                gameEntry.setLastMoveColIdx(j);
                handleTurnConsequences(board, player);
                finishTurn();
                updateCurrentTurn();
            }
        }
    }


    @Override
    public void increaseWinningsAmount(Player player){
        super.increaseWinningsAmount(player);
        int currentPlayerWinningNum = player.getWinningsNum();
        if(player.isGameHost())
            gameRecordInHistory.setHostWinningsNum(currentPlayerWinningNum);
        else
            gameRecordInHistory.setGuestWinningsNum(currentPlayerWinningNum);
        iGamesHistoryManager.UpdateGameInHistory(gameRecordInHistory);
    }

    @Override
    public void initializeBoard(Board board) {
        clearAllBoardMarks(board);

    }

    private void updateCurrentTurn(){
        firebaseGameUtils.updateCurrentTurn();
    }





    //----------------------------------------------- GameRecordInHistory section ----------------------------------------------------//

    private void initiateGameRecordInHistory(Game game){
        String hostFullName = game.getHost().getFullName();
        String guestFullName = game.getGuest().getFullName();
        this.gameRecordInHistory = new GameRecordInHistory(hostFullName, guestFullName);
    }



    //------------------------------------------------ FirebaseGameUtils class ------------------------------------------------------//

    private class FirebaseGameUtils extends FirebaseUtils {
        DatabaseReference gamesEntryReference;

        public FirebaseGameUtils() {
            this.gamesEntryReference = databaseReference.child("games");
        }

        // Guest part
        public void listenToGameCreation(Board board){
            gamesEntryReference.orderByChild("guestId").equalTo(player.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()) { // If the game has already been created in database
                        for(DataSnapshot child : snapshot.getChildren()) {
                            gameEntry = parseGameDataFromDataSnapshot(child);
                            gameRecordInHistory.setGameId(gameEntry.getGameId());
                        }
                        String hostId = gameEntry.getHostId();
                        Mark.EMark hostMarkSign = gameEntry.getHostMarkSign();
                        String guestId = gameEntry.getGuestId();
                        Mark.EMark guestMarkSign = gameEntry.getGuestMarkSign();
                        markSignsByIDMap = initMarkSignsByIDMap(hostId, hostMarkSign, guestId, guestMarkSign);
                        gamesEntryReference.removeEventListener(this);
                        requestRemovingGameOnDisconnect();
                        addGameChangedListener(board);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Host part
        public void addGameChangedListener(Board board){
            gamesEntryReference.child(gameEntry.getGameId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        if (didRivalLeftGame(snapshot)) { // Rival gave up or logged out
                            gamesEntryReference.child(gameEntry.getGameId()).removeEventListener(this);
                            onRivalLeft();
                            return;
                        }
                        String currentTurnUserId = snapshot.child("currentTurnUserId").getValue(String.class);
                        if (currentTurnUserId.equals(player.getId())) {
                            Integer lastMoveRowIdx = snapshot.child("lastMoveRowIdx").getValue(Integer.class);
                            Integer lastMoveColIdx = snapshot.child("lastMoveColIdx").getValue(Integer.class);
                            gameEntry.setLastMoveRowIdx(lastMoveRowIdx);
                            gameEntry.setLastMoveColIdx(lastMoveColIdx);
                            gameEntry.setCurrentTurnUserId(currentTurnUserId);
                            if (isLastMoveOfRivalUpdateNeeded()) {
                                board.set(lastMoveRowIdx, lastMoveColIdx, getPlayerMark(rival.getId()));
                                handleTurnConsequences(board, rival);
                                finishTurn();   // Finish the rival turn.
                            }
                        }
                    }
                    else {
                        gamesEntryReference.child(gameEntry.getGameId()).removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        private boolean isLastMoveOfRivalUpdateNeeded(){
            return isCurrentMoveFirstInSession();
        }

        private boolean isCurrentMoveFirstInSession(){
            return gameEntry.getLastMoveRowIdx() != null && gameEntry.getLastMoveColIdx() != null;
        }


        public void updateCurrentTurn(){
            gameEntry.setCurrentTurnUserId(rival.getId());
            updateGame(gameEntry);
        }

        private void addGame(Game game){
            String key = pushNewGameEntry();
            String hostId = game.getHost().getId();
            String guestId = game.getGuest().getId();
            String currentTurnPlayerId = getCurrentTurnPlayer().getId();
            gameEntry = new FirebaseGameEntry(key, hostId, guestId, markSignsByIDMap.get(hostId),
                    markSignsByIDMap.get(guestId), currentTurnPlayerId);
            gameRecordInHistory.setGameId(gameEntry.getGameId());
            updateGame(gameEntry);
            requestRemovingGameOnDisconnect();
        }

        public void updateGame(FirebaseGameEntry gameEntry) {
            String id = gameEntry.getGameId();
            gamesEntryReference.child(id).setValue(gameEntry);
        }



        public String pushNewGameEntry(){
            String key = gamesEntryReference.push().getKey();
            return key;
        }


        private boolean didRivalLeftGame(DataSnapshot snapshot){
            String requiredStr = getRival().isGameHost() ? "hostPlayingStatus" : "guestPlayingStatus";
            String rivalStatus = snapshot.child(requiredStr).getValue(String.class);
            return rivalStatus.equals("left");
        }


        private FirebaseGameEntry parseGameDataFromDataSnapshot(DataSnapshot dataSnapshot) {
            String id = dataSnapshot.child("gameId").getValue(String.class);
            String hostId = dataSnapshot.child("hostId").getValue(String.class);
            String guestId = dataSnapshot.child("guestId").getValue(String.class);
            Mark.EMark hostMarkSign = Mark.EMark.valueOf(dataSnapshot.child("hostMarkSign").getValue(String.class));
            Mark.EMark guestMarkSign = Mark.EMark.valueOf(dataSnapshot.child("guestMarkSign").getValue(String.class));
            String currentTurnUserId = dataSnapshot.child("currentTurnUserId").getValue(String.class);
            return new FirebaseGameEntry(id, hostId, guestId, hostMarkSign, guestMarkSign, currentTurnUserId);
        }


        private void removeGame(){
            gamesEntryReference.child(gameEntry.getGameId()).removeValue();
        }


        public void requestRemovingGameOnDisconnect(){
            gamesEntryReference.child(gameEntry.getGameId()).onDisconnect().removeValue();
        }

        public void updateInDBCurrentPlayerLeaving(FirebaseGameEntry gameEntry){
            gamesEntryReference.child(gameEntry.getGameId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("FirebaseGameUtils", "Error getting data", task.getException());
                    }
                    else {
                        if (task.getResult().getValue() != null){
                            updateGame(gameEntry);
                        }
                    }
                }
            });
        }
    }
}

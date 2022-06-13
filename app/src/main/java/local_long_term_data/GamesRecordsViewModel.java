package local_long_term_data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.tic_tac_toe.Commons;
import com.example.tic_tac_toe.MainActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helpers.DateAndTime;


public class GamesRecordsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<GameRecordInHistory>> gamesRecordsList;
    private IGamesHistoryManager iGamesHistoryManager;


    public GamesRecordsViewModel(@NonNull Application application) {
        super(application);
        this.gamesRecordsList = new MutableLiveData<List<GameRecordInHistory>>();
        this.iGamesHistoryManager = new GamesHistorySharedPreferences(MainActivity.mainActivityContext, Commons.getCurrentUser().getId());
        initiateGamesRecordsList();
    }

    private void initiateGamesRecordsList(){
        HashMap<String,String> retrievedGameRecordsMap = new HashMap<>(iGamesHistoryManager.getAllGameRecordsFromHistory());
        List<GameRecordInHistory> actualGamesRecordsList = new ArrayList<>();
        for(Map.Entry<String, String> entry : retrievedGameRecordsMap.entrySet()){
            GameRecordInHistory gameRecord = parseGamesRecordsFromString(entry.getKey(), entry.getValue());
            actualGamesRecordsList.add(gameRecord);
        }
        sortListByGamesOccurrenceTime(actualGamesRecordsList);
        this.gamesRecordsList.setValue(actualGamesRecordsList);
    }

    private GameRecordInHistory parseGamesRecordsFromString(String gameId, String gameRecordStr){
        String[] gameRecordStrStrArr = gameRecordStr.split(" ");
        long dateAndTimeInMillisecs = Long.valueOf(gameRecordStrStrArr[0]);
        String hostFullName = adjustPlayerNameForDisplay(gameRecordStrStrArr[1]);
        int hostWinningsNum = Integer.valueOf(gameRecordStrStrArr[2]);
        String guestFullName = adjustPlayerNameForDisplay(gameRecordStrStrArr[3]);
        int guestWinningsNum = Integer.valueOf(gameRecordStrStrArr[4]);
        return new GameRecordInHistory(gameId, dateAndTimeInMillisecs, hostFullName, guestFullName, hostWinningsNum, guestWinningsNum);
    }

    private void sortListByGamesOccurrenceTime(List<GameRecordInHistory> list){
        list.sort(new Comparator<GameRecordInHistory>() {
            @Override
            public int compare(GameRecordInHistory gameRecord1, GameRecordInHistory gameRecord2) {
                return gameRecord1.getDateAndTimeInMilliSecs().compareTo(gameRecord2.getDateAndTimeInMilliSecs());
            }
        });
    }

    public void addGameToHistory(GameRecordInHistory gameRecord){
        iGamesHistoryManager.UpdateGameInHistory(gameRecord);
    }

    public String adjustPlayerNameForDisplay(String playerName) {
        return playerName.replaceAll("_", " ");
    }

    public String adjustDateAndTimeForDisplay(long dateAndTimeInMillisecs){
        String dateAndTimeFormat = DateAndTime.timeInMillisToDateAndTimeFormat(dateAndTimeInMillisecs);
        String[] splitUpBySpaceArr = dateAndTimeFormat.split(" ");
        return String.format(splitUpBySpaceArr[0] + "\n" + splitUpBySpaceArr[1]);
    }

    public void removeGameRecord(int index){
        GameRecordInHistory gameRecordToRemove = this.gamesRecordsList.getValue().get(index);
        removeGameFromHistory(gameRecordToRemove);
        this.gamesRecordsList.getValue().remove(index);
    }

    private void removeGameFromHistory(GameRecordInHistory gameRecordInHistory){
        iGamesHistoryManager.removeGameFromHistory(gameRecordInHistory);
    }

    public MutableLiveData<List<GameRecordInHistory>> getGamesRecordsList() {
        return gamesRecordsList;
    }

    public void clearHistory(){
        iGamesHistoryManager.clearHistory();
        List<GameRecordInHistory> temp = gamesRecordsList.getValue();
        temp.clear();
        gamesRecordsList.setValue(temp);
    }

}

package local_long_term_data;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import java.util.Map;


public class GamesHistorySharedPreferences implements IGamesHistoryManager {
    private SharedPreferences sharedPreferences;
    private final String preferencesSuffixName = "_game_history_preferences";

    public GamesHistorySharedPreferences(Context context, String userId){
        this.sharedPreferences = initiateSharedPreferences(context, userId);
    }

    public SharedPreferences initiateSharedPreferences(Context context, String userId){
        FragmentActivity fragmentActivity = (FragmentActivity) context;
        SharedPreferences sharedPref = fragmentActivity.getSharedPreferences(userId + preferencesSuffixName, Context.MODE_PRIVATE);
        return sharedPref;
    }

    public void UpdateGameInHistory(GameRecordInHistory gameRecordInHistory) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String gameId = gameRecordInHistory.getGameId();
        removeGameFromHistory(gameRecordInHistory);
        String gameRecordAsHistoryPattern = stringifyGameRecord(gameRecordInHistory);
        editor.putString(gameId, gameRecordAsHistoryPattern);
        editor.apply();
    }

    public void removeGameFromHistory(GameRecordInHistory gameRecordInHistory){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String gameId = gameRecordInHistory.getGameId();
        String gameRecordFromHistory = getGameRecordFromHistory(gameId);
        if(gameRecordFromHistory != null) {
            editor.remove(gameId);
            editor.apply();
        }
    }

    public void clearHistory(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private String stringifyGameRecord(GameRecordInHistory gameRecordInHistory){
        StringBuilder sb = new StringBuilder();
        String dateAndTimeInMillisecs = String.valueOf(gameRecordInHistory.getDateAndTimeInMilliSecs());
        sb.append(dateAndTimeInMillisecs);
        sb.append(" ");
        String hostFullName = reformatStringWithSpace(gameRecordInHistory.getHostFullName());
        sb.append(hostFullName);
        sb.append(" ");
        sb.append(gameRecordInHistory.getHostWinningsNum());
        sb.append(" ");
        String guestFullName=  reformatStringWithSpace(gameRecordInHistory.getGuestFullName());
        sb.append(guestFullName);
        sb.append(" ");
        sb.append(gameRecordInHistory.getGuestWinningsNum());
        return sb.toString();
    }

    private String reformatStringWithSpace(String str){
        int spaceIndex = str.indexOf(' ');
        if(spaceIndex == -1)
            return str;
        char[] asCharArr = str.toCharArray();
        asCharArr[spaceIndex] = '_';
        return new String(asCharArr);
    }

    private String eraseSurroundingSigns(String str) {
        int startIdx = 0, endIdx = str.length() - 1;
        while (!Character.isLetter(str.charAt(startIdx)))
            startIdx++;
        while (!Character.isLetter(str.charAt(endIdx)))
            endIdx--;
        return str.substring(startIdx, endIdx + 1);
    }

    public String getGameRecordFromHistory(String gameId) {
        String res = sharedPreferences.getString(gameId, null);
        return res;

    }

    public Map<String, String> getAllGameRecordsFromHistory(){
        return (Map<String, String>)sharedPreferences.getAll();
    }

}

package local_long_term_data;

import java.util.HashMap;
import java.util.Map;


public interface IGamesHistoryManager {
    void UpdateGameInHistory(GameRecordInHistory game);
    void removeGameFromHistory(GameRecordInHistory gameRecordInHistory);
    String getGameRecordFromHistory(String gameId);
    Map<String, String> getAllGameRecordsFromHistory();
    void clearHistory();
}

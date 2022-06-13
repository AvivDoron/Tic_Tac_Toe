package tic_tac_toe_game;


public class GameFactory {

    public static IGameManager getGameManagerInstance(Game game, IGameManager.IGameManagerListener iGameManagerListener){
        if(game == null)
            throw new NullPointerException("Class GameFactory {In method: getGameManagerInstance}: game object is null");
        if(game.getHost() == null)
            throw new NullPointerException("Class GameFactory {In method: getGameManagerInstance}: Host object is null");
        if(game.getHost() == null)
            throw new NullPointerException("Class GameFactory {In method: getGameManagerInstance}: Guest object is null");

        switch(game.getGameType()){
            case OFFLINE:
                return new OfflineGame(game, iGameManagerListener);
            case ONLINE:
                return new OnlineGame(game, iGameManagerListener);
        }
        throw new IllegalArgumentException("Class GameFactory {In method: getGameManagerInstance}: Wrong type of game provided");
    }
}

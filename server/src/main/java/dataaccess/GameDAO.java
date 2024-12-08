package dataaccess;
import model.*;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames();
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game);
    void clear();
    boolean isEmpty();
    int getNumGames();
}

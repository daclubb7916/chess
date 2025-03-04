package dataaccess;
import model.*;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
    int getNumGames();
}

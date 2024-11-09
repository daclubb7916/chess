package dataaccess;
import model.*;

import java.util.Collection;

public interface GameDAO {
    public void createGame(GameData game) throws DataAccessException;
    public Collection<GameData> listGames();
    public GameData getGame(int gameID) throws DataAccessException;
    public void updateGame(GameData game);
    public void deleteGame(GameData game);
    public void clear();
    public int getNumGames();
}

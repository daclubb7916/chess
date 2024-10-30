package dataaccess;
import model.*;

import java.util.Collection;

public interface GameDAO {
    public void createGame(GameData game);
    public Collection<GameData> listGames();
    public GameData getGame(String gameID);
    public void updateGame(GameData game);
    public void deleteGame(GameData game);
    public void clear();
}

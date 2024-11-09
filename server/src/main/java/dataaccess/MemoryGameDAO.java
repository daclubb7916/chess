package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int numGames;

    public MemoryGameDAO() {
        this.numGames = 1;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        for (GameData gameData : games.values()) {
            if (Objects.equals(gameData.gameName(), game.gameName())) {
                throw new DataAccessException("Name already taken");
            }
        }

        games.put(game.gameID(), game);
        numGames += 1;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        throw new DataAccessException("Game not found");
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGame(GameData game) {

    }

    @Override
    public void clear() {
        numGames = 0;
    }

    @Override
    public int getNumGames() {
        return numGames;
    }
}

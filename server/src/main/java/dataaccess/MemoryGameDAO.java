package dataaccess;

import model.GameData;
import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int numGames;

    public MemoryGameDAO() {
        this.numGames = 1;
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        for (GameData gameData : games.values()) {
            if (Objects.equals(gameData.gameName(), game.gameName())) {
                throw new DataAccessException("Name already taken");
            }
        }
        games.put(game.gameID(), game);
        numGames += 1;
        return game;
    }

    @Override
    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        throw new DataAccessException("Game not found");
    }

    @Override
    public void updateGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() {
        numGames = 1;
        games.clear();
    }

    @Override
    public boolean isEmpty() {
        return games.isEmpty();
    }

    @Override
    public int getNumGames() {
        return numGames;
    }
}

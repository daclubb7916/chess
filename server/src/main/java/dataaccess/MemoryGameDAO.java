package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGame(GameData game) {

    }

    @Override
    public void clear() {

    }
}

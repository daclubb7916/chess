package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        String createTableStatement = """
            CREATE TABLE IF NOT EXISTS games (
                id INT NOT NULL AUTO_INCREMENT,
                gameData TEXT NOT NULL,
                PRIMARY KEY (id)
            )
            """;
        DatabaseManager.configureDatabase(createTableStatement);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getNumGames() {
        return 0;
    }
}

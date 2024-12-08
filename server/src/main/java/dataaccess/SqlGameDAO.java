package dataaccess;

import model.GameData;
import com.google.gson.Gson;

import java.sql.*;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        String createTableStatement = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255) DEFAULT NULL,
                blackUsername VARCHAR(255) DEFAULT NULL,
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )
            """;
        DatabaseManager.configureDatabase(createTableStatement);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String checkStatement = "SELECT * FROM games WHERE gameName=?";
            try (var prep = conn.prepareStatement(checkStatement)) {
                prep.setString(1, game.gameName());
                try (var rs = prep.executeQuery()) {
                    throw new DataAccessException("Name already taken");

                } catch (SQLException ex) {
                    String statement = "INSERT INTO games " +
                            "(whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
                    String json = new Gson().toJson(game.game());
                    int gameID = executeUpdate(statement, game.whiteUsername(),
                            game.blackUsername(), game.gameName(), json);
                    return new GameData(gameID, game.whiteUsername(),
                            game.blackUsername(), game.gameName(), game.game());
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

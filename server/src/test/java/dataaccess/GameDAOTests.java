package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameDAOTests {

    private static GameDAO gameDAO;

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        gameDAO = new SqlGameDAO();
    }

    @AfterEach
    public void emptyTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE games";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Test
    public void testCreateGame() {

    }

    @Test
    public void testListGames() {

    }

    @Test
    public void testGetGame() {

    }

    @Test
    public void testUpdateGame() {

    }

    @Test
    public void testIsEmptyAndClear() {

    }

    private Collection<Integer> addSomeGames() throws DataAccessException {
        Collection<Integer> gameIDs = new ArrayList<>();
        String statement = "INSERT INTO games " +
                "(whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String[] whiteUsernames = {"smirnoff", "joke", null};
        String[] blackUsernames = {"ghetto", null, null};
        String[] gameNames = {"winnah", "thisIsChess", "heyo"};
        ChessGame[] games = {new ChessGame(), new ChessGame(), new ChessGame()};

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < 3; i++) {
                    ps.setString(1, whiteUsernames[i]);
                    ps.setString(2, blackUsernames[i]);
                    ps.setString(3, gameNames[i]);
                    ps.setString(4, new Gson().toJson(games[i]));
                }

                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                while (rs.next()) {
                    gameIDs.add(rs.getInt(1));
                }
                return gameIDs;

            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}

package dataaccess;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class GameDAOTests {

    private static GameDAO gameDAO;

    private void addSomeGames() throws DataAccessException {
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
                    ps.executeUpdate();
                    var rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        gameIDs.add(rs.getInt(1));
                    }
                }

            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}

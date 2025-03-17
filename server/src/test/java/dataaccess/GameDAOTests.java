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

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        gameDAO = new SqlGameDAO();
    }

    @AfterEach
    public void emptyTable() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void testCreateGameSuccessfully() throws DataAccessException {
        GameData gameData = gameDAO.createGame(
                new GameData(3, "becky",
                        null, "tress", new ChessGame()));
        GameData sameGame = gameDAO.getGame(gameData.gameID());
        Assertions.assertNotEquals(0, gameData.gameID());
        Assertions.assertEquals(sameGame.gameID(), gameData.gameID());
        Assertions.assertEquals(sameGame.whiteUsername(), gameData.whiteUsername());
        Assertions.assertEquals(sameGame.blackUsername(), gameData.blackUsername());
        Assertions.assertEquals(sameGame.gameName(), gameData.gameName());
        Assertions.assertEquals(new Gson().toJson(sameGame.game()), new Gson().toJson(gameData.game()));
    }

    @Test
    public void testCreateGameWithExistingName() throws DataAccessException {
        GameData gameData = gameDAO.createGame(new GameData(1,
                null, null,
                "thisIsChess", new ChessGame()));
        GameData newData = new GameData(5,
                "chester", "craig",
                "thisIsChess", new ChessGame());
        DataAccessException e = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDAO.createGame(newData));
        Assertions.assertEquals("Name already taken", e.getMessage());
    }

    @Test
    public void testListGamesSuccessfully() throws DataAccessException {
        addSomeGames();
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(3, games.size());
    }

    @Test
    public void testGetGameSuccessfully() throws DataAccessException {
        addSomeGames();
        GameData game = gameDAO.getGame(1);
        Assertions.assertEquals("winnah", game.gameName());
        game = gameDAO.getGame(2);
        Assertions.assertEquals("joke", game.whiteUsername());
        game = gameDAO.getGame(3);
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    public void testGetGameUnsuccessfully() throws DataAccessException {
        addSomeGames();
        DataAccessException ex = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDAO.getGame(4));
        Assertions.assertEquals("Game not found", ex.getMessage());
    }

    @Test
    public void testUpdateGameSuccessfully() throws DataAccessException {
        addSomeGames();
        GameData gameData = gameDAO.getGame(2);
        ChessGame chessGame = gameData.game();
        ChessPosition startPosition = new ChessPosition(2, 1);
        ChessPosition endPosition = new ChessPosition(4, 1);
        ChessMove chessMove = new ChessMove(startPosition, endPosition, null);

        try {
            chessGame.makeMove(chessMove);
        } catch (InvalidMoveException ex) {
            Assertions.fail("Invalid move");
        }

        GameData newGame = new GameData(2, gameData.whiteUsername(),
                "Becky", gameData.gameName(), chessGame);
        gameDAO.updateGame(newGame);

        GameData updated = gameDAO.getGame(2);
        Assertions.assertEquals(new Gson().toJson(newGame.game()), new Gson().toJson(updated.game()));
        Assertions.assertEquals("Becky", updated.blackUsername());
    }

    @Test
    public void testUpdateGameUnsuccessfully() throws DataAccessException {
        addSomeGames();
        GameData game = gameDAO.getGame(2);
        GameData newGame = new GameData(2, game.whiteUsername(),
                game.blackUsername(), null, game.game());
        DataAccessException ex = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(newGame));
    }

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

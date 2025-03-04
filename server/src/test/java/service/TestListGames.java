package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import request.ListGamesRequest;
import result.ListGamesResult;
import java.util.*;

public class TestListGames {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private GameService service;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        service = new GameService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void testReturnsAllGamesSuccessfully() {
        HashMap<Integer, GameData> games = new HashMap<>();
        games.put(1, new GameData(1, null, null, "kiddo", new ChessGame()));
        games.put(2, new GameData(2, null, null, "heyo", new ChessGame()));
        games.put(3, new GameData(3, null, null, "boyo", new ChessGame()));

        for (GameData game : games.values()) {
            try {
                gameDAO.createGame(game);
            } catch (DataAccessException ex) {
                Assertions.fail(ex.getMessage());
            }
        }
        ArrayList<GameData> test = new ArrayList<>(games.values());

        String authToken = authDAO.createAuth("billy");
        ListGamesRequest request = new ListGamesRequest(authToken);
        try {
            ListGamesResult result = service.listGames(request);
            Assertions.assertEquals(result.games(), test);
        } catch (ResponseException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testBadAuthorizationThrowsException() {
        ListGamesRequest request = new ListGamesRequest("c-3po");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () -> service.listGames(request));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}

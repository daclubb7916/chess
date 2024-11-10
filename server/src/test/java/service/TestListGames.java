package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.ListGamesRequest;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.HashMap;

public class TestListGames {

    @Test
    public void testReturnsAllGamesSuccessfully() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService service = new GameService(userDAO, authDAO, gameDAO);
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
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService service = new GameService(userDAO, authDAO, gameDAO);
        ListGamesRequest request = new ListGamesRequest("c-3po");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () -> service.listGames(request));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}

package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.JoinGameRequest;
import model.GameData;

public class TestJoinGame {

    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;
    MemoryGameDAO gameDAO;
    GameService gameService;
    JoinGameRequest request1;
    JoinGameRequest request2;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameDAO.updateGame(new GameData(12, null, null,
                "Jethro", new ChessGame()));
        gameService = new GameService(userDAO, authDAO, gameDAO);
        String authToken = authDAO.createAuth("billy");
        request1 = new JoinGameRequest("WHITE", 12, authToken);
    }

    @Test
    public void testGameJoinedSuccessfully() {
        String authToken = authDAO.createAuth("jeff");
        request2 = new JoinGameRequest("BLACK", 12, authToken);
        try {
            gameService.joinGame(request1);
            gameService.joinGame(request2);
            GameData gameData = gameDAO.getGame(12);
            Assertions.assertEquals("billy", gameData.whiteUsername());
            Assertions.assertEquals("jeff", gameData.blackUsername());
        } catch (ResponseException | DataAccessException ex){
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void testSpotAlreadyTaken() {
        String authToken = authDAO.createAuth("jeff");
        request2 = new JoinGameRequest("WHITE", 12, authToken);
        try {
            gameService.joinGame(request1);
            ResponseException exception = Assertions.assertThrows(ResponseException.class,
                    () -> gameService.joinGame(request2));
            Assertions.assertEquals("Error: already taken", exception.getMessage());
        } catch (ResponseException ex){
            Assertions.fail(ex.getMessage());
        }
    }
}

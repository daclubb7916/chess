package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import result.CreateGameResult;

public class TestCreateGame {

    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;
    MemoryGameDAO gameDAO;
    GameService gameService;
    CreateGameRequest request;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        String authToken = authDAO.createAuth("billy");
        request = new CreateGameRequest("pedant", authToken);
    }

    @Test
    public void testGameSuccessfullyAdded() {
        try {
            CreateGameResult result = gameService.createGame(request);
            GameData gameData = gameDAO.getGame(result.gameID());
            Assertions.assertEquals(request.gameName(), gameData.gameName());
        } catch (ResponseException | DataAccessException ex) {
            Assertions.fail(ex.getMessage());
        }

    }

    @Test
    public void testNameAlreadyTaken() {
        try {
            GameData gameData = new GameData(
                    64, null, null, "pedant", new ChessGame());
            gameDAO.createGame(gameData);
            ResponseException exception = Assertions.assertThrows(ResponseException.class,
                    () -> gameService.createGame(request));
            Assertions.assertEquals("Error: bad request", exception.getMessage());
        } catch (DataAccessException ex) {
            Assertions.fail(ex.getMessage());
        }
    }
}

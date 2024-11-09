package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import result.CreateGameResult;

public class TestCreateGame {

    @Test
    public void testGameSuccessfullyAdded() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        String authToken = authDAO.createAuth("billy");
        CreateGameRequest request = new CreateGameRequest(authToken, "pedant");

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
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        String authToken = authDAO.createAuth("billy");
        CreateGameRequest request = new CreateGameRequest(authToken, "pedant");

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

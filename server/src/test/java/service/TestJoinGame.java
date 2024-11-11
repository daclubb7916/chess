package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import request.JoinGameRequest;
import model.GameData;
import org.junit.jupiter.api.Test;

public class TestJoinGame {

    @Test
    public void testGameJoinedSuccessfully() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        gameDAO.updateGame(new GameData(12, null, null,
                "Jethro", new ChessGame()));
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);

        String authToken = authDAO.createAuth("billy");
        JoinGameRequest request1 = new JoinGameRequest("WHITE", 12, authToken);
        authToken = authDAO.createAuth("jeff");
        JoinGameRequest request2 = new JoinGameRequest("BLACK", 12, authToken);

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
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        gameDAO.updateGame(new GameData(12, null, null,
                "Jethro", new ChessGame()));
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);

        String authToken = authDAO.createAuth("billy");
        JoinGameRequest request1 = new JoinGameRequest("WHITE", 12, authToken);
        authToken = authDAO.createAuth("jeff");
        JoinGameRequest request2 = new JoinGameRequest("WHITE", 12, authToken);

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

package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

public class TestClear {

    @Test
    public void testClearRemovesAllData() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService service = new GameService(userDAO, authDAO, gameDAO);
        UserData user = new UserData("billy", "billy", "billy@aol.com");
        userDAO.createUser(user);
        String authToken = authDAO.createAuth("billy");
        GameData game = new GameData(
                23, null, null, "pedant", new ChessGame());
        try {
            gameDAO.createGame(game);
        } catch (DataAccessException ex) {
            Assertions.fail(ex.getMessage());
        }
        Assertions.assertFalse(userDAO.isEmpty(), "User wasn't added");
        Assertions.assertFalse(authDAO.isEmpty(), "AuthData wasn't added");
        Assertions.assertFalse(gameDAO.isEmpty(), "Game wasn't added");
        try {
            service.clear();
        } catch (ResponseException ex) {
            Assertions.fail(ex.getMessage());
        }
        Assertions.assertTrue(userDAO.isEmpty(), "User data wasn't cleared");
        Assertions.assertTrue(authDAO.isEmpty(), "AuthToken data wasn't cleared");
        Assertions.assertTrue(gameDAO.isEmpty(), "Game data wasn't cleared");
    }
}

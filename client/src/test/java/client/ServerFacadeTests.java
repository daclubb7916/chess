package client;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import exception.ResponseException;
import request.*;
import result.*;

import java.util.Collection;
import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private SqlUserDAO userDAO;
    private SqlAuthDAO authDAO;
    private SqlGameDAO gameDAO;

    public ServerFacadeTests() {
        configureDAOs();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + server.port();
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException ex) {
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @Test
    public void clearTest() {
        addSomeGames();
        try {
            Assertions.assertDoesNotThrow(() -> facade.clear());
            Assertions.assertTrue(gameDAO.isEmpty());
            Assertions.assertTrue(userDAO.isEmpty());
            Assertions.assertTrue(authDAO.isEmpty());
        } catch (DataAccessException ex) {
            Assertions.fail("Threw exception: " + ex.getMessage());
        }
    }

    @Test
    public void createGamePositiveTest() {
        try {
            String authToken = getAuthToken();
            CreateGameRequest createGameRequest = new CreateGameRequest("jenga", authToken);
            CreateGameResult createResult = facade.createGame(createGameRequest);
            Collection<GameData> games = gameDAO.listGames();
            boolean gameAdded = false;
            for (GameData game : games) {
                if (game.gameName().equals("jenga")) {
                    gameAdded = true;
                    break;
                }
            }
            Assertions.assertTrue(gameAdded);

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error getting games");
        }
    }

    @Test
    public void createGameNegativeTest() {
        try {
            String authToken = getAuthToken();
            addSomeGames();
            CreateGameRequest createGameRequest = new CreateGameRequest("quackers", authToken);
            Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void joinGamePositiveTest() {
        try {
            String authToken = getAuthToken();
            addSomeGames();
            JoinGameRequest joinGameRequest =
                    new JoinGameRequest("WHITE", 1, authToken);
            Assertions.assertDoesNotThrow(() -> facade.joinGame(joinGameRequest));
            Collection<GameData> games = gameDAO.listGames();
            boolean containsUser = false;
            for (GameData game : games) {
                if (game.whiteUsername().equals("dak")) {
                    containsUser = true;
                    break;
                }
            }
            Assertions.assertTrue(containsUser);

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        } catch (DataAccessException exc) {
            System.out.println("Error getting games");
        }
    }

    @Test
    public void joinGameNegativeTest() {
        try {
            String authToken = getAuthToken();
            addSomeGames();
            JoinGameRequest joinGameRequest =
                    new JoinGameRequest("WHITE", 1, authToken);
            facade.joinGame(joinGameRequest);
            Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(joinGameRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void loginPositiveTest() {
        addUser();
        LoginRequest loginRequest = new LoginRequest("dak", "duk");
        Assertions.assertDoesNotThrow(() -> facade.login(loginRequest));
    }

    @Test
    public void loginNegativeTest() {
        addUser();
        LoginRequest badRequest = new LoginRequest("dak", "incorrect_password");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(badRequest));
    }

    @Test
    public void logoutPositiveTest() {
        try {
            String authToken = getAuthToken();
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            Assertions.assertDoesNotThrow(() -> facade.logout(logoutRequest));
        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void logoutNegativeTest() {
        try {
            String authToken = getAuthToken();
            authDAO.deleteAuth(authToken);
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            Assertions.assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        } catch (DataAccessException exc) {
            System.out.println("Error deleting authToken");
        }
    }

    @Test
    public void registerPositiveTest() {
        RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
        Assertions.assertDoesNotThrow(() -> facade.register(registerRequest));
    }

    @Test
    public void registerNegativeTest() {
        addUser();
        Assertions.assertThrows(ResponseException.class, () -> facade.register(
                new RegisterRequest("dak", "duk", "dok@byu.edu")));
    }

    @Test
    public void listGamesPositiveTest() {
        try {
            String authToken = getAuthToken();
            addSomeGames();
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            Collection<GameData> games = listGamesResult.games();
            boolean gameReturned = false;
            for (GameData game : games) {
                if ((game.gameName().equals("quackers")) || (game.gameName().equals("lemony")) ||
                        (game.gameName().equals("squirrel"))) {
                    gameReturned = true;
                    break;
                }
            }
            Assertions.assertTrue(gameReturned);
            Assertions.assertEquals(3, games.size());

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void listGamesNegativeTest() {
        ListGamesRequest badRequest = new ListGamesRequest(UUID.randomUUID().toString());
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames(badRequest));
    }

    private void addUser() {
        try {
            userDAO.createUser(new UserData("dak", "duk", "dok@byu.edu"));
        } catch (DataAccessException ex) {
            System.out.println("Error creating user");
        }
    }

    private String getAuthToken() throws ResponseException {
        try {
            userDAO.createUser(new UserData("dak", "duk", "dok@byu.edu"));
            return authDAO.createAuth("dak");
        } catch (DataAccessException ex) {
            throw new ResponseException(400, "Error getting authData");
        }
    }

    private void addSomeGames() {
        try {
            GameData game1 = gameDAO.createGame(new GameData(
                    1, null, null, "quackers", new ChessGame()));
            GameData game2 = gameDAO.createGame(new GameData(
                    2, null, null, "lemony", new ChessGame()));
            GameData game3 = gameDAO.createGame(new GameData(
                    3, null, null, "squirrel", new ChessGame()));
        } catch (DataAccessException ex) {
            System.out.println("Error adding games to database");
        }
    }

    private void configureDAOs() {
        try {
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
            gameDAO = new SqlGameDAO();
        } catch (DataAccessException ex) {
            System.out.println("Error creating data access objects");
        }
    }
}

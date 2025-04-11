package client;

import model.GameData;
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
            facade.clear();
        } catch (ResponseException ex) {
            Assertions.fail("Clear method threw exception: " + ex.getMessage());
        }
    }

    @Test
    public void createGameTests() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
            RegisterResult registerResult = facade.register(registerRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("jenga", registerResult.authToken());
            CreateGameResult createResult = facade.createGame(createGameRequest);
            ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            Collection<GameData> games = listGamesResult.games();
            for (GameData game : games) {
                if (game.gameName().equals("jenga")) {
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("Game names did not match");
                }
            }

            Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void joinGameTests() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
            RegisterResult registerResult = facade.register(registerRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("jenga", registerResult.authToken());
            CreateGameResult createResult = facade.createGame(createGameRequest);
            JoinGameRequest joinGameRequest =
                    new JoinGameRequest("WHITE", createResult.gameID(), registerResult.authToken());
            facade.joinGame(joinGameRequest);
            ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            Collection<GameData> games = listGamesResult.games();
            for (GameData game : games) {
                if (game.whiteUsername().equals("dak")) {
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("Usernames did not match");
                }
            }

            Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(joinGameRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void loginTests() {
        RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
        Assertions.assertDoesNotThrow(() -> facade.register(registerRequest));
        LoginRequest loginRequest = new LoginRequest("dak", "duk");
        Assertions.assertDoesNotThrow(() -> facade.login(loginRequest));

        LoginRequest badRequest = new LoginRequest("dak", "incorrect_password");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(badRequest));
    }

    @Test
    public void logoutTests() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
            RegisterResult registerResult = facade.register(registerRequest);
            LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
            facade.logout(logoutRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("jenga", registerResult.authToken());
            Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));

            Assertions.assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }

    @Test
    public void registerTests() {
        RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
        Assertions.assertDoesNotThrow(() -> facade.register(registerRequest));

        Assertions.assertThrows(ResponseException.class, () -> facade.register(registerRequest));
    }

    @Test
    public void listGamesTests() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("dak", "duk", "dok@byu.edu");
            RegisterResult registerResult = facade.register(registerRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("jenga", registerResult.authToken());
            CreateGameResult createResult = facade.createGame(createGameRequest);
            ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            Collection<GameData> games = listGamesResult.games();
            for (GameData game : games) {
                if (game.gameName().equals("jenga")) {
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("Game names did not match");
                }
            }

            ListGamesRequest badRequest = new ListGamesRequest(UUID.randomUUID().toString());
            Assertions.assertThrows(ResponseException.class, () -> facade.listGames(badRequest));

        } catch (ResponseException ex) {
            Assertions.fail("Threw exception :" + ex.getMessage());
        }
    }
}

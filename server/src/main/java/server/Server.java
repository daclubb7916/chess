package server;

import com.google.gson.Gson;
import exception.ResponseException;
import handler.*;
import spark.*;
import dataaccess.*;
import java.util.Map;

public class Server {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public Server() {
    }

    public int run(int desiredPort) {
        try {
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
            gameDAO = new SqlGameDAO();
        } catch (DataAccessException ex) {
            System.out.println("Unable to start SQL server");
        }
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));
        Spark.post("/user", new RegisterHandler(userDAO, authDAO));
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        Spark.delete("/session", new LogoutHandler(userDAO, authDAO));
        Spark.post("/game", new CreateGameHandler(userDAO, authDAO, gameDAO));
        Spark.get("/game", new ListGamesHandler(userDAO, authDAO, gameDAO));
        Spark.put("/game", new JoinGameHandler(userDAO, authDAO, gameDAO));
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", ex.getMessage()));
        res.status(ex.getStatusCode());
        res.body(body);
    }
}

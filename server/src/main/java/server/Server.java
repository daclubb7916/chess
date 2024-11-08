package server;

import com.google.gson.Gson;
import exception.ResponseException;
import handler.*;
import spark.*;
import dataaccess.*;

import java.util.Map;

public class Server {
    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        this.registerHandler = new RegisterHandler(userDAO, authDAO);
        this.loginHandler = new LoginHandler(userDAO, authDAO);
        this.logoutHandler = new LogoutHandler(userDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
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
        res.status(ex.StatusCode());
        res.body(body);
    }
}

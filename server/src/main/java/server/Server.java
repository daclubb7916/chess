package server;

import handler.Handler;
import spark.*;
import dataaccess.*;

public class Server {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private Handler handler;

    public Server() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.handler = new Handler(userDAO, authDAO, gameDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.post("/session", this::login);
        // initialize the DAO's

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object login(Request req, Response res) {
        handler.loginHandler(req.body());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

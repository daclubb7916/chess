package server;

import handler.Handler;
import spark.*;

public class Server {
    private Handler handler;

    public Server() {
        this.handler = new Handler();
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

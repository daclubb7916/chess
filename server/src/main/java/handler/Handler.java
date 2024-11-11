package handler;

import exception.ResponseException;
import spark.Request;
import spark.Response;
import spark.Route;

public class Handler implements Route {

    public Handler() {
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        return null;
    }
}

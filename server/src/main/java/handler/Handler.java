package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import request.*;
import result.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class Handler implements Route {

    public Handler() {

    }

    @Override
    public Object handle(Request request, Response response) throws ResponseException {
        return null;
    }
}

package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;
import spark.*;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        LogoutRequest request = new LogoutRequest(req.headers("authorization"));
        LogoutResult result = userService.logout(request);
        return new Gson().toJson(result);
    }
}

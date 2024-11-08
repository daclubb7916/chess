package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    private final UserService userService;

    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        LogoutRequest request = new Gson().fromJson(req.headers("authToken"), LogoutRequest.class);
        userService.logout(request);
        return new LogoutResult();
    }
}

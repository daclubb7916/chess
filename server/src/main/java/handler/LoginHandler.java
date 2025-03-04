package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.*;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        return new Gson().toJson(result);
    }
}

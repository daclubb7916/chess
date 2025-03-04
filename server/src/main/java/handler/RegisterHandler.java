package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import dataaccess.*;
import spark.*;

public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (request.username() == null | request.email() == null | request.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        RegisterResult result = userService.register(request);
        return new Gson().toJson(result);
    }
}

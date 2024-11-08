package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        return new Gson().toJson(result);
    }
}

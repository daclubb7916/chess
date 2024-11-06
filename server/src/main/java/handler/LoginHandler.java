package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    public LoginHandler() {

    }

    @Override
    public Object handle(Request request, Response response) throws ResponseException {
        /* LoginRequest request = new Gson().fromJson(body, LoginRequest.class);
        try {
            LoginResult result = userService.login(request);
            return new Gson().toJson(result);
        } catch (Exception ex) {
            throw new ResponseException(401, ex.getMessage());
        }

         */
    }
}

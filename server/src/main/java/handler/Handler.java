package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.*;
import result.*;
import service.UserService;

public class Handler {
    private Gson serializer;
    private UserService userService;

    public Handler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.serializer = new Gson();
        this.userService = new UserService(userDAO, authDAO);
    }

    public void clearHandler() {

    }
    public void registerHandler() {

    }
    public void loginHandler(String body) {
        LoginRequest request = serializer.fromJson(body, LoginRequest.class);
        Result result = userService.login(request);
    }
    public void logoutHandler() {

    }
    public void listGamesHandler() {

    }
    public void createGameHandler() {

    }
    public void joinGameHandler() {

    }
}

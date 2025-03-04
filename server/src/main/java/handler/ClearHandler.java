package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import result.ClearResult;
import service.GameService;
import spark.*;

public class ClearHandler implements Route {
    GameService gameService;

    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws ResponseException {
        gameService.clear();
        return new Gson().toJson(new ClearResult());
    }
}

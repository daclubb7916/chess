package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import spark.*;

public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        request = new CreateGameRequest(request.gameName(), req.headers("authorization"));
        CreateGameResult result = gameService.createGame(request);
        return new Gson().toJson(result);
    }
}

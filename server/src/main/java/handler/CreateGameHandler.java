package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handler {
    private final GameService gameService;

    public CreateGameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        request = new CreateGameRequest(req.headers("authorization"), request.gameName());
        CreateGameResult result = gameService.createGame(request);
        return new Gson().toJson(result);
    }
}

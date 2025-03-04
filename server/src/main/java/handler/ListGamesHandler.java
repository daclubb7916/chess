package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import request.ListGamesRequest;
import result.ListGamesResult;
import service.GameService;
import spark.*;

public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        ListGamesRequest request = new ListGamesRequest(req.headers("authorization"));
        ListGamesResult result = gameService.listGames(request);
        return new Gson().toJson(result);
    }
}

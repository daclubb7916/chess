package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import result.JoinGameResult;
import request.JoinGameRequest;
import service.GameService;
import spark.*;

public class JoinGameHandler implements Route {
    private final GameService service;

    public JoinGameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.service = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        request = new JoinGameRequest(request.playerColor(), request.gameID(), req.headers("authorization"));
        if (request.playerColor() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        JoinGameResult result = service.joinGame(request);
        return new Gson().toJson(result);
    }
}

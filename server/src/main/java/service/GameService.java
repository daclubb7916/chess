package service;

import chess.ChessGame;
import exception.ResponseException;
import request.*;
import result.*;
import dataaccess.*;
import model.*;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void listGames(ListGamesRequest request) {

    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        AuthData authData = authenticateUser(request.authToken());
        GameData gameData = new GameData(gameDAO.getNumGames(), null,
                null, request.gameName(), new ChessGame());

        try {
            gameDAO.createGame(gameData);
            return new CreateGameResult(gameData.gameID());
        } catch (DataAccessException ex) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public void joinGame(JoinGameRequest request) {

    }
    public void clear() {

    }

    public AuthData authenticateUser(String authToken) throws ResponseException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException ex) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}

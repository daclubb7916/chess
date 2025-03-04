package service;

import chess.ChessGame;
import exception.ResponseException;
import request.*;
import result.*;
import dataaccess.*;
import model.*;
import java.util.*;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        AuthData authData = authenticateUser(request.authToken());
        try {
            Collection<GameData> games = gameDAO.listGames();
            return new ListGamesResult(games);
        } catch (DataAccessException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        AuthData authData = authenticateUser(request.authToken());
        GameData gameData = new GameData(gameDAO.getNumGames(), null,
                null, request.gameName(), new ChessGame());
        try {
            GameData newGame = gameDAO.createGame(gameData);
            return new CreateGameResult(newGame.gameID());
        } catch (DataAccessException ex) {
            if (Objects.equals(ex.getMessage(), "Name already taken")) {
                throw new ResponseException(400, "Error: bad request");
            }
            throw new ResponseException(500, "Error: " + ex.getMessage());
        }
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException {
        AuthData authData = authenticateUser(request.authToken());
        try {
            GameData gameData = gameDAO.getGame(request.gameID());
            gameDAO.updateGame(addUserToGameData(request, gameData, authData));
        } catch (DataAccessException ex) {
            if (Objects.equals(ex.getMessage(), "Game not found")) {
                throw new ResponseException(400, "Error: bad request");
            }
            throw new ResponseException(500, "Error: " + ex.getMessage());
        }
        return new JoinGameResult();
    }

    public void clear() throws ResponseException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException ex) {
            throw new ResponseException(500, "Error: " + ex.getMessage());
        }
    }

    private AuthData authenticateUser(String authToken) throws ResponseException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException ex) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    private GameData addUserToGameData(JoinGameRequest request, GameData gameData, AuthData authData) throws ResponseException {
        if (Objects.equals(request.playerColor(), "WHITE")) {
            if (gameData.whiteUsername() == null) {
                return new GameData(gameData.gameID(), authData.username(),
                        gameData.blackUsername(), gameData.gameName(), gameData.game());
            }

        } else {
            if (gameData.blackUsername() == null) {
                return new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                        gameData.gameName(), gameData.game());
            }
        }
        throw new ResponseException(403, "Error: already taken");
    }
}

package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }

    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        String userName = "";
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            userName = authData.username();
            GameData game = gameDAO.getGame(command.getGameID());
            connections.add(userName, game.gameID(), session);

            String message;
            if (userName.equals(game.whiteUsername())) {
                message = String.format("%s joined Chess Game as White Player", userName);
            } else if (userName.equals(game.blackUsername())) {
                message = String.format("%s joined Chess Game as Black Player", userName);
            } else {
                message = String.format("%s is observing Chess Game", userName);
            }

            connections.sendGame(userName, game);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.broadcastMessage(userName, game.gameID(), notificationMessage);

        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendError(userName, errorMessage);
        }
    }

    private void makeMove(MakeMoveCommand command) throws IOException {
        String userName = "";
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            userName = authData.username();
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor teamColor;
            String opponentUserName;

            if (chessGame.isOver()) {
                ErrorMessage errorMessage = new ErrorMessage("This game is over");
                connections.sendError(userName, errorMessage);
                return;
            }

            if (userName.equals(gameData.whiteUsername())) {
                teamColor = ChessGame.TeamColor.WHITE;
                opponentUserName = gameData.blackUsername();
            } else {
                teamColor = ChessGame.TeamColor.BLACK;
                opponentUserName = gameData.whiteUsername();
            }

            if (!chessGame.getTeamTurn().equals(teamColor)) {
                ErrorMessage errorMessage = new ErrorMessage("It is not your turn yet");
                connections.sendError(userName, errorMessage);
                return;
            }

            ChessBoard chessBoard = chessGame.getBoard();
            ChessMove chessMove = command.getMove();
            ChessPiece chessPiece = chessBoard.getPiece(chessMove.getStartPosition());
            try {
                chessGame.makeMove(chessMove);
            } catch (InvalidMoveException ex) {
                ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
                connections.sendError(userName, errorMessage);
                return;
            }

            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);
            connections.broadcastGame(gameData);
            String pieceType = chessPiece.stringPieceType();
            String message = authData.username() + " moved their " + pieceType + " from " +
                    chessMove.getStartPosition().toString() + " to " + chessMove.getEndPosition().toString();
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.broadcastMessage(userName, gameData.gameID(), notificationMessage);

            if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                message = opponentUserName + " is in check";
                notificationMessage = new NotificationMessage(message);
                connections.sendMessage(userName, notificationMessage);
                connections.broadcastMessage(userName, gameData.gameID(), notificationMessage);
            } else if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = opponentUserName + " is in checkmate. Game over";
                notificationMessage = new NotificationMessage(message);
                connections.sendMessage(userName, notificationMessage);
                connections.broadcastMessage(userName, gameData.gameID(), notificationMessage);
            } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = "This move has resulted in a stalemate. Game over";
                notificationMessage = new NotificationMessage(message);
                connections.sendMessage(userName, notificationMessage);
                connections.broadcastMessage(userName, gameData.gameID(), notificationMessage);
            }

            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);

        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendError(userName, errorMessage);
        }

    }

    private void leave(UserGameCommand command) throws IOException {
        String userName = "";
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            userName = authData.username();
            GameData game = gameDAO.getGame(command.getGameID());

            String message;
            if (authData.username().equals(game.whiteUsername())) {
                message = String.format("%s left the Chess Game", userName);
                game = new GameData(game.gameID(), null,
                        game.blackUsername(), game.gameName(), game.game());
            } else if (authData.username().equals(game.blackUsername())) {
                message = String.format("%s left the Chess Game", userName);
                game = new GameData(game.gameID(), game.whiteUsername(),
                        null, game.gameName(), game.game());
            } else {
                message = String.format("%s stopped observing the Chess Game", userName);
            }

            gameDAO.updateGame(game);
            connections.remove(userName);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.broadcastMessage(userName, game.gameID(), notificationMessage);

        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendError(userName, errorMessage);
        }
    }

    private void resign(UserGameCommand command) throws IOException {
        String userName = "";
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            userName = authData.username();
            GameData game = gameDAO.getGame(command.getGameID());
            String message = String.format("%s has resigned. Chess Game is Over", authData.username());

            game.game().endGame();
            game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName(), game.game());
            gameDAO.updateGame(game);

            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.sendMessage(userName, notificationMessage);
            connections.remove(userName);
            connections.broadcastMessage(userName, game.gameID(), notificationMessage);

        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendError(userName, errorMessage);
        }
    }

}

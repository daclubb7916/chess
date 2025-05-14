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
import websocket.messages.ServerMessage;

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
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
        }

    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());
            connections.add(authData.username(), game.gameID(), session);

            String message;
            if (authData.username().equals(game.whiteUsername())) {
                message = String.format("%s joined Chess Game as White Player", authData.username());
            } else if (authData.username().equals(game.blackUsername())) {
                message = String.format("%s joined Chess Game as Black Player", authData.username());
            } else {
                message = String.format("%s is observing Chess Game", authData.username());
            }

            connections.sendGame(authData.username(), game);
            ServerMessage notifyMessage = new NotificationMessage(message);
            connections.broadcastMessage(authData.username(), game.gameID(), notifyMessage);

        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendMessage(session, errorMessage);
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor teamColor;

            if (chessGame.isOver()) {
                ServerMessage errorMessage = new ErrorMessage("This game is over");
                connections.sendMessage(session, errorMessage);
                return;
            }

            if (authData.username().equals(gameData.whiteUsername())) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else {
                teamColor = ChessGame.TeamColor.BLACK;
            }

            if (!chessGame.getTeamTurn().equals(teamColor)) {
                ServerMessage errorMessage = new ErrorMessage("It is not your turn yet");
                connections.sendMessage(session, errorMessage);
                return;
            }

            ChessBoard chessBoard = chessGame.getBoard();
            ChessMove chessMove = command.getMove();
            try {
                chessGame.makeMove(chessMove);
            } catch (InvalidMoveException ex) {
                ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
                connections.sendMessage(session, errorMessage);
                return;
            }
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);

            connections.broadcastGame(gameData);
            ChessPiece chessPiece = chessBoard.getPiece(chessMove.getStartPosition());
            String pieceType = chessPiece.stringPieceType();
            String message = authData.username() + " moved their " + pieceType + " from " +
                    chessMove.getStartPosition().toString() + " to " + chessMove.getEndPosition().toString();
            ServerMessage notifyMessage = new NotificationMessage(message);
            connections.broadcastMessage(authData.username(), gameData.gameID(), notifyMessage);

            if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                message = chessGame.stringTeamColor(chessGame.getTeamTurn()) + " is now in check";
                notifyMessage = new NotificationMessage(message);
                connections.sendMessage(session, notifyMessage);
                connections.broadcastMessage(authData.username(), gameData.gameID(), notifyMessage);
            } else if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = chessGame.stringTeamColor(chessGame.getTeamTurn()) + " is in checkmate";
                notifyMessage = new NotificationMessage(message);
                connections.sendMessage(session, notifyMessage);
                connections.broadcastMessage(authData.username(), gameData.gameID(), notifyMessage);
            } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = "This move has resulted in a stalemate";
                notifyMessage = new NotificationMessage(message);
                connections.sendMessage(session, notifyMessage);
                connections.broadcastMessage(authData.username(), gameData.gameID(), notifyMessage);
            }

            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);

        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendMessage(session, errorMessage);
        }

    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());

            String message;
            if (authData.username().equals(game.whiteUsername())) {
                message = String.format("%s playing as White Player left the Chess Game", authData.username());
                game = new GameData(game.gameID(), null,
                        game.blackUsername(), game.gameName(), game.game());
            } else if (authData.username().equals(game.blackUsername())) {
                message = String.format("%s playing as Black Player left the Chess Game", authData.username());
                game = new GameData(game.gameID(), game.whiteUsername(),
                        null, game.gameName(), game.game());
            } else {
                message = String.format("%s stopped observing the Chess Game", authData.username());
            }

            gameDAO.updateGame(game);
            ServerMessage notifyMessage = new NotificationMessage(message);
            connections.broadcastMessage(authData.username(), game.gameID(), notifyMessage);
            connections.remove(authData.username());

        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendMessage(session, errorMessage);
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());
            String message = getString(authData, game);

            game.game().endGame();
            game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName(), game.game());
            gameDAO.updateGame(game);
            ServerMessage notifyMessage = new NotificationMessage(message);
            connections.sendMessage(session, notifyMessage);
            connections.broadcastMessage(authData.username(), game.gameID(), notifyMessage);
            connections.remove(authData.username());

        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendMessage(session, errorMessage);
        }
    }

    private static String getString(AuthData authData, GameData game) {
        String message;
        if (authData.username().equals(game.whiteUsername())) {
            message = String.format("%s playing as White Player has resigned. Chess Game is Over", authData.username());
        } else if (authData.username().equals(game.blackUsername())) {
            message = String.format("%s playing as Black Player has resigned. Chess Game is Over", authData.username());
        } else {
            message = String.format("%s has attempted to resign a game that they are not playing lol",
                    authData.username());
        }
        return message;
    }
}

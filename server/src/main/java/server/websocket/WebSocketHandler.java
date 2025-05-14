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
            connections.broadcastMessage(userName, game.gameID(), message);

        } catch (DataAccessException ex) {
            System.out.println("DataAccessException in connect");
            connections.sendError(userName, ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Null Pointer Exception in connect");
        } catch (Exception ex) {
            System.out.println("Exception in connect: " + ex.getMessage());
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
                connections.sendError(userName, "This game is over");
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
                connections.sendError(userName, "It is not your turn yet");
                return;
            }

            ChessBoard chessBoard = chessGame.getBoard();
            ChessMove chessMove = command.getMove();
            ChessPiece chessPiece = chessBoard.getPiece(chessMove.getStartPosition());
            try {
                chessGame.makeMove(chessMove);
            } catch (InvalidMoveException ex) {
                connections.sendError(userName, ex.getMessage());
                return;
            }

            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);
            connections.broadcastGame(gameData);
            String pieceType = chessPiece.stringPieceType();
            String message = authData.username() + " moved their " + pieceType + " from " +
                    chessMove.getStartPosition().toString() + " to " + chessMove.getEndPosition().toString();
            connections.broadcastMessage(userName, gameData.gameID(), message);

            if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                message = opponentUserName + " is in check";
                connections.sendMessage(userName, message);
                connections.broadcastMessage(userName, gameData.gameID(), message);
            } else if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = opponentUserName + " is in checkmate. Game over";
                connections.sendMessage(userName, message);
                connections.broadcastMessage(userName, gameData.gameID(), message);
            } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                chessGame.endGame();
                message = "This move has resulted in a stalemate. Game over";
                connections.sendMessage(userName, message);
                connections.broadcastMessage(userName, gameData.gameID(), message);
            }

            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), chessGame);
            gameDAO.updateGame(gameData);

        } catch (DataAccessException ex) {
            System.out.println("DataAccessException in makeMove");
            connections.sendError(userName, ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Null Pointer Exception in makeMove");
        } catch (Exception ex) {
            System.out.println("Exception in makeMove: " + ex.getMessage());
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
                message = String.format("%s playing as White Player left the Chess Game", userName);
                game = new GameData(game.gameID(), null,
                        game.blackUsername(), game.gameName(), game.game());
            } else if (authData.username().equals(game.blackUsername())) {
                message = String.format("%s playing as Black Player left the Chess Game", userName);
                game = new GameData(game.gameID(), game.whiteUsername(),
                        null, game.gameName(), game.game());
            } else {
                message = String.format("%s stopped observing the Chess Game", userName);
            }

            gameDAO.updateGame(game);
            // send message?
            connections.broadcastMessage(userName, game.gameID(), message);
            connections.remove(userName);

        } catch (DataAccessException ex) {
            System.out.println("DataAccessException in leave");
            connections.sendError(userName, ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Null Pointer Exception in leave");
        } catch (Exception ex) {
            System.out.println("Exception in leave: " + ex.getMessage());
        }
    }

    // resign is being invoked at an improper time?
    // At the very least it is throwing an illegal state exception
    private void resign(UserGameCommand command) throws IOException {
        String userName = "";
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            userName = authData.username();
            GameData game = gameDAO.getGame(command.getGameID());
            String message = getString(authData, game);

            game.game().endGame();
            game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName(), game.game());
            gameDAO.updateGame(game);

            connections.sendMessage(userName, message);
            connections.broadcastMessage(userName, game.gameID(), message);
            connections.remove(userName);

        } catch (DataAccessException ex) {
            System.out.println("DataAccessException in resign");
            connections.sendError(userName, ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Null Pointer Exception in resign");
        } catch (Exception ex) {
            System.out.println("Exception in resign: " + ex.getMessage());
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

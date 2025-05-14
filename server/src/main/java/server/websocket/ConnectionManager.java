package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Integer gameID, Session session) {
        var connection = new Connection(userName, gameID, session);
        connections.put(userName, connection); // are these thread safe operations?
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void broadcastMessage(String excludeName, Integer allGameID, String message)
            throws IOException {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        var removeList = new ArrayList<Connection>();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if ((!c.userName.equals(excludeName)) && (c.gameID.equals(allGameID))) {
                    c.send(new Gson().toJson(notificationMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.userName);
        }
    }

    public void broadcastGame(GameData gameData) throws IOException {
        var removeList = new ArrayList<Connection>();
        String chessBoard;
        LoadGameMessage loadMessage;
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(gameData.gameID())) {
                    chessBoard = stringChessBoard(c.userName, gameData);
                    loadMessage = new LoadGameMessage(chessBoard);
                    c.send(new Gson().toJson(loadMessage));
                }

            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.userName);
        }

    }

    public void sendError(String userName, String message) throws IOException {
        if (message.isEmpty()) {
            message = "Invalid AuthToken";
        }
        Connection toSend = connections.get(userName);
        ErrorMessage errorMessage = new ErrorMessage(message);
        toSend.send(new Gson().toJson(errorMessage));
    }

    public void sendMessage(String userName, String message) throws IOException {
        Connection toSend = connections.get(userName);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        toSend.send(new Gson().toJson(notificationMessage));
    }

    public void sendGame(String userName, GameData gameData) throws IOException {
        Connection toSend = connections.get(userName);
        String chessBoard = stringChessBoard(userName, gameData);
        LoadGameMessage loadMessage = new LoadGameMessage(chessBoard);
        toSend.send(new Gson().toJson(loadMessage));
    }

    public String stringChessBoard(String userName, GameData gameData) {
        ChessBoard board = gameData.game().getBoard();
        if (userName.equals(gameData.blackUsername())) {
            return board.stringBoard(ChessGame.TeamColor.BLACK);
        }
        return board.stringBoard(ChessGame.TeamColor.WHITE);
    }
}


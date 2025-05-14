package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Integer gameID, Session session) {
        var connection = new Connection(userName, gameID, session);
        connections.put(userName, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void broadcastMessage(String excludeName, Integer allGameID, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        String message = new Gson().toJson(serverMessage);
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if ((!c.userName.equals(excludeName)) && (c.gameID.equals(allGameID))) {
                    c.send(message);
                }
            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.userName);
        }
    }

    // Change ServerMessage to LoadGameMessage
    public void broadcastGame(GameData gameData, ServerMessage serverMessage) throws IOException {
        /*
        var removeList = new ArrayList<Connection>();
        String message = new Gson().toJson(serverMessage);
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if ((!c.userName.equals(excludeName)) && (c.gameID.equals(allGameID))) {
                    c.send(message);
                }
            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.userName);
        }

         */
    }

    public void sendMessage(Session session, ServerMessage serverMessage) throws IOException {
        String message = new Gson().toJson(serverMessage);
        for (Connection c : connections.values()) {
            if (c.session.equals(session)) {
                c.send(message);
                break;
            }
        }
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


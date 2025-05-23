package server.websocket;

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
        connections.put(userName, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void broadcastMessage(String excludeName, Integer allGameID, NotificationMessage notificationMessage)
            throws IOException {
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
        LoadGameMessage loadMessage;
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(gameData.gameID())) {
                    loadMessage = new LoadGameMessage(gameData);
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

    public void sendError(Session session, ErrorMessage errorMessage, String userName) throws IOException {
        if ((userName.isEmpty()) || connections.isEmpty()) {
            errorMessage = new ErrorMessage("Invalid Authorization");
        } else if (connections.containsKey(userName)) {
            Connection c = connections.get(userName);
            if (c.session.isOpen()) {
                c.send(new Gson().toJson(errorMessage));
                return;
            } else {
                errorMessage = new ErrorMessage("Session is unavailable");
            }

        }
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }

    public void sendMessage(String userName, NotificationMessage notificationMessage) throws IOException {
        Connection toSend = connections.get(userName);
        toSend.send(new Gson().toJson(notificationMessage));
    }

    public void sendGame(String userName, GameData gameData) throws IOException {
        Connection toSend = connections.get(userName);
        LoadGameMessage loadMessage = new LoadGameMessage(gameData);
        toSend.send(new Gson().toJson(loadMessage));
    }

}


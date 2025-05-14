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
        connections.put(userName, connection); // are these thread safe operations?
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
                    loadMessage = new LoadGameMessage(gameData.game());
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

    public void sendError(String userName, ErrorMessage errorMessage) throws IOException {
        if (errorMessage.getMessage().isEmpty()) {
            errorMessage = new ErrorMessage("Invalid AuthToken");
        }
        Connection toSend = connections.get(userName);
        toSend.send(new Gson().toJson(errorMessage));
    }

    public void sendMessage(String userName, NotificationMessage notificationMessage) throws IOException {
        Connection toSend = connections.get(userName);
        toSend.send(new Gson().toJson(notificationMessage));
    }

    public void sendGame(String userName, GameData gameData) throws IOException {
        Connection toSend = connections.get(userName);
        LoadGameMessage loadMessage = new LoadGameMessage(gameData.game());
        toSend.send(new Gson().toJson(loadMessage));
    }

}


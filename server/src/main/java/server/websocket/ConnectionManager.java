package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
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

    public void broadcast(String excludeName, Integer allGameID, ServerMessage serverMessage) throws IOException {
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

    public void send(Session session, ServerMessage serverMessage) throws IOException {
        String message = new Gson().toJson(serverMessage);
        for (Connection c : connections.values()) {
            if (c.session.equals(session)) {
                c.send(message);
                break;
            }
        }
    }
}


package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
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
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
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

            ServerMessage loadMessage = new LoadGameMessage(game.game());
            ServerMessage notifyMessage = new NotificationMessage(message);
            connections.send(session, loadMessage);
            connections.broadcast(authData.username(), game.gameID(), notifyMessage);
        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.send(session, errorMessage);
        }
    }

    private void makeMove() throws IOException {
        // throw error if not their turn? if not in game then they're observing
    }

    private void leave() throws IOException {

    }

    private void resign() throws IOException {

    }
}

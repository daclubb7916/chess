package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave(command, session);
            case RESIGN -> resign();
        }

    }

    // If sending a message, specify notify or error. Otherwise, don't specify
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

    private void makeMove() throws IOException {
        // throw error if not their turn? if not in game then they're observing
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());
            connections.remove(authData.username());

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

        } catch (DataAccessException ex) {
            ServerMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.sendMessage(session, errorMessage);
        }
    }

    private void resign() throws IOException {

    }
}

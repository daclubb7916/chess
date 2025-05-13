package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
    public void onMessage(Session session, String message) throws ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }

    }

    private void connect(UserGameCommand command, Session session) throws ResponseException {
        // add to connections, get the game, see if they're in the game, if not then they're observing
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            connections.add(authData.username(), session);
            GameData game = gameDAO.getGame(command.getGameID());

            String message;
            if (authData.username().equals(game.whiteUsername())) {
                message = String.format("%s joined Chess Game as White Player", authData.username());
            } else if (authData.username().equals(game.blackUsername())) {
                message = String.format("%s joined Chess Game as White Player", authData.username());
            } else {
                message = String.format("%s is observing Chess Game", authData.username());
            }

            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        } catch (DataAccessException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void makeMove() throws ResponseException {
        // throw error if not their turn? if not in game then they're observing
    }

    private void leave() {

    }

    private void resign() throws ResponseException {

    }
}

package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // Potential error?
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
                    switch (serverMessageType) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.loadGame(loadGameMessage);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.error(errorMessage);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notifyMessage = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notifyMessage);
                        }
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            // this might be happening too soon or affecting other gameplay
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            UserGameCommand command = new MakeMoveCommand(move, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}

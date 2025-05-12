package ui;

import exception.ResponseException;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import static ui.EscapeSequences.*;

public class GamePlay implements ClientUI {
    private String username;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public GamePlay(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.ws = null;
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        try {
            if (ws == null) {
                ws = new WebSocketFacade(serverUrl, notificationHandler);
            }

        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.INGAME, request.authToken(), request.gameID());
        }


        return new ClientResult(null, null, null, null);
    }

    public ClientResult help() {
        String result = """
                commands:
                    redraw - redraws the chess board
                    leave - leave current chess game
                    move <start> <end> - make a chess move (Example: move A2 A4)
                    resign - forfeit current chess game
                    legal moves <piece> - highlights legal moves for a chess piece
                    help - to view commands
                """;
        return new ClientResult(result, State.INGAME, null, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    // if leave or resign then set entering to true
}

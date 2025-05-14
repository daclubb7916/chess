package ui;

import exception.ResponseException;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;

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
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        try {
            if (ws == null) {
                ws = new WebSocketFacade(serverUrl, notificationHandler);
            }
            String input = request.input();
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw(request.authToken(), request.gameID());
                case "leave" -> leave(request.authToken(), request.gameID());
                case "move" -> move(params, request.authToken(), request.gameID());
                case "resign" -> resign(request.authToken(), request.gameID());
                case "legal" -> legalMoves(params, request.authToken(), request.gameID());
                default -> help(request.authToken(), request.gameID());
            };

        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.INGAME, request.authToken(), request.gameID());
        }
    }

    public ClientResult help(String authToken, Integer gameID) {
        String result = """
                commands:
                    redraw - redraws the chess board
                    leave - leave current chess game
                    move <start> <end> - make a chess move (Example: move A2 A4)
                    resign - forfeit current chess game
                    legal moves <piece> - highlights legal moves for a chess piece
                    help - to view commands
                """;
        return new ClientResult(result, State.INGAME, authToken, gameID);
    }

    private ClientResult redraw(String authToken, Integer gameID) {
        return new ClientResult("", State.INGAME, authToken, gameID);
    }

    private ClientResult leave(String authToken, Integer gameID) {
        return new ClientResult("", State.SIGNEDIN, authToken, gameID);
    }

    private ClientResult move(String[] params, String authToken, Integer gameID) {
        return new ClientResult("", State.INGAME, authToken, gameID);
    }

    private ClientResult resign(String authToken, Integer gameID) {
        return new ClientResult("", State.SIGNEDIN, authToken, gameID);
    }

    private ClientResult legalMoves(String[] params, String authToken, Integer gameID) {
        return new ClientResult("", State.INGAME, authToken, gameID);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }

}

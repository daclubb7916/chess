package ui.cl;

import exception.ResponseException;
import result.*;
import request.*;
import server.ServerFacade;
import ui.*;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PostLogin implements ClientUI {
    private final ServerFacade server;

    public PostLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        try {
            String input = request.input();
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(request.authToken());
                default -> help(request.authToken());
            };
        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.SIGNEDIN, request.authToken());
        }
    }

    public ClientResult help(String authToken) {
        String result = """
                commands:
                    create <NAME> - to create a chess game
                    list - to list all chess games
                    join <ID> [WHITE|BLACK] - to join a chess game
                    observe <ID> - to observe a chess game
                    logout - to exit to login menu
                    help - to view commands
                """;
        return new ClientResult(result, State.SIGNEDIN, authToken);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private ClientResult create(String... params) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, null);
    }

    private ClientResult list(String... params) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, null);
    }

    private ClientResult join(String... params) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, null);
    }

    private ClientResult observe(String... params) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, null);
    }

    private ClientResult logout(String authToken) throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return new ClientResult("Logged out user", State.SIGNEDOUT, null);
    }
}

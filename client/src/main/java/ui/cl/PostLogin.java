package ui.cl;

import exception.ResponseException;
import model.GameData;
import result.*;
import request.*;
import server.ServerFacade;
import ui.*;
import java.util.*;
import static ui.EscapeSequences.*;

public class PostLogin implements ClientUI {
    private final ServerFacade server;
    private Collection<GameData> games;

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
                case "create" -> create(params, request.authToken());
                case "list" -> list(request.authToken());
                case "join" -> join(params, request.authToken());
                case "observe" -> observe(params, request.authToken());
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

    private ClientResult create(String[] params, String authToken) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult result = server.createGame(new CreateGameRequest(params[0], authToken));
            String message = String.format("Chess Game '%s' created with gameId %d", params[0], result.gameID());
            return new ClientResult(message, State.SIGNEDIN, authToken);
        }
        throw new ResponseException(400, "Expected format: create <NAME>");
    }

    private ClientResult list(String authToken) throws ResponseException {
        ListGamesResult result = server.listGames(new ListGamesRequest(authToken));
        games = result.games();
        if (games.isEmpty()) {
            return new ClientResult("No Chess Games", State.SIGNEDIN, authToken);
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (GameData game : games) {
            sb.append("\n    ").append(i).append(". Name: ").append(game.gameName()).append(", White Player: ");
            sb.append(game.whiteUsername()).append(", Black Player: ").append(game.blackUsername());
            i += 1;
        }
        return new ClientResult(sb.toString(), State.SIGNEDIN, authToken);
    }

    private ClientResult join(String[] params, String authToken) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, authToken);
    }

    private ClientResult observe(String[] params, String authToken) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, authToken);
    }

    private ClientResult logout(String authToken) throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return new ClientResult("Logged out user", State.SIGNEDOUT, null);
    }
}

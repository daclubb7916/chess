package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import result.*;
import request.*;
import server.ServerFacade;

import java.util.*;
import static ui.EscapeSequences.*;

public class PostLogin implements ClientUI {
    private final ServerFacade server;
    private HashMap<Integer, GameData> games;

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
            return new ClientResult(ex.getMessage(), State.SIGNEDIN, request.authToken(), null);
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
        return new ClientResult(result, State.SIGNEDIN, authToken, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private ClientResult create(String[] params, String authToken) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult result = server.createGame(new CreateGameRequest(params[0], authToken));
            String message = String.format("Chess Game '%s' created", params[0]);
            return new ClientResult(message, State.SIGNEDIN, authToken, null);
        }
        throw new ResponseException(400, "Expected format: create <NAME>");
    }

    private ClientResult list(String authToken) throws ResponseException {
        ListGamesResult result = server.listGames(new ListGamesRequest(authToken));
        Collection<GameData> tmpList = result.games();
        if (tmpList.isEmpty()) {
            return new ClientResult("No Chess Games", State.SIGNEDIN, authToken, null);
        }

        HashMap<Integer, GameData> gameData = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (GameData game : tmpList) {
            String whiteUser = (game.whiteUsername() == null) ? "none" : game.whiteUsername();
            String blackUser = (game.blackUsername() == null) ? "none" : game.blackUsername();
            sb.append("\n    ").append(i).append(". Name: ").append(game.gameName()).append(", White Player: ");
            sb.append(whiteUser).append(", Black Player: ").append(blackUser);
            gameData.put(i, game);
            i += 1;
        }
        sb.append("\n");
        games = gameData;
        return new ClientResult(sb.toString(), State.SIGNEDIN, authToken, null);
    }

    private ClientResult join(String[] params, String authToken) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected format: join <ID> [WHITE|BLACK]");
        }
        int gameIndex = parseID(params[0]);
        if (games.isEmpty()) {
            throw new ResponseException(400, "No games yet");
        }
        GameData game = games.get(gameIndex);
        ChessBoard board = game.game().getBoard();

        switch (params[1]) {
            case "WHITE" -> {
                server.joinGame(new JoinGameRequest("WHITE", game.gameID(), authToken));
                System.out.print(board.stringBoard(ChessGame.TeamColor.WHITE));
            }
            case "BLACK" -> {
                server.joinGame(new JoinGameRequest("BLACK", game.gameID(), authToken));
                System.out.print(board.stringBoard(ChessGame.TeamColor.BLACK));
            }
            default -> throw new ResponseException(400, "Acceptable inputs for team color: [WHITE|BLACK]");
        }

        return new ClientResult("", State.INGAME, authToken, game.gameID()); // Check back on this later
    }

    private ClientResult observe(String[] params, String authToken) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected format: observe <ID>");
        }
        int gameIndex = parseID(params[0]);
        if (games.isEmpty()) {
            throw new ResponseException(400, "No games yet");
        }
        GameData game = games.get(gameIndex);
        ChessBoard board = game.game().getBoard();
        System.out.print(board.stringBoard(ChessGame.TeamColor.WHITE));

        return new ClientResult("", State.INGAME, authToken, game.gameID());
    }

    private ClientResult logout(String authToken) throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return new ClientResult("Logged out user", State.SIGNEDOUT, null, null);
    }

    private int parseID(String id) throws ResponseException {
        try {
            int gameID = Integer.parseInt(id);
            if (games.containsKey(gameID)) {
                return gameID;
            }
            throw new ResponseException(400, "Invalid gameID");
        } catch (NumberFormatException ex) {
            throw new ResponseException(400, "Invalid gameID");
        }
    }

}

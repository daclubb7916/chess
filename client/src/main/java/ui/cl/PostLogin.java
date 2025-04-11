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
            String message = String.format("Chess Game '%s' created", params[0]);
            return new ClientResult(message, State.SIGNEDIN, authToken);
        }
        throw new ResponseException(400, "Expected format: create <NAME>");
    }

    private ClientResult list(String authToken) throws ResponseException {
        ListGamesResult result = server.listGames(new ListGamesRequest(authToken));
        Collection<GameData> tmpList = result.games();
        if (tmpList.isEmpty()) {
            return new ClientResult("No Chess Games", State.SIGNEDIN, authToken);
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
        return new ClientResult(sb.toString(), State.SIGNEDIN, authToken);
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

        switch (params[1]) {
            case "WHITE" -> {
                server.joinGame(new JoinGameRequest("WHITE", game.gameID(), authToken));
                whiteChessBoard();
            }
            case "BLACK" -> {
                server.joinGame(new JoinGameRequest("BLACK", game.gameID(), authToken));
                blackChessBoard();
            }
            default -> throw new ResponseException(400, "Acceptable inputs for team color: [WHITE|BLACK]");
        }
        return new ClientResult("", State.SIGNEDIN, authToken);
    }

    private ClientResult observe(String[] params, String authToken) throws ResponseException {
        return new ClientResult(null, State.SIGNEDIN, authToken);
    }

    private ClientResult logout(String authToken) throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return new ClientResult("Logged out user", State.SIGNEDOUT, null);
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

    private void whiteChessBoard() {
        System.out.println();
        printWhiteAlpha();
        for (int i = 8; i > 0; i--) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            System.out.print(" " + i + " ");
            whiteRow(i);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            System.out.println(" " + i + " " + RESET_BG_COLOR);
        }
        printWhiteAlpha();
    }

    private void printWhiteAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + columns[i] + " ");
        }
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void whiteRow(int row) {
        String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] blackPieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        System.out.print(SET_TEXT_COLOR_BLUE);

        for (int i = 0; i < 8; i++) {
            if ((row + i) % 2 == 0) {
                System.out.print(SET_BG_COLOR_WHITE);
            } else {
                System.out.print(SET_BG_COLOR_BLACK);
            }

            switch (row) {
                case 8 -> System.out.print(blackPieces[i]);
                case 7 -> System.out.print(BLACK_PAWN);
                case 2 -> System.out.print(WHITE_PAWN);
                case 1 -> System.out.print(whitePieces[i]);
                default -> System.out.print(EMPTY);
            }
        }
    }

    private void blackChessBoard() {
        System.out.println();
        printBlackAlpha();
        for (int i = 1; i < 9; i++) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            System.out.print(" " + i + " ");
            blackRow(i);
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            System.out.println(" " + i + " " + RESET_BG_COLOR);
        }
        printBlackAlpha();
    }

    private void printBlackAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 9; i > -1; i--) {
            System.out.print(" " + columns[i] + " ");
        }
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void blackRow(int row) {
        String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING,
                WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] blackPieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING,
                BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        System.out.print(SET_TEXT_COLOR_BLUE);

        for (int i = 0; i < 8; i++) {
            if ((row + i) % 2 == 1) {
                System.out.print(SET_BG_COLOR_WHITE);
            } else {
                System.out.print(SET_BG_COLOR_BLACK);
            }

            switch (row) {
                case 8 -> System.out.print(blackPieces[i]);
                case 7 -> System.out.print(BLACK_PAWN);
                case 2 -> System.out.print(WHITE_PAWN);
                case 1 -> System.out.print(whitePieces[i]);
                default -> System.out.print(EMPTY);
            }
        }
    }
}

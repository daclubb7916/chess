package ui;

import chess.*;
import exception.ResponseException;
import model.GameData;
import request.ListGamesRequest;
import result.ListGamesResult;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class GamePlay implements ClientUI {
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
                case "redraw" -> redraw(request.authToken(), request.gameID(), request.userName());
                case "leave" -> leave(request.authToken(), request.gameID(), request.userName());
                case "move" -> move(params, request.authToken(), request.gameID(),
                        request.userName(), request.gameData());
                case "resign" -> resign(request.authToken(), request.gameID(), request.userName());
                case "legal" -> legalMoves(params, request.authToken(), request.gameID(),
                        request.userName(), request.gameData());
                case "escape" -> escape(request.authToken(), request.userName());
                default -> help(request.authToken(), request.gameID(), request.userName(), request.gameData());
            };

        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.INGAME, request.authToken(), request.gameID(),
                    request.userName(), request.gameData());
        }
    }

    private ClientResult escape(String authToken, String userName) {
        return new ClientResult("", State.SIGNEDIN, authToken,
                null, userName, null);
    }

    public ClientResult help(String authToken, Integer gameID, String userName, GameData gameData) {
        String result = """
                commands:
                    redraw - redraws the chess board
                    leave - leave current chess game
                    move <start> <end> - make a chess move (Example: move A2 A4)
                    resign - forfeit current chess game
                    legal moves <piece> - highlights legal moves for a chess piece
                    help - to view commands
                """;
        return new ClientResult(result, State.INGAME, authToken, gameID, userName, gameData);
    }

    private ClientResult redraw(String authToken, Integer gameID, String userName)
            throws ResponseException {
        GameData gameData = updateGameData(authToken, gameID);
        String result;
        ChessBoard chessBoard = gameData.game().getBoard();
        if (userName.equals(gameData.blackUsername())) {
            result = chessBoard.stringBoard(ChessGame.TeamColor.BLACK);
        } else {
            result = chessBoard.stringBoard(ChessGame.TeamColor.WHITE);
        }
        return new ClientResult(result, State.INGAME, authToken, gameID, userName, gameData);
    }

    private ClientResult leave(String authToken, Integer gameID, String userName) throws ResponseException {
        ws.leave(authToken, gameID);
        return new ClientResult("", State.SIGNEDIN, authToken, null, userName, null);
    }

    // update ChessGame after
    private ClientResult move(String[] params, String authToken, Integer gameID, String userName,
                              GameData gameData) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected Format: move <start> <end>\n(Example: move A2 A4)");
        }
        ChessMove chessMove = parseCoords(params);
        // white cannot move black, observer cannot move any, confirm it is their turn (maybe not in this method)
        return new ClientResult("", State.INGAME, authToken, gameID, userName, gameData);
    }

    private ClientResult resign(String authToken, Integer gameID, String userName) {
        return new ClientResult("", State.SIGNEDIN, authToken, null, userName, null);
    }

    //update ChessGame before
    private ClientResult legalMoves(String[] params, String authToken, Integer gameID,
                                    String userName, GameData gameData) {
        return new ClientResult("", State.INGAME, authToken, gameID, userName, gameData);
    }

    private GameData updateGameData(String authToken, Integer gameID) throws ResponseException {
        ListGamesResult result = server.listGames(new ListGamesRequest(authToken));
        Collection<GameData> gameList = result.games();
        for (GameData gameData : gameList) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        throw new ResponseException(400, "Failed to update GameData");
    }

    private ChessMove parseCoords(String[] params) throws ResponseException {
        String firstCoord = params[0];
        String secondCoord = params[1];
        if ((firstCoord.length() != 2) || (secondCoord.length() != 2)) {
            throw new ResponseException(400, "Invalid Coordinates, use format <start> <end>\n(Example: A2 A4)");
        }
        char firstCol = Character.toLowerCase(firstCoord.charAt(0));
        char secondCol = Character.toLowerCase(secondCoord.charAt(0));
        char firstRowChar = firstCoord.charAt(1);
        char secondRowChar = secondCoord.charAt(1);
        /*
        int startCol;
        switch (firstCol) {
            case 'a' -> startCol = 1;
            case 'b' -> startCol = 2;
            case 'c' -> startCol = 3;
            case 'd' -> startCol = 4;
            case 'e' -> startCol = 5;
            case 'f' -> startCol = 6;
            case 'g' -> startCol = 7;
            case 'h' -> startCol = 8;
            default -> throw new ResponseException(400, "Columns must be a letter from a-h");
        }
        int endCol;
        switch (secondCol) {
            case 'a' -> endCol = 1;
            case 'b' -> endCol = 2;
            case 'c' -> endCol = 3;
            case 'd' -> endCol = 4;
            case 'e' -> endCol = 5;
            case 'f' -> endCol = 6;
            case 'g' -> endCol = 7;
            case 'h' -> endCol = 8;
            default -> throw new ResponseException(400, "Columns must be a letter from a-h");
        }

        int startRow;
        switch (firstRow) {
            case '1' -> startRow = 1;
            case '2' -> startRow = 2;
            case '3' -> startRow = 3;
            case '4' -> startRow = 4;
            case '5' -> startRow = 5;
            case '6' -> startRow = 6;
            case '7' -> startRow = 7;
            case '8' -> startRow = 8;
            default -> throw new ResponseException(400, "Rows must be a number from 1-8");
        }
        int endRow;
        switch (secondRow) {
            case '1' -> endRow = 1;
            case '2' -> endRow = 2;
            case '3' -> endRow = 3;
            case '4' -> endRow = 4;
            case '5' -> endRow = 5;
            case '6' -> endRow = 6;
            case '7' -> endRow = 7;
            case '8' -> endRow = 8;
            default -> throw new ResponseException(400, "Rows must be a number from 1-8");
        }

         */
        int startCol = firstCol - 'a' + 1;
        int endCol = secondCol - 'a' + 1;
        if (startCol < 1 || startCol > 8 || endCol < 1 || endCol > 8) {
            throw new ResponseException(400, "Columns must be a letter from a-h");
        }

        int startRow = firstRowChar - '0';
        int endRow = secondRowChar - '0';
        if (startRow < 1 || startRow > 8 || endRow < 1 || endRow > 8) {
            throw new ResponseException(400, "Rows must be a number from 1-8");
        }

        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        return new ChessMove(startPosition, endPosition, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }

}

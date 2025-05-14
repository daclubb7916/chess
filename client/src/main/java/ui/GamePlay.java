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
                default -> help(request.authToken(), request.gameID(), request.userName(), request.gameData());
            };

        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.INGAME, request.authToken(), request.gameID(),
                    request.userName(), request.gameData());
        }
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
                              GameData gameData) {
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
        throw new ResponseException(500, "Failed to update GameData");
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }

}

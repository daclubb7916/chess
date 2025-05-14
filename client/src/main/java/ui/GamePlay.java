package ui;

import chess.*;
import exception.ResponseException;
import model.GameData;
import request.ListGamesRequest;
import result.ListGamesResult;
import facade.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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
                case "resign" -> resign(request.authToken(), request.gameID(), request.userName(), request.gameData());
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

    private ClientResult move(String[] params, String authToken, Integer gameID, String userName,
                              GameData gameData) throws ResponseException {
        checkIfObserver(userName, gameData);
        if (params.length != 2) {
            throw new ResponseException(400, "Expected Format: move <start> <end>\n(Example: move A2 A4)");
        }

        ChessPosition start = parseCoord(params[0]);
        ChessPosition end = parseCoord(params[1]);
        ChessMove chessMove = new ChessMove(start, end, null);
        ws.makeMove(authToken, gameID, chessMove);
        gameData = updateGameData(authToken, gameID);
        return new ClientResult("", State.INGAME, authToken, gameID, userName, gameData);
    }

    private ClientResult resign(String authToken, Integer gameID, String userName, GameData gameData)
            throws ResponseException {
        checkIfObserver(userName, gameData);
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Are you sure you want to resign? [y/n]\n");
        System.out.print(">>> " + SET_TEXT_COLOR_MAGENTA);

        String line = scanner.nextLine();
        if (line.equals("y")) {
            ws.resign(authToken, gameID);
            return new ClientResult("", State.SIGNEDIN, authToken, null, userName, null);
        } else if (line.equals("n")) {
            return new ClientResult("Successfully cancelled resignation", State.INGAME,
                    authToken, gameID, userName, gameData);
        }
        throw new ResponseException(400, "Failed to resign from Chess Game");
    }

    private ClientResult legalMoves(String[] params, String authToken, Integer gameID,
                                    String userName, GameData gameData) throws ResponseException {
        if ((params.length != 2) || (!params[0].equals("moves"))) {
            throw new ResponseException(400, "Expected format: legal moves <piece>\n(Example: legal moves A1");
        }
        checkIfObserver(userName, gameData);
        gameData = updateGameData(authToken, gameID);

        ChessPosition chessPosition = parseCoord(params[1]);
        ChessGame chessGame = gameData.game();
        Collection<ChessMove> chessMoves = chessGame.validMoves(chessPosition);
        String result;
        if (userName.equals(gameData.blackUsername())) {
            result = blackChessBoard(chessMoves, chessGame.getBoard());
        } else {
            result = whiteChessBoard(chessMoves, chessGame.getBoard());
        }

        return new ClientResult(result, State.INGAME, authToken, gameID, userName, gameData);
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

    private ChessPosition parseCoord(String coord) throws ResponseException {
        if (coord == null || coord.length() != 2) {
            throw new ResponseException(400, "Invalid coordinate format. Use format like A2");
        }

        char colChar = Character.toLowerCase(coord.charAt(0));
        char rowChar = coord.charAt(1);
        int col = colChar - 'a' + 1;
        int row = rowChar - '0';

        if (col < 1 || col > 8) {
            throw new ResponseException(400, "Column must be a letter from A to H");
        }
        if (row < 1 || row > 8) {
            throw new ResponseException(400, "Row must be a number from 1 to 8");
        }

        return new ChessPosition(row, col);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private void checkIfObserver(String userName, GameData gameData) throws ResponseException {
        if ((!userName.equals(gameData.whiteUsername())) && (!userName.equals(gameData.blackUsername()))) {
            throw new ResponseException(400, "This action cannot be performed as an Observer");
        }
    }

    private String whiteChessBoard(Collection<ChessMove> chessMoves, ChessBoard chessBoard) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(whiteAlpha());
        for (int i = 8; i > 0; i--) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ");
            stringBuilder.append(whiteRow(i - 1, chessMoves, chessBoard));
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ").append(RESET_BG_COLOR);
            stringBuilder.append("\n");
        }
        stringBuilder.append(whiteAlpha());
        return stringBuilder.toString();
    }

    private String whiteAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(" ").append(columns[i]).append(" ");
        }
        stringBuilder.append(RESET_BG_COLOR + RESET_TEXT_COLOR).append('\n');
        return stringBuilder.toString();
    }

    private String whiteRow(int row, Collection<ChessMove> chessMoves, ChessBoard chessBoard) {
        StringBuilder stringBuilder = new StringBuilder();

        ChessPosition comparator;
        for (int col = 0; col < 8; col++) {

            comparator = new ChessPosition(row + 1, col + 1);
            boolean isStartPosition = false;
            boolean isEndPosition = false;
            for (ChessMove chessMove : chessMoves) {
                if (chessMove.getStartPosition().equals(comparator)) {
                    isStartPosition = true;
                    break;
                } else if (chessMove.getEndPosition().equals(comparator)) {
                    isEndPosition = true;
                    break;
                }
            }

            if (isStartPosition) {
                stringBuilder.append(SET_BG_COLOR_YELLOW);
            } else if ((row + col) % 2 == 1) {
                if (isEndPosition) {
                    stringBuilder.append(SET_BG_COLOR_GREEN);
                } else {
                    stringBuilder.append(SET_BG_COLOR_WHITE);
                }

            } else {
                if (isEndPosition) {
                    stringBuilder.append(SET_BG_COLOR_DARK_GREEN);
                } else {
                    stringBuilder.append(SET_BG_COLOR_BLACK);
                }
            }

            ChessPiece piece = chessBoard.getPiece(comparator);
            stringBuilder.append(printPiece(piece, isStartPosition, isEndPosition));
        }
        return stringBuilder.toString();
    }

    private String printPiece(ChessPiece piece, boolean isStartPosition, boolean isEndPosition) {
        StringBuilder stringBuilder = new StringBuilder();

        if (piece == null) {
            stringBuilder.append(EMPTY);

        } else {

            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                stringBuilder.append(SET_TEXT_COLOR_RED);

                if ((isStartPosition) || (isEndPosition)) {
                    stringBuilder.append(SET_TEXT_COLOR_BLACK);
                }
                switch (piece.getPieceType()) {
                    case ChessPiece.PieceType.PAWN -> stringBuilder.append(WHITE_PAWN);
                    case ChessPiece.PieceType.ROOK -> stringBuilder.append(WHITE_ROOK);
                    case ChessPiece.PieceType.BISHOP -> stringBuilder.append(WHITE_BISHOP);
                    case ChessPiece.PieceType.KNIGHT -> stringBuilder.append(WHITE_KNIGHT);
                    case ChessPiece.PieceType.KING -> stringBuilder.append(WHITE_KING);
                    case ChessPiece.PieceType.QUEEN -> stringBuilder.append(WHITE_QUEEN);
                    default -> stringBuilder.append(EMPTY);
                }
            } else {
                stringBuilder.append(SET_TEXT_COLOR_BLUE);

                if ((isStartPosition) || (isEndPosition)) {
                    stringBuilder.append(SET_TEXT_COLOR_BLACK);
                }
                switch (piece.getPieceType()) {
                    case ChessPiece.PieceType.PAWN -> stringBuilder.append(BLACK_PAWN);
                    case ChessPiece.PieceType.ROOK -> stringBuilder.append(BLACK_ROOK);
                    case ChessPiece.PieceType.BISHOP -> stringBuilder.append(BLACK_BISHOP);
                    case ChessPiece.PieceType.KNIGHT -> stringBuilder.append(BLACK_KNIGHT);
                    case ChessPiece.PieceType.KING -> stringBuilder.append(BLACK_KING);
                    case ChessPiece.PieceType.QUEEN -> stringBuilder.append(BLACK_QUEEN);
                    default -> stringBuilder.append(EMPTY);
                }
            }

        }

        return stringBuilder.toString();
    }

    private String blackChessBoard(Collection<ChessMove> chessMoves, ChessBoard chessBoard) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(blackAlpha());
        for (int i = 1; i < 9; i++) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ");
            stringBuilder.append(blackRow(i - 1, chessMoves, chessBoard));
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ").append(RESET_BG_COLOR);
            stringBuilder.append("\n");
        }
        stringBuilder.append(blackAlpha());

        return stringBuilder.toString();
    }

    private String blackAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 9; i > -1; i--) {
            stringBuilder.append(" ").append(columns[i]).append(" ");
        }
        stringBuilder.append(RESET_BG_COLOR + RESET_TEXT_COLOR).append('\n');
        return stringBuilder.toString();
    }

    private String blackRow(int row, Collection<ChessMove> chessMoves, ChessBoard chessBoard) {
        StringBuilder stringBuilder = new StringBuilder();

        ChessPosition comparator;
        for (int col = 7; col > -1; col --) {

            comparator = new ChessPosition(row + 1, col + 1);
            boolean isStartPosition = false;
            boolean isEndPosition = false;
            for (ChessMove chessMove : chessMoves) {
                if (chessMove.getStartPosition().equals(comparator)) {
                    isStartPosition = true;
                    break;
                } else if (chessMove.getEndPosition().equals(comparator)) {
                    isEndPosition = true;
                    break;
                }
            }

            if (isStartPosition) {
                stringBuilder.append(SET_BG_COLOR_YELLOW);
            } else if ((row + col) % 2 == 0) {
                if (isEndPosition) {
                    stringBuilder.append(SET_BG_COLOR_GREEN);
                } else {
                    stringBuilder.append(SET_BG_COLOR_WHITE);
                }

            } else {
                if (isEndPosition) {
                    stringBuilder.append(SET_BG_COLOR_DARK_GREEN);
                } else {
                    stringBuilder.append(SET_BG_COLOR_BLACK);
                }
            }

            ChessPiece piece = chessBoard.getPiece(comparator);
            stringBuilder.append(printPiece(piece, isStartPosition, isEndPosition));
        }
        return stringBuilder.toString();

    }

}

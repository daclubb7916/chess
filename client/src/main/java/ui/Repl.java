package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import ui.websocket.NotificationHandler;
import websocket.messages.*;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;
    private State state;
    private String userName = "";

    public Repl(String serverUrl) {
        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl, this);
        gamePlay = new GamePlay(serverUrl, this);
        state = State.SIGNEDOUT;
    }

    public void run() {
        System.out.println("♔ This is Chess! Type 'help' to get started ♔");
        Scanner scanner = new Scanner(System.in);
        ClientResult clientResult = new ClientResult("", state, null, null,
                null, null);
        ClientUI ui = null;

        while (!clientResult.result().equals("quit")) {
            switch (state) {
                case SIGNEDOUT -> ui = preLogin;
                case SIGNEDIN -> ui = postLogin;
                case INGAME -> ui = gamePlay;
            }
            ui.printPrompt();
            String line = scanner.nextLine();

            try {
                clientResult = ui.eval(new ClientRequest(line, clientResult.authToken(), clientResult.gameID(),
                        clientResult.userName(), clientResult.gameData()));
                userName = clientResult.userName();
                System.out.print(clientResult.result());
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            state = clientResult.state();
        }
        System.out.println();
    }

    public void notify(NotificationMessage notifyMessage) {
        System.out.println(SET_TEXT_COLOR_RED + notifyMessage.getMessage());
        printPrompt();
    }

    public void loadGame(LoadGameMessage loadMessage) {
        GameData gameData = loadMessage.getGameData();
        ChessBoard chessBoard = gameData.game().getBoard();
        if (userName.equals(gameData.blackUsername())) {
            chessBoard.stringBoard(ChessGame.TeamColor.BLACK);
        } else {
            chessBoard.stringBoard(ChessGame.TeamColor.WHITE);
        }
        printPrompt();
    }

    public void error(ErrorMessage errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + "Error: " + errorMessage.getMessage());
        printPrompt();
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }
}

package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;
    private State state;

    public Repl(String serverUrl) {
        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl);
        gamePlay = new GamePlay(serverUrl);
        state = State.SIGNEDOUT;
    }

    public void run() {
        System.out.println("♔ This is Chess! Type 'help' to get started ♔");
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
            switch (state) {
                case SIGNEDOUT -> {
                    System.out.print("\nLogin ");
                    printPrompt();
                }
                case SIGNEDIN -> {
                    System.out.print("\nChess ");
                    printPrompt();
                }
                case INGAME -> {
                    System.out.print("\nChess Game ");
                    printPrompt();
                }
            }
        }
    }

    private void printPrompt() {
        System.out.print(">>> " + SET_TEXT_COLOR_MAGENTA);
    }

    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME
    }
}

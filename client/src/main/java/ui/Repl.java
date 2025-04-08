package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;

    public Repl(String serverUrl) {
        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl);
        gamePlay = new GamePlay(serverUrl);
    }
}

package ui;

import java.util.Scanner;

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
        ClientResult clientResult = new ClientResult("", state, null);
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
                clientResult = ui.eval(new ClientRequest(line, clientResult.authToken()));
                System.out.print(clientResult.result());
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            state = clientResult.state();
        }
        System.out.println();
    }
}

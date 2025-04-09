package ui.cl;

import ui.*;

public interface ClientUI {
    ClientResult eval(ClientRequest request);
    ClientResult help();
    void printPrompt();
}

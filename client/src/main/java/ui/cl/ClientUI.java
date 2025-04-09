package ui.cl;

import ui.*;

public interface ClientUI {
    ClientResult eval(ClientRequest request);
    void printPrompt();
}

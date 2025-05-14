package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {
    private final String message;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.message = errorMessage;
    }

    public String getMessage() {
        return message;
    }
}

package ui;

import model.GameData;

public record ClientResult(String result, State state, String authToken,
                           Integer gameID, String userName, GameData gameData) {
}

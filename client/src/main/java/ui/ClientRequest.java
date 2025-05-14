package ui;

import model.GameData;

public record ClientRequest(String input, String authToken, Integer gameID, String userName, GameData gameData) {
}

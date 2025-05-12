package ui;

import model.GameData;

public record ClientRequest(String input, String authToken, GameData gameData) {
}

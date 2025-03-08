package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GameDaoTests {
    GameDao games;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        games = new MySqlGameDao();
    }

    @Test
    public void addGame() {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());

        assertDoesNotThrow(() -> games.addGame(game));
    }
}

package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameDaoTests {
    GameDao games;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        games = new MySqlGameDao();
        games.clearGames();
    }

    @Test
    public void addGame() {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());

        assertDoesNotThrow(() -> games.addGame(game));
    }

    @Test
    public void addDuplicateGame() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());
        GameData game2 = new GameData(1, null, null, "gameName2", new ChessGame());

        games.addGame(game1);
        String error = "";
        try {
            games.addGame(game2);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assert !error.isBlank();
    }

    @Test
    public void clearGames() throws DataAccessException {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());
        games.addGame(game);
        games.clearGames();
        assertDoesNotThrow(() -> games.addGame(game));
    }
}

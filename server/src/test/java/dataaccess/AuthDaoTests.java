package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AuthDaoTests {
    AuthDao tokens;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        tokens = new MySqlAuthDao();
        tokens.clearAuthTokens();
    }

    @Test
    public void addGame() {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "name");

        assertDoesNotThrow(() -> tokens.addAuthToken(token));
    }
}

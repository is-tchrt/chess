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
    public void addAuthToken() {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "name");

        assertDoesNotThrow(() -> tokens.addAuthToken(token));
    }

    @Test
    public void addAuthTokenDuplicateUsername() throws DataAccessException {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "duplicate name");
        AuthData token2 = new AuthData(UUID.randomUUID().toString(), "duplicate name");

        tokens.addAuthToken(token);

        String error = "";
        try {
            tokens.addAuthToken(token2);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assert !error.isBlank();
    }

    @Test
    public void clearAuthTokens() throws DataAccessException {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "name");
        tokens.addAuthToken(token);
        tokens.clearAuthTokens();
        assertDoesNotThrow(() -> tokens.addAuthToken(token));
    }
}

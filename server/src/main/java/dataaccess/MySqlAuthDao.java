package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static java.sql.Types.NULL;

public class MySqlAuthDao implements AuthDao {

    public MySqlAuthDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearAuthTokens() throws DataAccessException {
        executeUpdate("TRUNCATE tokens");
    }

    @Override
    public void addAuthToken(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO tokens (authToken, username) VALUES (?, ?);";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public Collection<AuthData> listAuthTokens() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeAuthData(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS tokens (
                `id` int NOT NULL AUTO_INCREMENT,
                `authToken` varchar(256),
                `username` varchar(256),
                PRIMARY KEY (`id`),
                INDEX(authToken),
                UNIQUE(username)
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch(DataAccessException e) {
            throw new RuntimeException(String.format("Error creating database: %s", e.getMessage()));
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error configuring database: ".concat(e.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                PreparedStatement updatedStatement = setStatementVariables(ps, params);
                updatedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error updating database: ".concat(e.getMessage()));
        }
    }

    private PreparedStatement setStatementVariables(PreparedStatement ps, Object ... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case Integer p -> ps.setInt(i + 1, p);
                case String p -> ps.setString(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
        return ps;
    }
}

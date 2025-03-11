package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.NULL;

public class MySqlUserDao implements UserDao {

    public MySqlUserDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearUsers() throws DataAccessException {
        executeUpdate("TRUNCATE users");
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public Collection<UserData> listUsers() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                return formatListUsersResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                return formatGetUserResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public UserData getUserByNameAndPassword(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256),
                `password` varchar(256),
                `email` varchar(256),
                PRIMARY KEY (`id`),
                INDEX(username),
                INDEX(password),
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

    private ArrayList<UserData> formatListUsersResult(PreparedStatement ps) throws DataAccessException {
        ArrayList<UserData> result = new ArrayList<>();
        try (ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                result.add(new UserData(username, password, email));
            }
            return result;
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }

    private UserData formatGetUserResult(PreparedStatement ps) throws DataAccessException {
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                return new UserData(username, password, email);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }
}

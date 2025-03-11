package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDaoTests {
    UserDao users;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        users = new MySqlUserDao();
        users.clearUsers();
    }

    @Test
    public void addUser() {
        UserData user = new UserData("name", "password", "email");

        assertDoesNotThrow(() -> users.addUser(user));
    }

    @Test
    public void addUsersDuplicateUsername() throws DataAccessException {
        UserData user = new UserData("name", "password", "email");
        UserData user2 = new UserData("name", "password", "email");

        users.addUser(user);

        String error = "";
        try {
            users.addUser(user2);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assert !error.isBlank();
    }

    @Test
    public void clearUsers() throws DataAccessException {
        UserData user = new UserData("name", "password", "email");
        users.addUser(user);
        users.clearUsers();
        assertDoesNotThrow(() -> users.addUser(user));
    }
}

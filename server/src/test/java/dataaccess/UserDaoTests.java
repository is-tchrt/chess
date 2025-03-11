package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDaoTests {
    UserDao users;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        users = new MySqlUserDao();
//        tokens.clearAuthTokens();
    }

    @Test
    public void addUser() {
        UserData user = new UserData("name", "password", "email");

        assertDoesNotThrow(() -> users.addUser(user));
    }
}

package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
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

    @Test
    public void listUsers() throws DataAccessException {
        ArrayList<UserData> expected = new ArrayList<>();
        UserData user1 =
                new UserData("name1", "password1", "email1");
        UserData user2 =
                new UserData("name2", "password2", "email2");
        UserData user3 =
                new UserData("name3", "password3", "email3");

        users.addUser(user1);
        users.addUser(user2);
        users.addUser(user3);

        user1 =
                new UserData("name1", BCrypt.hashpw("password1", BCrypt.gensalt()), "email1");
        user2 =
                new UserData("name2", BCrypt.hashpw("password2", BCrypt.gensalt()), "email2");
        user3 =
                new UserData("name3", BCrypt.hashpw("password3", BCrypt.gensalt()), "email3");

        expected.add(user1);
        expected.add(user2);
        expected.add(user3);

        ArrayList<UserData> actual = new ArrayList<>(users.listUsers());
        for (int i = 0; i < expected.size(); i++) {
            assert expected.get(i).username().equals(actual.get(i).username());
            assert expected.get(i).email().equals(actual.get(i).email());
        }
    }

    @Test
    public void getUser() throws DataAccessException {
        UserData user =
                new UserData("username", "password", "email");

        users.addUser(user);

        UserData result = users.getUser("username");
        assert user.equals(result);
    }

    @Test
    public void getNonexistentUser() throws DataAccessException {
        UserData result = users.getUser("username");
        assert result == null;
    }
}

package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryUserDao implements UserDao {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public Collection<UserData> listUsers() {
        return users.values();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public UserData getUserByNameAndPassword(String username, String password) {
        UserData userData = getUser(username);
        if (userData == null) {
            return null;
        } else if (userData.password().equals(password)) {
            return userData;
        } else {
            return null;
        }
    }
}

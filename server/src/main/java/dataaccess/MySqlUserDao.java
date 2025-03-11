package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public class MySqlUserDao implements UserDao {
    @Override
    public void clearUsers() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addUser(UserData user) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<UserData> listUsers() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserData getUserByNameAndPassword(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeUser(String username) {
        throw new RuntimeException("Not implemented");
    }
}

package handler;

import dataaccess.*;
import service.Service;
import service.UserService;
import spark.Request;
import spark.Response;

public class HttpHandler {
    UserDao userDao = new MemoryUserDao();
    GameDao gameDao = new MemoryGameDao();
    AuthDao authDao = new MemoryAuthDao();

    Service service = new Service(userDao, gameDao, authDao);
    UserService userService = new UserService(userDao, gameDao, authDao);

    public Object clear(Request req, Response res) {
        service.clear();
        res.status(200);
        return "";
    }
}

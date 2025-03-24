package client;

import types.LoginResponse;
import model.UserData;

import java.util.Arrays;

public class PreLoginClient extends Client {

    public PreLoginClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public PreLoginClient(Client other) {
        super(other);
    }

    @Override
    public String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "help";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String help() {
        return COMMAND_NAME_COLOR + "help" + COMMAND_DESCRIPTION_COLOR + " - Display possible commands\n" +
                COMMAND_NAME_COLOR + "register <username> <password> <email>" + COMMAND_DESCRIPTION_COLOR + " - Create an" +
                " account\n" +
                COMMAND_NAME_COLOR + "login <username> <password>" + COMMAND_DESCRIPTION_COLOR + " - Login\n" +
                COMMAND_NAME_COLOR + "quit" + COMMAND_DESCRIPTION_COLOR + " - Exit the application";
    }

    private String register(String ... params) {
        if (params.length == 3) {
            try {
                LoginResponse response = serverFacade.register(new UserData(params[0], params[1], params[2]));
                authToken = response.authToken();
                username = response.username();
                System.out.println("Success!");
                return "login";
            } catch (HttpException e) {
                if (e.getStatusCode() == 403) {
                    return "This username is already taken, please try again with a different username";
                }
            }
            return "Something went wrong, please check your input and try again.";
        }
        else {
            return "The register command takes three arguments, a username, a password and an email. Please try again.";
        }
    }

    private String login(String ... params) {
        if (params.length == 2) {
            try {
                LoginResponse response = serverFacade.login(params[0], params[1]);
                authToken = response.authToken();
                username = response.username();
                System.out.println("Success!");
                return "login";
            } catch (HttpException e) {
                if (e.getStatusCode() == 401) {
                    return "The username or password you provided was incorrect";
                }
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The login command takes three arguments, a username, and a password. Please try again.";
        }
    }
}

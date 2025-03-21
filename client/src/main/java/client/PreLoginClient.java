package client;

import DataTypes.LoginResponse;
import model.UserData;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginClient extends Client {

    public PreLoginClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    @Override
    public String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "help";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            default -> help();
        };
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_BLACK + " - Display possible commands\n" +
                SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + SET_TEXT_COLOR_BLACK + " - Create an" +
                " account\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + SET_TEXT_COLOR_BLACK + " - Login\n" +
                SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_BLACK + " - Exit the application";
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
        if (params.length == 3) {
            try {
                LoginResponse response = serverFacade.login(params[0], params[1]);
                authToken = response.authToken();
                username = response.username();
                System.out.println("Success!");
                return "login";
            } catch (HttpException e) {
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The login command takes three arguments, a username, and a password. Please try again.";
        }
    }
}

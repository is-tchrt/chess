package client;

import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginClient extends Client {
    private HashMap<Integer, GameData> gameList;

    public PostLoginClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public PostLoginClient(Client other) {
        super(other);
    }

    @Override
    String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "help";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "create" -> create(parameters);
            case "list" -> list();
            case "join" -> join(parameters);
            case "observe" -> observe(parameters);
            case "logout" -> logout();
            default -> help();
        };
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_BLACK + " - Display possible commands\n" +
                SET_TEXT_COLOR_BLUE + "create <name>" + SET_TEXT_COLOR_BLACK + " - Create a new game\n" +
                SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_BLACK + " - List all games\n" +
                SET_TEXT_COLOR_BLUE + "join <id> <WHITE|BLACK>" + SET_TEXT_COLOR_BLACK + " - Join a game as the" +
                "specified color\n" +
                SET_TEXT_COLOR_BLUE + "observe <id>" + SET_TEXT_COLOR_BLACK + " - Observe a game\n" +
                SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_BLACK + " - Logout of your account\n" +
                SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_BLACK + " - Exit the application";
    }

    private String create(String ... params) {
        if (params.length == 1) {
            try {
                int response = serverFacade.createGame(params[0], authToken);
                System.out.println("Success!");
                return "Successfully created a game.";
            } catch (HttpException e) {
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The create command takes one argument, the name of the game you want to create. Please try again.";
        }
    }

    private String list() {
        ArrayList<GameData> games = new ArrayList<GameData>(serverFacade.listGames(authToken));
        String response = "";
        for (int i = 0; i < games.size(); i++) {
            response = response.concat(addGameListEntry(i, games.get(i)));
        }
        return response;
    }

    private String join(String ... params) {
        if (params.length == 2) {
            try {
                GameData selectedGame = gameList.get(Integer.parseInt(params[0]));
                serverFacade.joinGame(params[1], selectedGame.gameID(), authToken);
                System.out.println("You have joined " + selectedGame.gameName());
                game = selectedGame;
                return "join";
            } catch (Exception e) {
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The join command takes two arguments, the ID of the game you want to create and the color you" +
                    " want to play. Please try again.";
        }
    }

    private String observe(String ... params) {
        if (params.length == 1) {
            return "observe";
        }
        else {
            return "The observe command takes one argument, the ID of the game you want to observe. Please try again.";
        }
    }

    private String logout() {
        serverFacade.logout(authToken);
        System.out.println("Successfully logged out");
        return "logout";
    }

    private String addGameListEntry(int index, GameData gameData) {
        gameList.put(index, gameData);
        return index + " Name: " + gameData.gameName() + ", White: " + gameData.whiteUsername() + ", Black: " +
                gameData.blackUsername() + "\n";
    }
}

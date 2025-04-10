package client;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostLoginClient extends Client {
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
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String help() {
        return COMMAND_NAME_COLOR + "help" + COMMAND_DESCRIPTION_COLOR + " - Display possible commands\n" +
                COMMAND_NAME_COLOR + "create <name>" + COMMAND_DESCRIPTION_COLOR + " - Create a new game\n" +
                COMMAND_NAME_COLOR + "list" + COMMAND_DESCRIPTION_COLOR + " - List all games\n" +
                COMMAND_NAME_COLOR + "join <id> <WHITE|BLACK>" + COMMAND_DESCRIPTION_COLOR + " - Join a game as the" +
                " specified color\n" +
                COMMAND_NAME_COLOR + "observe <id>" + COMMAND_DESCRIPTION_COLOR + " - Observe a game\n" +
                COMMAND_NAME_COLOR + "logout" + COMMAND_DESCRIPTION_COLOR + " - Logout of your account\n" +
                COMMAND_NAME_COLOR + "quit" + COMMAND_DESCRIPTION_COLOR + " - Exit the application" + RESET_TEXT_COLOR;
    }

    private String create(String ... params) {
        if (params.length == 1) {
            try {
                serverFacade.createGame(params[0], authToken);
                return "Successfully created a game.";
            } catch (Exception e) {
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The create command takes one argument, the name of the game you want to create. Please try again.";
        }
    }

    private String list() {
        ArrayList<GameData> games = new ArrayList<>(serverFacade.listGames(authToken));
        String response = "";
        for (int i = 0; i < games.size(); i++) {
            response = response.concat(addGameListEntry(i, games.get(i)));
        }
        return response;
    }

    private String join(String ... params) {
        if (params.length == 2) {
            try {
                if (!params[1].equals("WHITE") && !(params[1].equals("BLACK"))) {
                    return "You must specify either WHITE or BLACK. The requested color must be typed in all caps.";
                }
                GameData selectedGame = gameList.get(Integer.parseInt(params[0]));
                serverFacade.joinGame(params[1], selectedGame.gameID(), authToken);
                System.out.println("You have joined " + selectedGame.gameName());
                game = selectedGame;
                if (params[1].equals("WHITE")) {
                    color = ChessGame.TeamColor.WHITE;
                } else if (params[1].equals("BLACK")) {
                    color = ChessGame.TeamColor.BLACK;
                }
                playing = true;
                return "join";
            } catch (NumberFormatException e) {
                return "Please specify the game you want to join using it's number in the list. Use the digit, don't type" +
                        " out the number (e.g. 'join 1 WHITE' not 'observe 1 WHITE')";
            } catch (Exception e) {
                return switch (e.getMessage()) {
                    case "Error: unauthorized" -> "quit";
                    case "Error: already taken" -> "That color is already taken. Please try again.";
                    default -> "Something went wrong, please check your input and try again.";
                };
            }
        }
        else {
            return "The join command takes two arguments, the ID of the game you want to create and the color you" +
                    " want to play. Please try again.";
        }
    }

    private String observe(String ... params) {
        if (params.length == 1) {
            try {
                game = gameList.get(Integer.parseInt(params[0]));
                color = ChessGame.TeamColor.WHITE;
                if (game == null) {
                    return "This game does not exist";
                }
                return "observe";
            } catch (NumberFormatException e) {
                return "Please specify the game you want to join using it's number in the list. Use the digit, don't type" +
                        " out the number (e.g. 'observe 1' not 'observe one')";
            } catch (Exception e) {
                return "Something went wrong, please check your input and try again.";
            }
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
        gameList.put(index + 1, gameData);
        return (index + 1) + " Name: " + gameData.gameName() + ", White: " + gameData.whiteUsername() + ", Black: " +
                gameData.blackUsername() + "\n";
    }
}

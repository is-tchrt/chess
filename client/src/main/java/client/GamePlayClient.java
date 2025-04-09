package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlayClient extends Client {
    private final WebSocketClient webSocketClient;

    public GamePlayClient(ServerFacade serverFacade) {
        super(serverFacade);
        try {
            webSocketClient = new WebSocketClient(serverFacade.getServerUrl(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame()));
        printBoard();
    }

    public GamePlayClient(Client other) {
        super(other);
        try {
            webSocketClient = new WebSocketClient(serverFacade.getServerUrl(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        printBoard();
    }

    @Override
    String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "leave";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move();
            case "resign" -> resign();
            case "highlight" -> highlight();
            default -> help();
        };
    }

    public String help() {
        return COMMAND_NAME_COLOR + "help" + COMMAND_DESCRIPTION_COLOR + " - Display possible commands\n" +
                COMMAND_NAME_COLOR + "redraw" + COMMAND_DESCRIPTION_COLOR + " - Redraw the board\n" +
                COMMAND_NAME_COLOR + "leave" + COMMAND_DESCRIPTION_COLOR + " - Leave the game\n" +
                COMMAND_NAME_COLOR + "move <id> <WHITE|BLACK>" + COMMAND_DESCRIPTION_COLOR + " - make the specified move" +
                " (Players only)" +
                COMMAND_NAME_COLOR + "resign" + COMMAND_DESCRIPTION_COLOR + " - Resign the game (Players only)\n" +
                COMMAND_NAME_COLOR + "highlight <piece>" + COMMAND_DESCRIPTION_COLOR + " - Highlight moves for the given piece";
    }

    public String redraw() {
        return getBoardString(game.game().getBoard());
    }

    public String leave() {
        webSocketClient.sendLeave();
        return "leave";
    }

    public String move() {
        throw new RuntimeException("Not implemented");
    }

    public String resign() {
        webSocketClient.sendResign();
        return "You have resigned.";
    }

    public String highlight() {
        throw new RuntimeException("Not implemented");
    }

    public void printNotification(String message) {
        System.out.println(message);
    }

    public void printBoard() {
        System.out.println(getBoardString(game.game().getBoard()));
    }

    public String getBoardString(ChessBoard board) {
        String letters = getLetters();
        StringBuilder printedBoard = new StringBuilder(letters);
        int rowNumber;
        int stepSize;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            rowNumber = 8;
            stepSize = -1;
        } else {
            rowNumber = 1;
            stepSize = 1;
        }
        boolean isEvenRow = true;
        for (int i = rowNumber - 1; (i < 8 && i >= 0); i += stepSize) {
            printedBoard.append(printBoardRow(board.getBoard()[i], rowNumber, isEvenRow ? SET_BG_COLOR_WHITE :
                    SET_BG_COLOR_MAGENTA, isEvenRow ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE));
            rowNumber += stepSize;
            isEvenRow = !isEvenRow;
        }
        printedBoard.append(letters);
        printedBoard.append(RESET_TEXT_COLOR);
        System.out.print(printedBoard.toString());
        return printedBoard.toString();
    }

    String printBoardRow(ChessPiece[] row, int rowNumber, String firstColor, String lastColor) {
//        String firstColor = rowNumber % 2 == 0 ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE;
//        String lastColor = rowNumber % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_MAGENTA;
        StringBuilder printedRow = new StringBuilder(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber +
                " ");
        boolean colorSwitch = true;
        int columnNumber;
        int stepSize;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            columnNumber = 1;
            stepSize = 1;
        } else {
            columnNumber = 8;
            stepSize = -1;
        }
        for (int i = columnNumber - 1; i < 8 && i >= 0; i += stepSize) {
            ChessPiece piece = row[i];
            printedRow.append(printBoardSquare(piece, colorSwitch ? firstColor : lastColor));
            colorSwitch = !colorSwitch;
        }
        printedRow.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber + " " + RESET_BG_COLOR +
                "\n");
        return printedRow.toString();
    }

    String printBoardSquare(ChessPiece piece, String squareColor) {
        String pieceCharacter;
        String pieceColor = "";
        if (piece == null) {
            pieceCharacter = EMPTY;
        } else {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                pieceColor = SET_TEXT_COLOR_BLUE;
            } else {
                pieceColor = SET_TEXT_COLOR_BLACK;
            }
            pieceCharacter = switch (piece.getPieceType()) {
                case ChessPiece.PieceType.KING -> BLACK_KING;
                case ChessPiece.PieceType.QUEEN -> BLACK_QUEEN;
                case ChessPiece.PieceType.ROOK -> BLACK_ROOK;
                case ChessPiece.PieceType.BISHOP -> BLACK_BISHOP;
                case ChessPiece.PieceType.KNIGHT -> BLACK_KNIGHT;
                case ChessPiece.PieceType.PAWN -> BLACK_PAWN;
            };
        }
        return squareColor + pieceColor + pieceCharacter;
    }

    String getLetters() {
        String whiteLetters = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    " + "a" + "  " + "b" + "  " + "c" + "  "
                + "d" + "  " + "e" + "  " + "f" + "  " + "g" + "  " + "h" + "    " + RESET_BG_COLOR + "\n";
        String blackLetters = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    " + "h" + "  " + "g" + "  " + "f" + "  "
                + "e" + "  " + "d" + "  " + "c" + "  " + "b" + "  " + "a" + "    " + RESET_BG_COLOR + "\n";
        return color.equals(ChessGame.TeamColor.WHITE) ? whiteLetters : blackLetters;
    }
}

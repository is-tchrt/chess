package client;

import chess.*;
import model.GameData;

import java.util.*;

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
            case "highlight" -> highlight(parameters);
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
                COMMAND_NAME_COLOR + "highlight <position>" + COMMAND_DESCRIPTION_COLOR + " - Highlight moves for the piece" +
                " at the given position. List the position using a letter for the column and a number for the row, e.g. e1.";
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

    public String highlight(String ... params) {
        if (params.length != 1) {
            return "The highlight command takes one argument, the position of the piece you want to see moves for." +
                    " Please try again.";
        }
        ChessPosition position = parsePositionParameter(params[0]);
        if (position == null) {
            return "The highlight command takes an argument specifying a board position, e.g. f7.";
        }
        Collection<ChessMove> validMoves = game.game().validMoves(position);
        String board = getHighlightedBoardString(game.game().getBoard(), position, getEndPositions(validMoves));
        return "";
    }

    public void printNotification(String message) {
        System.out.println(message);
    }

    public void printBoard() {
        System.out.println(getBoardString(game.game().getBoard()));
    }

    private String getBoardString(ChessBoard board) {
        return getHighlightedBoardString(board, null, null);
    }

    public String getHighlightedBoardString(ChessBoard board, ChessPosition currentPosition, Set<ChessPosition> validMoves) {
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
                    SET_BG_COLOR_MAGENTA, isEvenRow ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE, currentPosition, validMoves));
            rowNumber += stepSize;
            isEvenRow = !isEvenRow;
        }
        printedBoard.append(letters);
        printedBoard.append(RESET_TEXT_COLOR);
        System.out.print(printedBoard.toString());
        return printedBoard.toString();
    }

    String printBoardRow(ChessPiece[] row, int rowNumber, String firstColor, String lastColor, ChessPosition selectedPosition,
                         Set<ChessPosition> validMoves) {
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
            String squareColor;
            ChessPosition currentSquare = new ChessPosition(rowNumber, i + 1);
            if (validMoves.contains(currentSquare)) {
                squareColor = SET_BG_COLOR_GREEN;
            } else if (currentSquare.equals(selectedPosition)) {
                squareColor = SET_BG_COLOR_RED;
            } else {
                squareColor = colorSwitch ? firstColor : lastColor;
            }
            printedRow.append(printBoardSquare(piece, squareColor));
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

    ChessPosition parsePositionParameter(String parameter) {
        try {
            Integer column = switch (parameter.substring(0, 1)) {
                case "a" -> 1;
                case "b" -> 2;
                case "c" -> 3;
                case "d" -> 4;
                case "e" -> 5;
                case "f" -> 6;
                case "g" -> 7;
                case "h" -> 8;
                default -> null;
            };
            int row = Integer.parseInt(parameter.substring(1));
            if (row > 8 || row < 1 || column == null) {
                return null;
            }
            return new ChessPosition(row, column);
        } catch (Exception e) {
            return null;
        }
    }

    Set<ChessPosition> getEndPositions(Collection<ChessMove> moves) {
        Set<ChessPosition> positions = new HashSet<>();
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return positions;
    }
}

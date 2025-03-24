package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlayClient extends Client {

    public GamePlayClient(ServerFacade serverFacade) {
        super(serverFacade);
        printBoard(new ChessGame().getBoard());
    }

    public GamePlayClient(Client other) {
        super(other);
        printBoard(new ChessGame().getBoard());
    }

    @Override
    String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "leave";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "leave" -> "leave";
            case "quit" -> "quit";
            default -> "leave";
        };
    }

    String printBoard(ChessBoard board) {
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
                + "e" + "  " + "e" + "  " + "c" + "  " + "b" + "  " + "a" + "    " + RESET_BG_COLOR + "\n";
        return color.equals(ChessGame.TeamColor.WHITE) ? whiteLetters : blackLetters;
    }
}

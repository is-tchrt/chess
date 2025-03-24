package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.rmi.server.RMIClassLoader;

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
        return "";
    }

    String printBoard(ChessBoard board) {
        String letters = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK + "    " + "a" + "  " + "b" + "  " + "c" + "  "
                + "d" + "  " + "e" + "  " + "f" + "  " + "g" + "  " + "h" + "    " + SET_BG_COLOR_BLACK + "\n";
        StringBuilder printedBoard = new StringBuilder(letters);
        int rowNumber;
        int stepSize;
        int firstRow;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            rowNumber = 8;
            stepSize = -1;
        } else {
            rowNumber = 1;
            stepSize = 1;
        }
        for (int i = rowNumber - 1; (i < 8 && i >= 0); i += stepSize) {
            printedBoard.append(printBoardRow(board.getBoard()[i], rowNumber));
            rowNumber += stepSize;
        }
        printedBoard.append(letters);
        System.out.print(printedBoard.toString());
        return printedBoard.toString();
    }

    String printBoardRow(ChessPiece[] row, int rowNumber) {
        String firstColor = rowNumber % 2 == 0 ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_WHITE;
        String lastColor = rowNumber % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_MAGENTA;
        StringBuilder printedRow = new StringBuilder(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber +
                " ");
        boolean colorSwitch = true;
        for (ChessPiece piece : row) {
            printedRow.append(printBoardSquare(piece, colorSwitch ? firstColor : lastColor));
            colorSwitch = !colorSwitch;
        }
        printedRow.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber + " " + SET_BG_COLOR_BLACK +
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
                pieceColor = SET_TEXT_COLOR_GREEN;
            } else {
                pieceColor = SET_TEXT_COLOR_BLUE;
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
        System.out.println(pieceColor + BLACK_BISHOP);
        return squareColor + pieceColor + pieceCharacter;
    }
}

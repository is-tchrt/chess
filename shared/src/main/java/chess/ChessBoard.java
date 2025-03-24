package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard toCopy) {
        board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (toCopy.board[i][j] != null) {
                    board[i][j] = new ChessPiece(toCopy.board[i][j]);
                }
            }
        }
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRowIndex()][position.getColumnIndex()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRowIndex()][position.getColumnIndex()];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece.PieceType pieceType : ChessPiece.PieceType.values()) {
            addPieceByType(ChessGame.TeamColor.WHITE, pieceType);
            addPieceByType(ChessGame.TeamColor.BLACK, pieceType);
        }
    }

    private void addPieceByType(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        for (int column : pieceType.initialColumns) {
            if (pieceType == ChessPiece.PieceType.PAWN) {
                addPiece(new ChessPosition(teamColor.secondRow, column), new ChessPiece(teamColor, pieceType));
            } else {
                addPiece(new ChessPosition(teamColor.firstRow, column), new ChessPiece(teamColor, pieceType));
            }
        }
    }
}

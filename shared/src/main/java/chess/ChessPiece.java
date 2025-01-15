package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = getPositiveDiagonalMoves(board, myPosition);
        moves.addAll(getNegativeDiagonalMoves(board, myPosition));
        return moves;
    }

    /**
     * Calculates all possible diagonal moves on the positive-sloped diagonal
     *
     * @param position Current piece position
     * @return Collection of valid moves
     */
    public ArrayList<ChessMove> getPositiveDiagonalMoves(ChessBoard board, ChessPosition position) {
        int rowColumnDifference = position.getRow() - position.getColumn();
        int row;
        int col;

        //Calculate where the positive diagonal meets the board edge and set the initial
        //row and column coordinates accordingly
        if (rowColumnDifference >= 0) {
            row = rowColumnDifference;
            col = 0;
        } else {
            row = 0;
            col = -1 * rowColumnDifference;
        }

        return getMovesByStep(row, col, 1, 1, board, position);
    }

    /**
     * Calculates all possible diagonal moves on the negative-sloped diagonal
     *
     * @param position Current piece position
     * @return Collection of possible moves
     */
    public ArrayList<ChessMove> getNegativeDiagonalMoves(ChessBoard board, ChessPosition position) {
        int rowColumnDifference = 7 - position.getRow() - position.getColumn();
        int row;
        int col;
        if (rowColumnDifference >= 0) {
            row = 7 - rowColumnDifference;
            col = 0;
        } else {
            row = 7;
            col = -1 * rowColumnDifference;
        }

        return getMovesByStep(row, col, -1, 1, board, position);
    }

    public ArrayList<ChessMove> getMovesByStep(int startRow, int startCol, int rowStep, int colStep, ChessBoard board, ChessPosition position) {
        int currentRow = startRow;
        int currentCol = startCol;
        ArrayList<ChessMove> moves = new ArrayList<>();
        while (currentRow < 8 && currentRow >= 0 && currentCol < 8 && currentCol >= 0) {
            if (currentRow != position.getRow() && board.getPiece(position) == null) {
                moves.add(new ChessMove(position, new ChessPosition(currentRow, currentCol), type));
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return moves;
    }
}

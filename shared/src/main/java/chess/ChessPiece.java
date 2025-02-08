package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece toCopy) {
        teamColor = toCopy.teamColor;
        type = toCopy.type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL, CalculateMoves.MoveType.SQUARE}, false, new int[] {5}),
        QUEEN (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL, CalculateMoves.MoveType.SQUARE}, true, new int[] {4}),
        BISHOP (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL}, true, new int[] {3, 6}),
        KNIGHT (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.KNIGHT}, false, new int[] {2, 7}),
        ROOK (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.SQUARE}, true, new int[] {1, 8}),
        PAWN (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.PAWN}, false, new int[] {1, 2, 3, 4, 5, 6, 7, 8});

        public final CalculateMoves.MoveType[] moveTypes;
        public final boolean repeated;
        public final int[] initialColumns;

        PieceType(CalculateMoves.MoveType[] moveTypes, boolean repeated, int[] initialColumns) {
            this.moveTypes = moveTypes;
            this.repeated = repeated;
            this.initialColumns = initialColumns;
        }
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + teamColor +
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
        return CalculateMoves.getMoves(board, this, myPosition);
    }
}

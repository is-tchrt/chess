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
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL, CalculateMoves.MoveType.SQUARE}, false),
        QUEEN (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL, CalculateMoves.MoveType.SQUARE}, true),
        BISHOP (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.DIAGONAL}, true),
        KNIGHT (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.KNIGHT}, false),
        ROOK (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.SQUARE}, true),
        PAWN (new CalculateMoves.MoveType[] {CalculateMoves.MoveType.PAWN}, false);

        public final CalculateMoves.MoveType[] moveTypes;
        public final boolean repeated;

        PieceType(CalculateMoves.MoveType[] moveTypes, boolean repeated) {
            this.moveTypes = moveTypes;
            this.repeated = repeated;
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

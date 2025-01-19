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

        public CalculateMoves.MoveType[] moveTypes;
        public boolean repeated;

        private PieceType(CalculateMoves.MoveType[] moveTypes, boolean repeated) {
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
            col = 1;
        } else {
            row = 1;
            col = (-1 * rowColumnDifference) + 1;
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
        int rowColumnDifference = (9 - position.getRow()) - position.getColumn(); //Use 9 so that we get the number of rows from the top to the current position
        int row;
        int col;
        if (rowColumnDifference >= 0) {
            row = 8 - rowColumnDifference;
            col = 1;
        } else {
            row = 8;
            col = (-1 * rowColumnDifference) + 1;
        }

        return getMovesByStep(row, col, -1, 1, board, position);
    }

    public ArrayList<ChessMove> getMovesByStep(int startRow, int startCol, int rowStep, int colStep, ChessBoard board, ChessPosition position) {
        int currentRow = startRow;
        int currentCol = startCol;
        System.out.println("row " + startRow + " col " + startCol + " rowStep " + rowStep);
        ArrayList<ChessMove> moves = new ArrayList<>();
        while (currentRow <= 8 && currentRow >= 0 && currentCol <= 8 && currentCol >= 0) {
            System.out.println(currentRow);
            if (currentRow != position.getRow() && board.getPiece(new ChessPosition(currentRow, currentCol)) == null) {
                moves.add(new ChessMove(position, new ChessPosition(currentRow, currentCol), null));
            }
            currentRow += rowStep;
            System.out.println(currentRow);
            currentCol += colStep;
        }
        return moves;
    }
}

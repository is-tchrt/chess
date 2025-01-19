package chess;

import java.util.ArrayList;

public class CalculateMoves {

    public enum MoveType {
        DIAGONAL(new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}),
        SQUARE(new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}),
        PAWN(new int[][] {{}}),
        KNIGHT(new int[][] {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}});

        private final int[][] steps;

        MoveType(int[][] steps) {
            this.steps = steps;
        }
    }

    public static ArrayList<ChessMove> getMoves(ChessBoard board, ChessPiece piece, ChessPosition position) {
        ArrayList<ChessMove> moves;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            moves = new ArrayList<>();
        } else {
            moves = getNonPawnMoves(board, piece, position);
        }
        return moves;
    }

    private static ArrayList<ChessMove> getNonPawnMoves(ChessBoard board, ChessPiece piece, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (MoveType moveType : piece.getPieceType().moveTypes) {
            moves.addAll(getMovesByType(board, piece, position, moveType));
        }
        return moves;
    }

    private static ArrayList<ChessMove> getMovesByType(ChessBoard board, ChessPiece piece, ChessPosition position, MoveType moveType) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] step : moveType.steps) {
            moves.addAll(getMovesByStep(board, piece, position, step));
        }
        return moves;
    }


    private static ArrayList<ChessMove> getMovesByStep(ChessBoard board, ChessPiece piece, ChessPosition position, int[] step) {
        ChessPosition nextPosition = new ChessPosition(position.getRow() + step[0], position.getColumn() + step[1]);
        ArrayList<ChessMove> moves = new ArrayList<>();
        boolean repeat = piece.getPieceType().repeated;
        while (true) {
            if (nextPosition.getColumn() > 8 || nextPosition.getColumn() < 1 || nextPosition.getRow() > 8 || nextPosition.getRow() < 1) {
                break;
            } else if (board.getPiece(nextPosition) != null) {
                if (board.getPiece(nextPosition).getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, nextPosition, null));
                }
                break;
            } else {
                moves.add(new ChessMove(position, nextPosition, null));
            }
            if (!repeat) {
                break;
            } else {
                nextPosition = new ChessPosition(nextPosition.getRow() + step[0], nextPosition.getColumn() + step[1]);
            }
        }
        return moves;
    }
}

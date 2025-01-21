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
            moves = getPawnMoves(board, piece, position);
        } else {
            moves = getNonPawnMoves(board, piece, position);
        }
        return moves;
    }


    private static ArrayList<ChessMove> getPawnMoves(ChessBoard board, ChessPiece piece, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition forwardOne = new ChessPosition(position.getRow() + piece.getTeamColor().pawnStep, position.getColumn());
        if (board.getPiece(forwardOne) == null) {
            moves.addAll(getPawnPromotionOrNormalMoves(piece, position, forwardOne));

            ChessPosition forwardTwo = new ChessPosition(position.getRow() + (2 * piece.getTeamColor().pawnStep), position.getColumn());
            if (position.getRow() == piece.getTeamColor().secondRow && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(position, forwardTwo, null));
            }
        }
        if (position.getColumn() > 1) {
            ChessPosition forwardLeft = new ChessPosition(position.getRow() + piece.getTeamColor().pawnStep, position.getColumn() - 1);
            moves.addAll(getPawnCaptureMoves(board, piece, position, forwardLeft));
        }
        if (position.getColumn() < 8) {
            ChessPosition forwardRight = new ChessPosition(position.getRow() + piece.getTeamColor().pawnStep, position.getColumn() + 1);
            moves.addAll(getPawnCaptureMoves(board, piece, position, forwardRight));
        }
        return moves;
    }


    private static ArrayList<ChessMove> getPawnCaptureMoves(ChessBoard board, ChessPiece piece, ChessPosition position, ChessPosition newPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
            moves = getPawnPromotionOrNormalMoves(piece, position, newPosition);
        }
        return moves;
    }


    private static ArrayList<ChessMove> getPawnPromotionOrNormalMoves(ChessPiece piece, ChessPosition position, ChessPosition newPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() == piece.getTeamColor().secondToLastRow) {
            moves = getPawnPromotionMoves(position, newPosition);
        } else {
            moves.add(new ChessMove(position, newPosition, null));
        }
        return moves;
    }


    private static ArrayList<ChessMove> getPawnPromotionMoves(ChessPosition start, ChessPosition end) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
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

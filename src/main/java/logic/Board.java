package logic;

import javafx.util.Pair;

import java.util.*;
import java.util.regex.Pattern;

import static logic.enums.*;
import static logic.enums.PieceType.*;
import static logic.enums.Square.*;

public class Board {

    public Board() {
        init();
    }

    public Board(String FEN) {
        initFEN(FEN);
    }

    private long figures;
    private long whites;
    private long blacks;

    private long rooks;
    private long knights;
    private long bishops;
    private long queens;
    private long kings;
    private long pawns;

    private long castlingInfo; // 0 bit - short rook 1 bit - long rook 2 bit - king
    private int enPassant;
    private int whiteKing;
    private int blackKing;

    private boolean checkMate;
    private boolean gameOver;
    private boolean turn;

    /**
     * Init default board
     */
    private void init() {
        figures = 0b1111111111111111000000000000000000000000000000001111111111111111L;
        whites  = 0b0000000000000000000000000000000000000000000000001111111111111111L;
        blacks  = 0b1111111111111111000000000000000000000000000000000000000000000000L;
        rooks   = 0b1000000100000000000000000000000000000000000000000000000010000001L;
        knights = 0b0100001000000000000000000000000000000000000000000000000001000010L;
        bishops = 0b0010010000000000000000000000000000000000000000000000000000100100L;
        queens  = 0b0000100000000000000000000000000000000000000000000000000000001000L;
        kings   = 0b0001000000000000000000000000000000000000000000000000000000010000L;
        pawns   = 0b0000000011111111000000000000000000000000000000001111111100000000L;

        castlingInfo = 0b111111L;
        enPassant = 0;
        whiteKing = 4;
        blackKing = 60;

        checkMate = false;
        gameOver = false;
        turn = true;

        Magic.init();
    }

    /**
     * Init board by FEN representation
     * If FEN is incorrect - init default board
     * @param FEN - representation of the board
     */
    public void initFEN(String FEN) {
        castlingInfo = 0;
        enPassant = 0;
        figures = 0;
        whites  = 0;
        blacks  = 0;
        rooks   = 0;
        knights = 0;
        bishops = 0;
        queens  = 0;
        kings   = 0;
        pawns   = 0;
        Pattern pattern = Pattern.compile
                ("([PRNBQKprnbqk\\d]{1,8}/){7}[PRNBQKprnbqk\\d]{1,8} [wb] ([KQkq]{1,4}|-) (\\d{1,2}|-)( (\\d+) (\\d+))?");
        if (!pattern.matcher(FEN).find()) {
            this.init();
            return;
        }
        String[] parts = FEN.split(" ");
        if (parts.length < 4) {
            this.init();
            return;
        }
        String[] positions = parts[0].split("/");
        if (positions.length != 8) {
            this.init();
            return;
        }
        long square;
        int was;
        for (int i = 0; i < 8; i++) {
            square = 0b1L << (8 * (7 - i));
            was = (8 * (7 - i));
            for (char c : positions[i].toCharArray()) {
                switch (c) {
                    case 'R':
                        figures |= square;
                        whites |= square;
                        rooks |= square;
                        break;
                    case 'N':
                        figures |= square;
                        whites |= square;
                        knights |= square;
                        break;
                    case 'B':
                        figures |= square;
                        whites |= square;
                        bishops |= square;
                        break;
                    case 'Q':
                        figures |= square;
                        whites |= square;
                        queens |= square;
                        break;
                    case 'K':
                        figures |= square;
                        whites |= square;
                        kings |= square;
                        whiteKing = was;
                        break;
                    case 'P':
                        figures |= square;
                        whites |= square;
                        pawns |= square;
                        break;
                    case 'r':
                        figures |= square;
                        blacks |= square;
                        rooks |= square;
                        break;
                    case 'n':
                        figures |= square;
                        blacks |= square;
                        knights |= square;
                        break;
                    case 'b':
                        figures |= square;
                        blacks |= square;
                        bishops |= square;
                        break;
                    case 'q':
                        figures |= square;
                        blacks |= square;
                        queens |= square;
                        break;
                    case 'k':
                        figures |= square;
                        blacks |= square;
                        kings |= square;
                        blackKing = was;
                        break;
                    case 'p':
                        figures |= square;
                        blacks |= square;
                        pawns |= square;
                        break;
                    default:
                        square <<= c - '0' - 1;
                        was += c - '0' - 1;
                        break;
                }
                square <<= 1;
                was++;
            }
        }
        turn = parts[1].equals("w");
        for (char c : parts[2].toCharArray()) {
            switch (c) {
                case 'K':
                    castlingInfo |= 0b1L << 2;
                    castlingInfo |= 0b1L;
                    break;
                case 'Q':
                    castlingInfo |= 0b1L << 2;
                    castlingInfo |= 0b1L << 1;
                    break;
                case 'k':
                    castlingInfo |= 0b1L << 5;
                    castlingInfo |= 0b1L << 3;
                    break;
                case 'q':
                    castlingInfo |= 0b1L << 5;
                    castlingInfo |= 0b1L << 4;
                    break;
            }
        }
        if (!parts[3].equals("-")) {
            enPassant = Integer.valueOf(parts[3]);
        }

        Magic.init();
    }

    public List<Move> getAllMoves() {
        List<Move> movesList = new ArrayList<>();
        long ours = turn ? whites : blacks;
        long enemy = turn ? blacks : whites;
        long currentSquare = 0b1L;
        enums.PieceType type;
        for (int i = 0; i < 64; i++) {
            if ((ours & currentSquare) != 0) {
                type = getType(currentSquare);
                if (type != PAWN) {
                    if (type == KING) {
                        movesList.addAll(generateKingMoves(i));
                        currentSquare <<= 1;
                        continue;
                    }
                    if (type == ROOK) {
                        movesList.addAll(generateRookMoves(i));
                        currentSquare <<= 1;
                        continue;
                    }
                    long moves =
                            type == enums.PieceType.BISHOP ||
                            type == enums.PieceType.QUEEN ?
                            Magic.getPseudoLegalMoves(figures, i, type) & ~ours :
                            type == enums.PieceType.KNIGHT ?
                            Moves.KnightMoves[i] & ~ours : 0;
                    long square = 0b1L;
                    for (int j = 0; j < 64; j++) {
                        if ((moves & square) != 0) {
                            Move m;
                            if ((enemy & square) != 0) {
                                m = new Move(i, j, getType(square).type, enPassant);
                            } else {
                                m = new Move(i, j, enPassant);
                            }
                            movesList.add(m);
                        }
                        square <<= 1;
                    }
                } else {
                    movesList.addAll(generatePawnMoves(i));
                }
            }
            currentSquare <<= 1;
        }
        List<Move> movesCopy = new ArrayList<>(movesList);
        int wasrem = 0;
        for (int i = 0; i < movesCopy.size(); i++) {
            if (!makeMove(movesList.get(i - wasrem))) {
                movesList.remove(i - wasrem);
                wasrem++;
            } else {
                unMove(movesCopy.get(i));
            }
        }
        if (movesList.size() == 0) {
            gameOver = true;
        }
        if (isCheck(turn) && gameOver) {
            checkMate = true;
        }
        return movesList;
    }

    public boolean makeMove(Move move) {
        if (gameOver) {
            return false;
        }
        int[] moveData = move.get();
        long fromSquare = ~(0b1L << moveData[0]);
        long toSquare = 0b1L << moveData[1];
        enums.PieceType type = getType(~fromSquare);
        // Clear toSquare if capturing
        if (moveData[5] == 1 || (moveData[5] == 2 && moveData[2] != 0)) {
            captureFigure(~toSquare);
        }
        // Basic for every move: delete old pos, append new pos
        switch (type) {
            case ROOK:
                rooks &= fromSquare;
                rooks |= toSquare;
                switch (moveData[0]) {
                    case 7:
                        castlingInfo &= ~(0b1L);
                        break;
                    case 0:
                        castlingInfo &= ~(0b1L << 1);
                        break;
                    case 63:
                        castlingInfo &= ~(0b1L << 3);
                        break;
                    case 56:
                        castlingInfo &= ~(0b1L << 4);
                        break;
                }
                break;
            case KNIGHT:
                knights &= fromSquare;
                knights |= toSquare;
                break;
            case BISHOP:
                bishops &= fromSquare;
                bishops |= toSquare;
                break;
            case QUEEN:
                queens &= fromSquare;
                queens |= toSquare;
                break;
            case KING:
                if (turn) {
                    whiteKing = moveData[1];
                    castlingInfo &= ~(0b1L << 2);
                } else {
                    blackKing = moveData[1];
                    castlingInfo &= ~(0b1L << 5);
                }
                kings &= fromSquare;
                kings |= toSquare;
                break;
            case PAWN:
                pawns &= fromSquare;
                pawns |= toSquare;
                break;
        }
        figures &= fromSquare;
        figures |= toSquare;
        if (turn) {
            whites &= fromSquare;
            whites |= toSquare;
        } else {
            blacks &= fromSquare;
            blacks |= toSquare;
        }
        // End basic

        // Deletes pawn, appends new figure
        if (moveData[5] == 2) {
            promotion(moveData);
        }

        // Deletes enPassant pawn
        if (moveData[5] == 4) {
            enPassant();
        }

        // moves rook
        if (moveData[5] == 3) {
            castling(moveData);
        }

        // Checks for legality
        if (isCheck(turn)) {
            turn = !turn;
            unMove(move);
            return false;
        }

        if (moveData[5] != 2) {
            enPassant = moveData[4];
        } else {
            enPassant = 0;
        }
        turn = !turn;
        return true;
    }

    public void unMove(Move move) {
        checkMate = false;
        gameOver = false;
        int[] moveData = move.get();
        long fromSquare = ~(0b1L << moveData[1]);
        long toSquare = 0b1L << moveData[0];
        enPassant = moveData[3];
        enums.PieceType type = getType(~fromSquare);

        // Basic: remove figure from new pos, append to old
        switch (type) {
            case ROOK:
                rooks &= fromSquare;
                rooks |= toSquare;
                break;
            case KNIGHT:
                knights &= fromSquare;
                knights |= toSquare;
                break;
            case BISHOP:
                bishops &= fromSquare;
                bishops |= toSquare;
                break;
            case QUEEN:
                queens &= fromSquare;
                queens |= toSquare;
                break;
            case KING:
                if (!turn) {
                    whiteKing = moveData[0];
                } else {
                    blackKing = moveData[0];
                }
                kings &= fromSquare;
                kings |= toSquare;
                break;
            case PAWN:
                pawns &= fromSquare;
                pawns |= toSquare;
                break;
        }
        figures &= fromSquare;
        figures |= toSquare;
        if (turn) {
            blacks &= fromSquare;
            blacks |= toSquare;
        } else {
            whites &= fromSquare;
            whites |= toSquare;
        }
        // Basic end

        // Append to old pos figure if there was capture
        if (moveData[5] == 1) {
            unCaptureFigure(moveData[1], PieceType.fromInt(moveData[2]));
        }

        // Removes promotion if there was it and uncaptures if needed
        if (moveData[5] == 2) {
            unPromotion(moveData);
        }

        // Adds enPassant figure
        if (moveData[5] == 4) {
            unEnPassant();
        }

        // uncastling & recovery
        if (moveData[5] == 3) {
            unCastling(moveData);
        } else {
            castlingRecovery(moveData);
        }

        turn = !turn;
    }

    private void castling(int[] moveData) {
        long square = 0b1L;
        switch (moveData[2]) {
            case 1:
                square <<= 5;
                figures |= square;
                rooks |= square;
                whites |= square;
                square <<= 2;
                rooks &= ~square;
                whites &= ~square;
                figures &= ~square;
                break;
            case 2:
                square <<= 3;
                figures |= square;
                rooks |= square;
                whites |= square;
                square >>>= 3;
                rooks &= ~square;
                whites &= ~square;
                figures &= ~square;
                break;
            case 3:
                square <<= 61;
                figures |= square;
                rooks |= square;
                blacks |= square;
                square <<= 2;
                rooks &= ~square;
                blacks &= ~square;
                figures &= ~square;
                break;
            case 4:
                square <<= 59;
                figures |= square;
                rooks |= square;
                blacks |= square;
                square >>>= 3;
                rooks &= ~square;
                blacks &= ~square;
                figures &= ~square;
                break;
        }
    }

    private void unCastling(int[] moveData) {
        long square = 0b1L;
        switch (moveData[2]) {
            case 1:
                square <<= 5;
                rooks &= ~square;
                whites &= ~square;
                figures &= ~square;
                square <<= 2;
                rooks |= square;
                whites |= square;
                figures |= square;
                square = 0b1L;
                castlingInfo |= square;
                castlingInfo |= (square << 2);
                break;
            case 2:
                square <<= 3;
                rooks &= ~square;
                whites &= ~square;
                figures &= ~square;
                square >>>= 3;
                rooks |= square;
                whites |= square;
                figures |= square;
                square = 0b1L;
                castlingInfo |= (square << 1);
                castlingInfo |= (square << 2);
                break;
            case 3:
                square <<= 61;
                rooks &= ~square;
                blacks &= ~square;
                figures &= ~square;
                square <<= 2;
                rooks |= square;
                blacks |= square;
                figures |= square;
                square = 0b1L;
                castlingInfo |= (square << 3);
                castlingInfo |= (square << 5);
                break;
            case 4:
                square <<= 59;
                rooks &= ~square;
                blacks &= ~square;
                figures &= ~square;
                square >>>= 3;
                rooks |= square;
                blacks |= square;
                figures |= square;
                square = 0b1L;
                castlingInfo |= (square << 4);
                castlingInfo |= (square << 5);
                break;
        }
        if (turn) {
            castlingInfo |= (0b1L << 5);
        } else {
            castlingInfo |= (0b1L << 2);
        }
    }

    private void castlingRecovery(int[] moveData) {
        if (moveData[6] == 1) {
            switch (moveData[0]) {
                case 7:
                    castlingInfo |= 0b1L;
                    break;
                case 0:
                    castlingInfo |= 0b1L << 1;
                    break;
                case 4:
                    castlingInfo |= 0b1L << 2;
                    break;
                case 63:
                    castlingInfo |= 0b1L << 3;
                    break;
                case 56:
                    castlingInfo |= 0b1L << 4;
                    break;
                case 60:
                    castlingInfo |= 0b1L << 5;
                    break;
            }
        }
    }

    private void promotion(int[] moveData) {
        long square = 0b1L << moveData[1];
        pawns &= ~square;
        switch (moveData[4]) {
            case 5:
                queens |= square;
                break;
            case 2:
                rooks |= square;
                break;
            case 3:
                knights |= square;
                break;
            case 4:
                bishops |= square;
                break;
        }
    }

    private void unPromotion(int[] moveData) {
        long squareTo = 0b1L << moveData[0];
        switch (moveData[4]) {
            case 5:
                queens &= ~squareTo;
                break;
            case 2:
                rooks &= ~squareTo;
                break;
            case 3:
                knights &= ~squareTo;
                break;
            case 4:
                bishops &= ~squareTo;
                break;
        }
        pawns |= squareTo;
        if (moveData[2] != PieceType.NONE.type) {
            unCaptureFigure(moveData[1], PieceType.fromInt(moveData[2]));
        }
    }

    private void captureFigure(long square) {
        if (getType(~square) != KING) {
            rooks &= square;
            knights &= square;
            bishops &= square;
            queens &= square;
            pawns &= square;
            figures &= square;
            if (turn) {
                blacks &= square;
            } else {
                whites &= square;
            }
        }
    }

    private void unCaptureFigure(int index, PieceType type) {
        if (type != NONE) {
            long square = 0b1L << index;
            switch (type) {
                case ROOK:
                    rooks |= square;
                    break;
                case KNIGHT:
                    knights |= square;
                    break;
                case BISHOP:
                    bishops |= square;
                    break;
                case QUEEN:
                    queens |= square;
                    break;
                case PAWN:
                    pawns |= square;
                    break;
            }
            figures |= square;
            if (turn) {
                whites |= square;
            } else {
                blacks |= square;
            }
        }
    }

    private void enPassant() {
        long square = ~(0b1L << enPassant);
        pawns &= square;
        figures &= square;
        if (turn) {
            blacks &= square;
        } else {
            whites &= square;
        }
    }

    private void unEnPassant() {
        long enPassant = 0b1L << this.enPassant;
        pawns |= enPassant;
        figures |= enPassant;
        if (turn) {
            whites |= enPassant;
        } else {
            blacks |= enPassant;
        }
    }

    private List<Move> generatePawnMoves(int index) {
        List<Move> pawnMoves = generatePawnMovesDefault(index);
        pawnMoves.addAll(generatePawnMovesEnPassant(index));
        return pawnMoves;
    }

    private List<Move> generatePawnMovesDefault(int index) {
        List<Move> moves = new ArrayList<>();
        long square = 0b1L;
        if (turn) {
            square <<= index + 8;
            if (index >= A7.value && index <= H7.value) {
                if (index % 8 != H1.value && (blacks & (square << 1)) != 0) {
                    moves.add(new Move(index, index + 9, getType(square << 1).type, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index + 9, getType(square << 1).type, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index + 9, getType(square << 1).type, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index + 9, getType(square << 1).type, enPassant, BISHOP.type, 2));
                }
                if (index % 8 != A1.value && (blacks & (square >>> 1)) != 0) {
                    moves.add(new Move(index, index + 7, getType(square >>> 1).type, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index + 7, getType(square >>> 1).type, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index + 7, getType(square >>> 1).type, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index + 7, getType(square >>> 1).type, enPassant, BISHOP.type, 2));
                }
                if ((figures & square) == 0) {
                    moves.add(new Move(index, index + 8, 0, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index + 8, 0, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index + 8, 0, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index + 8, 0, enPassant, BISHOP.type, 2));
                }
            } else {
                if (index % 8 != H1.value && (blacks & (square << 1)) != 0) {
                    moves.add(new Move(index, index + 9, getType(square << 1).type, enPassant));
                }
                if (index % 8 != A1.value && (blacks & (square >>> 1)) != 0) {
                    moves.add(new Move(index, index + 7, getType(square >>> 1).type, enPassant));
                }
                if ((figures & square) == 0) {
                    moves.add(new Move(index, index + 8, enPassant));
                } else {
                    return moves;
                }
                if (index >= A2.value && index <= H2.value) {
                    if ((figures & (square << 8)) == 0) {
                        moves.add(new Move(index, index + 16, 0, enPassant, index + 16));
                    }
                }
            }
        } else {
            square <<= index - 8;
            if (index >= A2.value && index <= H2.value) {
                if (index % 8 != H1.value && (whites & (square << 1)) != 0) {
                    moves.add(new Move(index, index - 7, getType(square << 1).type, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index - 7, getType(square << 1).type, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index - 7, getType(square << 1).type, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index - 7, getType(square << 1).type, enPassant, BISHOP.type, 2));
                }
                if (index % 8 != A1.value && (whites & (square >>> 1)) != 0) {
                    moves.add(new Move(index, index - 9, getType(square >>> 1).type, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index - 9, getType(square >>> 1).type, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index - 9, getType(square >>> 1).type, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index - 9, getType(square >>> 1).type, enPassant, BISHOP.type, 2));
                }
                if ((figures & square) == 0) {
                    moves.add(new Move(index, index - 8, 0, enPassant, QUEEN.type, 2));
                    moves.add(new Move(index, index - 8, 0, enPassant, ROOK.type, 2));
                    moves.add(new Move(index, index - 8, 0, enPassant, KNIGHT.type, 2));
                    moves.add(new Move(index, index - 8, 0, enPassant, BISHOP.type, 2));
                }
            } else {
                if (index % 8 != H1.value && (whites & (square << 1)) != 0) {
                    moves.add(new Move(index, index - 7, getType(square << 1).type, enPassant));
                }
                if (index % 8 != A1.value && (whites & (square >>> 1)) != 0) {
                    moves.add(new Move(index, index - 9, getType(square >>> 1).type, enPassant));
                }
                if ((figures & square) == 0) {
                    moves.add(new Move(index, index - 8, enPassant));
                } else {
                    return moves;
                }
                if (index >= A7.value && index <= H7.value) {
                    if ((figures & (square >>> 8)) == 0) {
                        moves.add(new Move(index, index - 16, 0, enPassant, index - 16));
                    }
                }
            }
        }
        return moves;
    }

    private List<Move> generatePawnMovesEnPassant(int index) {
        List<Move> moves = new ArrayList<>();
        if (enPassant == 0) {
            return moves;
        }
        int column = index % 8;
        if (column > A1.value && column < H1.value && Math.abs(index - enPassant) == 1
                || column == A1.value && index - enPassant == -1
                || column == H1.value && index - enPassant == 1) {
            if (turn) {
                moves.add(new Move(index, 8 + enPassant, PAWN.type, enPassant, 0, 4));
            } else {
                moves.add(new Move(index, enPassant - 8, PAWN.type, enPassant, 0, 4));
            }
        }
        return moves;
    }

    private List<Move> generateCastlingMoves() {
        List<Move> moves = new ArrayList<>();
        long square = 0b1L;
        int ep = enPassant;
        if (turn) {
            if (((square << 2) & castlingInfo) == 0 || isCheck(true)) {
                return moves;
            }
            // long castling
            if (((square << 1) & castlingInfo) != 0 && ((square << 1) & figures) == 0
                    && ((square << 2) & figures) == 0 && ((square << 3) & figures) == 0 &&
                    (0b1L  & whites & rooks) != 0) {
                if (makeMove(new Move(4, 3, ep))) {
                    turn = !turn;
                    if (makeMove(new Move(3, 2, ep))) {
                        moves.add(new Move(4, 2, Castling.OOOW, ep));
                        unMove(new Move(3, 2, ep));
                        castlingInfo |= 0b1L << 2;
                    }
                    turn = !turn;
                    unMove(new Move(4, 3, ep));
                }
                castlingInfo |= square << 2;
            }
            // short
            if ((square & castlingInfo) != 0
                    && ((square << 5) & figures) == 0 && ((square << 6) & figures) == 0 &&
                    ((0b1L << 7) & whites & rooks) != 0) {
                if (makeMove(new Move(4, 5, ep))) {
                    turn = !turn;
                    if (makeMove(new Move(5, 6, ep))) {
                        moves.add(new Move(4, 6, Castling.OOW, ep));
                        unMove(new Move(5, 6, ep));
                        castlingInfo |= 0b1L << 2;
                    }
                    turn = !turn;
                    unMove(new Move(4, 5, ep));
                }
                castlingInfo |= square << 2;
            }
        } else {
            if (((square << 5) & castlingInfo) == 0 || isCheck(false)) {
                return moves;
            }
            // long castling
            if (((square << 4) & castlingInfo) != 0 && ((square << 57) & figures) == 0
                    && ((square << 58) & figures) == 0 && ((square << 59) & figures) == 0 &&
                    ((0b1L << 56) & blacks & rooks) != 0) {
                if (makeMove(new Move(60, 59, ep))) {
                    turn = !turn;
                    if (makeMove(new Move(59, 58, ep))) {
                        moves.add(new Move(60, 58, Castling.OOOB, ep));
                        unMove(new Move(59, 58, ep));
                        castlingInfo |= 0b1L << 5;
                    }
                    turn = !turn;
                    unMove(new Move(60, 59, ep));
                }
                castlingInfo |= square << 5;
            }
            // short
            if (((square << 3) & castlingInfo) != 0 && ((square << 61) & figures) == 0
                    && ((square << 62) & figures) == 0 &&
                    ((0b1L << 63) & blacks & rooks) != 0) {
                if (makeMove(new Move(60, 61, ep))) {
                    turn = !turn;
                    if (makeMove(new Move(61, 62, ep))) {
                        moves.add(new Move(60, 62, Castling.OOB, ep));
                        unMove(new Move(61, 62, ep));
                        castlingInfo |= 0b1L << 5;
                    }
                    turn = !turn;
                    unMove(new Move(60, 61, ep));
                }
                castlingInfo |= square << 5;
            }
        }
        return moves;
    }

    private List<Move> generateRookMoves(int index) {
        List<Move> moves = new ArrayList<>();
        long ours = turn ? whites : blacks;
        long enemy = turn ? blacks : whites;
        long pseudoLegalMoves = Magic.getPseudoLegalMoves(figures, index, ROOK) & ~ours;
        int castlingChange = 0;
        switch (index) {
            case 7:
                if ((castlingInfo & 0b1L) != 0) {
                    castlingChange = 1;
                }
                break;
            case 0:
                if ((castlingInfo & (0b1L << 1)) != 0) {
                    castlingChange = 1;
                }
                break;
            case 63:
                if ((castlingInfo & (0b1L << 3)) != 0) {
                    castlingChange = 1;
                }
                break;
            case 56:
                if ((castlingInfo & (0b1L << 4)) != 0) {
                    castlingChange = 1;
                }
                break;
        }
        long square = 0b1L;
        for (int j = 0; j < 64; j++) {
            if ((pseudoLegalMoves & square) != 0) {
                Move m;
                if ((enemy & square) != 0) {
                    m = new Move(index, j, getType(square).type, enPassant, castlingChange, 0, 1);
                } else {
                    m = new Move(index, j, 0, enPassant, castlingChange, 0, 0);
                }
                moves.add(m);
            }
            square <<= 1;
        }
        return moves;
    }

    private List<Move> generateKingMoves(int index) {
        List<Move> moves = new ArrayList<>();
        long ours = turn ? whites : blacks;
        long enemy = turn ? blacks : whites;
        int castlingChange = 0;
        if (turn) {
            if ((castlingInfo & (0b1L << 2)) != 0) {
                castlingChange = 1;
            }
        } else {
            if ((castlingInfo & (0b1L << 5)) != 0) {
                castlingChange = 1;
            }
        }
        long pseudoLegalMoves = Moves.KingMoves[index] & ~ours;
        long square = 0b1L;
        for (int j = 0; j < 64; j++) {
            if ((pseudoLegalMoves & square) != 0) {
                Move m;
                if ((enemy & square) != 0) {
                    m = new Move(index, j, getType(square).type, enPassant, castlingChange, 0, 1);
                } else {
                    m = new Move(index, j, 0, enPassant, castlingChange, 0, 0);
                }
                moves.add(m);
            }
            square <<= 1;
        }
        moves.addAll(generateCastlingMoves());
        return moves;
    }

    private PieceType getType(long square) {
        return  (rooks & square) != 0 ? ROOK :
                (knights & square) != 0 ? KNIGHT :
                (bishops & square) != 0 ? BISHOP :
                (queens & square) != 0 ? QUEEN :
                (kings & square) != 0 ? KING :
                (pawns & square) != 0 ? PAWN :
                NONE;
    }

    public boolean isCheck(boolean turn) {
        int kingPos;
        long king;
        long attackers;
        long pawnAttacks;
        long fig;
        if (turn) {
            kingPos = whiteKing;
            attackers = blacks;
            pawnAttacks = Moves.PawnAttacksWhite[kingPos];
        } else {
            kingPos = blackKing;
            attackers = whites;
            pawnAttacks = Moves.PawnAttacksBlack[kingPos];
        }
        king = 0b1L << kingPos;
        fig = figures & ~king;
        if (((rooks & attackers) &
                Magic.getPseudoLegalMoves(fig, kingPos, ROOK)) != 0) {
            return true;
        }
        if (((bishops & attackers) &
                Magic.getPseudoLegalMoves(fig, kingPos, BISHOP)) != 0) {
            return true;
        }
        if (((queens & attackers) &
                Magic.getPseudoLegalMoves(fig, kingPos, QUEEN)) != 0) {
            return true;
        }
        if ((((knights & attackers)) & Moves.KnightMoves[kingPos]) != 0) {
            return true;
        }
        if ((((kings & attackers)) & Moves.KingMoves[kingPos]) != 0) {
            return true;
        }
        if ((((pawns & attackers)) & pawnAttacks) != 0) {
            return true;
        }
        return false;
    }

    /**
     * @return FEN representation of the board
     */
    @Override
    public String toString() {
        // Position info
        int wasEmptyCells = 0;
        int wasEmptyCellsTotal = 0;
        long currentSquare = 0b1L << 56;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            if (i % 8 == 0 && i != 0) {
                if (wasEmptyCells != 0) {
                    sb.append(Integer.valueOf(wasEmptyCells).toString());
                    wasEmptyCells = 0;
                    wasEmptyCellsTotal--;
                }
                sb.append('/');
                wasEmptyCellsTotal--;
                currentSquare = 0b1L << (56 - i);
            }
            if ((figures & currentSquare) == 0) {
                wasEmptyCells++;
                wasEmptyCellsTotal++;
            } else {
                if (wasEmptyCells != 0) {
                    sb.append(Integer.valueOf(wasEmptyCells).toString());
                    wasEmptyCells = 0;
                    wasEmptyCellsTotal--;
                }
            }
            if ((rooks & currentSquare) != 0) {
                sb.append(enums.PieceType.ROOK.toString());
            }
            if ((knights & currentSquare) != 0) {
                sb.append(enums.PieceType.KNIGHT.toString());
            }
            if ((bishops & currentSquare) != 0) {
                sb.append(enums.PieceType.BISHOP.toString());
            }
            if ((queens & currentSquare) != 0) {
                sb.append(enums.PieceType.QUEEN.toString());
            }
            if ((kings & currentSquare) != 0) {
                sb.append(KING.toString());
            }
            if ((pawns & currentSquare) != 0) {
                sb.append(enums.PieceType.PAWN.toString());
            }
            if ((figures & currentSquare) != 0 && (whites & currentSquare) == 0) {
                sb.replace(i - wasEmptyCellsTotal, i + 1 - wasEmptyCellsTotal,
                        sb.substring(i - wasEmptyCellsTotal, i + 1 - wasEmptyCellsTotal).toLowerCase());
            }
            currentSquare <<= 1;
        }
        if (wasEmptyCells != 0) {
            sb.append(Integer.valueOf(wasEmptyCells).toString());
        }
        // Turn info
        sb.append(turn ? " w " : " b ");
        // Castling info
        boolean castlingAvailable = false;
        if ((castlingInfo & 0b000101) != 0) {
            sb.append("K");
            castlingAvailable = true;
        }
        if ((castlingInfo & 0b000110) != 0) {
            sb.append("Q");
            castlingAvailable = true;
        }
        if ((castlingInfo & 0b101000) != 0) {
            sb.append("k");
            castlingAvailable = true;
        }
        if ((castlingInfo & 0b110000) != 0) {
            sb.append("q");
            castlingAvailable = true;
        }
        if (!castlingAvailable) {
            sb.append("-");
        }
        // En passant info
        sb.append(enPassant == 0 ? " -" : (" " + enPassant));
        return sb.toString();
    }

    /**
     * Method for giving board position to the
     * @return
     */
    public String[][] getBoard() {
        long currentSquare = 0b1L;
        String[][] buffer = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((rooks & currentSquare) != 0) {
                    buffer[i][j] = ROOK.toString();
                }
                if ((knights & currentSquare) != 0) {
                    buffer[i][j] = KNIGHT.toString();
                }
                if ((bishops & currentSquare) != 0) {
                    buffer[i][j] = BISHOP.toString();
                }
                if ((queens & currentSquare) != 0) {
                    buffer[i][j] = QUEEN.toString();
                }
                if ((kings & currentSquare) != 0) {
                    buffer[i][j] = KING.toString();
                }
                if ((pawns & currentSquare) != 0) {
                    buffer[i][j] = PAWN.toString();
                }
                if ((figures & currentSquare) == 0) {
                    buffer[i][j] = "0";
                }
                if ((blacks & currentSquare) != 0) {
                    buffer[i][j] = buffer[i][j].toLowerCase();
                }
                currentSquare <<= 1;
            }
        }
        return buffer;
    }

    public List<Move> getSquareMoves(int x, int y, List<Move> moveList) {
        List<Move> moves = new ArrayList<>();
        int index = x + y * 8;
        for (Move m : moveList) {
            int[] data = m.get();
            if (data[0] == index) {
                moves.add(m);
            }
        }
        return moves;
    }

    /**
     * First 768 - position info
     * For each square 12 values - 1 for each type of piece
     * Another inputs is:
     * white to move
     * black to move
     * check for whites
     * check for blacks
     * points difference div 3
     * checkmate for whites
     * checkmate for blacks
     * @return Inputs for the neural network
     */
    public Pair<float[], List<Integer>> getInputs() {
        float[] inputs = new float[775];
        List<Integer> list = new ArrayList<>(64);
        long square = 0b1L;
        for (int i = 0; i < 768; i += 12) {
            if ((figures & square) != 0) {
                PieceType type = getType(square);
                boolean colour = (whites & square) != 0;
                if (colour) {
                    inputs[i + type.type - 1] = 1.0f;
                    list.add(i + type.type - 1);
                } else {
                    inputs[i + type.type + 5] = 1.0f;
                    list.add(i + type.type + 5);
                }
            }
            square <<= 1;
        }
        inputs[768] = turn ? 1.0f : 0.0f;
        inputs[769] = !turn ? 1.0f : 0.0f;
        inputs[770] = isCheck(true) ? 1.0f : 0.0f;
        inputs[771] = isCheck(false) ? 1.0f : 0.0f;
        inputs[772] = (float) (getPoints().getKey() - getPoints().getValue()) / 3.0f;
        inputs[773] = isCheck(true) && gameOver ? 1.0f : 0.0f; // mate to white (black win)
        inputs[774] = isCheck(false) && gameOver ? 1.0f : 0.0f;
        list.add(768);
        list.add(769);
        list.add(770);
        list.add(771);
        list.add(772);
        list.add(773);
        list.add(774);
        return new Pair<>(inputs, list);
    }

    /**
     * pawn - 1
     * knight and bishop - 3
     * rook - 5
     * queen - 9
     * @return pair (white points, black points)
     */
    public Pair<Integer, Integer> getPoints() {
        int white = 0;
        int black = 0;
        white += Long.bitCount(pawns & whites);
        white += Long.bitCount(rooks & whites) * 5;
        white += Long.bitCount(knights & whites) * 3;
        white += Long.bitCount(bishops & whites) * 3;
        white += Long.bitCount(queens & whites) * 9;
        black += Long.bitCount(pawns & blacks);
        black += Long.bitCount(rooks & blacks) * 5;
        black += Long.bitCount(knights & blacks) * 3;
        black += Long.bitCount(bishops & blacks) * 3;
        black += Long.bitCount(queens & blacks) * 9;
        return new Pair<>(white, black);
    }

    public boolean getTurn() {
        return turn;
    }

    public boolean isGameOver() {
        getAllMoves();
        return gameOver;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    /**
     * Test for checking logic correctness
     * @param deepLevel - level of the tree to count
     * @return nodes count
     */
    public long perft(int deepLevel) {
        perfTest p = new perfTest();
        long startTime = System.nanoTime();
        p.perft(deepLevel);
        /*
        long stopTime = System.nanoTime() - startTime;
        System.out.println("Nodes:         " + p.nodes);
        System.out.println("Captures:      " + p.captures);
        System.out.println("E.P.:          " + p.ep);
        System.out.println("Castles:       " + p.castles);
        System.out.println("Promotions:    " + p.promotions);
        System.out.println("Checks :       " + p.checks);
        System.out.println("Checkmates:    " + p.checkMates);
        System.out.println("Perft done in: " + (stopTime / 1000) + "us");
        System.out.println("Speed was:     " + (p.count * 1000000000 / stopTime));
        */
        return p.nodes;
    }

    private class perfTest {
        private perfTest() {
            nodes = 0;
            captures = 0;
            ep = 0;
            castles = 0;
            promotions = 0;
            checks = 0;
            checkMates = 0;
            count = 0;
        }

        private long nodes;
        private long captures;
        private long ep;
        private long castles;
        private long promotions;
        private long checks;
        private long checkMates;
        private long count;

        private void perft(int deepLevel) {
            List<Move> moves = getAllMoves();
            if (deepLevel == 1) {
                for (Move m : moves) {
                    count++;
                    int[] moveData = m.get();
                    nodes += 1;
                    switch (moveData[5]) {
                        case 1:
                            captures++;
                            break;
                        case 2:
                            promotions++;
                            if (moveData[2] != 0) {
                                captures++;
                            }
                            break;
                        case 3:
                            castles++;
                            break;
                        case 4:
                            ep++;
                            captures++;
                    }
                }
                return;
            }
            for (Move m : moves) {
                count++;
                makeMove(m);
                perft(deepLevel - 1);
                unMove(m);
            }
        }
    }
}

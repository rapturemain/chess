package logic;

public class Move {
    private static final int fromMask   = 0b11111100000000000000000000000000;
    private static final int toMask     = 0b00000011111100000000000000000000;
    private static final int pieceMask  = 0b00000000000011110000000000000000;
    private static final int epMask     = 0b00000000000000001111110000000000;
    private static final int epPosMask  = 0b00000000000000000000000111111000;
    private static final int castlingM  = 0b00000000000000000000001000000000;
    private static final int typeMask   = 0b00000000000000000000000000000111;
    private static final int fromShift  = 26; // 1
    private static final int toShift    = 20; // 2
    private static final int pieceShift = 16; // 3
    private static final int epShift    = 10; // 4
    private static final int castlingS  = 9;  // 7
    private static final int epPosShift = 3;  // 5
                                              // flag - 6

    // flags: 0 - normal move, 1 - capture, 2 - promoting, 3 - castling, 4 - en passant

    public Move(int from, int to, int enPassant) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 10;
        move += enPassant;
        move <<= 10;
    }

    public Move(int from, int to, int pieceType, int enPassant) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 4;
        move += pieceType;
        move <<= 6;
        move += enPassant;
        move <<= 10;
        move += 1;
    }

    public Move(int from, int to, enums.Castling castling, int enPassant) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 4;
        move += castling.where;
        move <<= 6;
        move += enPassant;
        move <<= 10;
        move += 3;
    }

    public Move(int from, int to, int pieceType, int enPassant, int newEnPassant) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 4;
        move += 0;
        move <<= 6;
        move += enPassant;
        move <<= 7;
        move += newEnPassant;
        move <<= 3;
        move += 0;
    }

    public Move(int from, int to, int pieceType, int enPassant, int enPassantMove, int type) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 4;
        move += pieceType;
        move <<= 6;
        move += enPassant;
        move <<= 7;
        move += enPassantMove;
        move <<= 3;
        move += type;
    }

    public Move(int from, int to, int pieceType, int enPassant, int castlingChange, int enPassantMove, int type) {
        move = from;
        move <<= 6;
        move += to;
        move <<= 4;
        move += pieceType;
        move <<= 6;
        move += enPassant;
        move <<= 1;
        move += castlingChange;
        move <<= 6;
        move += enPassantMove;
        move <<= 3;
        move += type;
    }

    private int move;

    public int[] get() {
        int[] data = new int[7];
        data[0] = (move & fromMask) >>> fromShift;
        data[1] = (move & toMask) >>> toShift;
        data[2] = (move & pieceMask) >>> pieceShift;
        data[3] = (move & epMask) >>> epShift;
        data[4] = (move & epPosMask) >>> epPosShift;
        data[5] = (move & typeMask);
        data[6] = (move & castlingM) >>> castlingS;
        return data;
    }

    public int getX() {
        return ((move & toMask) >>> toShift) % 8;
    }

    public int getY() {
        return ((move & toMask) >>> toShift) / 8;
    }

    public int getFromX() {
        return ((move & fromMask) >>> fromShift) % 8;
    }

    public int getFromY() {
        return ((move & fromMask) >>> fromShift) / 8;
    }

    public int getPromotionPiece() {
        if ((move & typeMask) == 2) {
            return (move & epPosMask) >>> epPosShift;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            return ((Move) obj).move == this.move;
        }
    }
}

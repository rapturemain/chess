package logic;

public class enums {
    enum PieceType {
        NONE(0),
        PAWN(1),
        ROOK(2),
        KNIGHT(3),
        BISHOP(4),
        QUEEN(5),
        KING(6);

        PieceType(int type) {
            this.type = type;
        }

        public final int type;

        public static PieceType fromInt(int index) {
            switch (index) {
                case 0:
                    return NONE;
                case 1:
                    return PAWN;
                case 2:
                    return ROOK;
                case 3:
                    return KNIGHT;
                case 4:
                    return BISHOP;
                case 5:
                    return QUEEN;
                case 6:
                    return KING;
            }
            return NONE;
        }

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return " ";
                case PAWN:
                    return "P";
                case ROOK:
                    return "R";
                case KNIGHT:
                    return "N";
                case BISHOP:
                    return "B";
                case QUEEN:
                    return "Q";
                case KING:
                    return "K";
            }
            throw new IllegalArgumentException ("Invalid piece type, toString() failed");
        }
    }

    enum Square {
        A1(0 ), B1(1 ), C1(2 ), D1(3 ), E1(4 ), F1(5 ), G1(6 ), H1(7 ),
        A2(8 ), B2(9 ), C2(10), D2(11), E2(12), F2(13), G2(14), H2(15),
        A3(16), B3(17), C3(18), D3(19), E3(20), F3(21), G3(22), H3(23),
        A4(24), B4(25), C4(26), D4(27), E4(28), F4(29), G4(30), H4(31),
        A5(32), B5(33), C5(34), D5(35), E5(36), F5(37), G5(38), H5(39),
        A6(40), B6(41), C6(42), D6(43), E6(44), F6(45), G6(46), H6(47),
        A7(48), B7(49), C7(50), D7(51), E7(52), F7(53), G7(54), H7(55),
        A8(56), B8(57), C8(58), D8(59), E8(60), F8(61), G8(62), H8(63);

        Square(int value) {
            this.value = value;
        }

        public final int value;

        public int index() {
            return value;
        }

        @Override
        public String toString() {
            char[] buffer = "abcdefgh".toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append(buffer[value % 8]);
            sb.append(value / 8 + 1);
            return sb.toString();
        }
    }

    enum Direction {
        NORTH(0, 1), NORTHEATS(1, 1), EAST(1, 0), SOUTHEAST(1, -1),
        SOUTH(0, -1), SOUTHWEST(-1, -1), WEST(-1, 0), NORTHWEST(-1, 1);

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public final int x;
        public final int y;
    }

    enum Castling {
        NONE(0), OOW(1), OOOW(2), OOB(3), OOOB(4);

        Castling(int where) {
            this.where = where;
        }

        public final int where;
    }
}

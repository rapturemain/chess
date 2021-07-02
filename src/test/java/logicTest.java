import logic.Board;
import org.junit.Test;

import static org.junit.Assert.*;

public class logicTest {
    /**
     * Checks move generator and chess logic for correctness
     * Uses Perft (PERFormance Test, move path enumeration)
     * Results and positions provided by
     * https://www.chessprogramming.org/Perft_Results
     *
     * P.S. Test will run around half-two minutes, so be patient.
     */
    @Test
    public void perft() {
        Board board = new Board();
        //<editor-fold desc="Default Start position">
        assertEquals(board.perft(1), 20L);
        assertEquals(board.perft(2), 400L);
        assertEquals(board.perft(3), 8902L);
        assertEquals(board.perft(4), 197281L);
        assertEquals(board.perft(5), 4865609L);
        assertEquals(board.perft(6), 119060324L);
        //</editor-fold>

        //<editor-fold desc="Kiwipete position">
        board.initFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        assertEquals(board.perft(1), 48L);
        assertEquals(board.perft(2), 2039L);
        assertEquals(board.perft(3), 97862L);
        assertEquals(board.perft(4), 4085603L);
        assertEquals(board.perft(5), 193690690L);
        //</editor-fold>

        //<editor-fold desc="Position 3">
        board.initFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        assertEquals(board.perft(1), 14L);
        assertEquals(board.perft(2), 191L);
        assertEquals(board.perft(3), 2812L);
        assertEquals(board.perft(4), 43238L);
        assertEquals(board.perft(5), 674624L);
        assertEquals(board.perft(6), 11030083L);
        //</editor-fold>

        //<editor-fold desc="Position 4">
        board.initFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        assertEquals(board.perft(1), 6L);
        assertEquals(board.perft(2), 264L);
        assertEquals(board.perft(3), 9467L);
        assertEquals(board.perft(4), 422333L);
        assertEquals(board.perft(5), 15833292L);
        //</editor-fold>

        //<editor-fold desc="Position 5">
        board.initFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        assertEquals(board.perft(1), 44L);
        assertEquals(board.perft(2), 1486L);
        assertEquals(board.perft(3), 62379L);
        assertEquals(board.perft(4), 2103487L);
        assertEquals(board.perft(5), 89941194L);
        //</editor-fold>
    }

    @Test
    public void toStringTest() {
        Board board = new Board();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -", board.toString());

        board.initFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        assertEquals("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", board.toString());

        board.initFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        assertEquals("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", board.toString());

        board.initFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -");
        assertEquals("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -", board.toString());


        board.initFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        assertEquals("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ -", board.toString());
    }
}

package logic;

import java.util.ArrayList;
import java.util.List;

public class Chess {

    private final String BOT_INDEX = "2";

    public Chess(String FEN) {
        board = new Board(FEN);
        moves = board.getAllMoves();
        bot = new Bot(board, 5);
        bot.updateNN(BOT_INDEX);
        position = board.getBoard();
        turn = board.getTurn();
    }

    private Board board;
    private Bot bot;
    private List<Move> moves;
    private List<Move> history = new ArrayList<>(128);
    private int lastMoveIndex = -1;

    private boolean turn;
    private String[][] position;
    private boolean botThinking = false;
    private boolean gameOver = false;

    public void makeMove(Move move) {
        board.makeMove(move);
        afterMove();
        addMove(move);
    }

    public void makeBotMove() {
        if (!botThinking) {
            botThinking = true;
            bot.makeMove();
            afterMove();
            addMove(bot.getLastMove());
            botThinking = false;
        }
    }

    public void takeMoveBack() {
        if (history.size() == 0 || botThinking) {
            return;
        }
        board.unMove(history.get(lastMoveIndex));
        afterMove();
        history.remove(lastMoveIndex);
        lastMoveIndex--;
    }

    public List<Move> getMoves(int x, int y) {
        if (botThinking) {
            return new ArrayList<>();
        } else {
            return board.getSquareMoves(x, y, moves);
        }
    }

    public String[][] getBoard() {
        return position;
    }

    public boolean getTurn() {
        return turn;
    }

    public boolean isBotRunning() {
        return botThinking;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Move getLastMove() {
        return history.get(lastMoveIndex);
    }

    private void addMove(Move move) {
        history.add(move);
        lastMoveIndex++;
    }

    private void afterMove() {
        moves = board.getAllMoves();
        position = board.getBoard();
        turn = board.getTurn();
        gameOver = board.isGameOver();
    }
}

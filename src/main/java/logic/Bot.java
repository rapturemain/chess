package logic;

import javafx.util.Pair;
import neuralNetwork.NeuralNetwork;

import java.util.List;

public class Bot {

    public Bot(double limitRecursiveLevel) {
        this.score = 0;
        this.game = null;
        this.limitRecursiveLevel = limitRecursiveLevel;
        this.nn = new NeuralNetwork(new int[] {775, 140, 30, 1});
        lastMoves[0] = new Move(0, 0, 0);
        lastMoves[1] = new Move(0, 0, 0);
    }

    public Bot(Board game, double limitRecursiveLevel) {
        this.score = 0;
        this.game = game;
        this.limitRecursiveLevel = limitRecursiveLevel;
        this.nn = new NeuralNetwork(new int[] {775, 140, 30, 1});
        lastMoves[0] = new Move(0, 0, 0);
        lastMoves[1] = new Move(0, 0, 0);

    }

    private Board game;
    private double limitRecursiveLevel;
    private NeuralNetwork nn;
    private int score;
    private long cloneTime;
    private Move[] lastMoves = new Move[2];
    private int breakCount;

    private float getEvaluation(Board game) {
        Pair<float[], List<Integer>> buffer = game.getInputs();
        return nn.getOutputs(buffer.getKey(), buffer.getValue())[0];
    }

    private float minMax(Board game, double deepLevel, double alpha, double beta) {
        boolean turn = game.getTurn();
        float minMax = turn ? -Float.MAX_VALUE : Float.MAX_VALUE;
        List<Move> movesList = game.getAllMoves();
        if (deepLevel == 1 && game.isGameOver() && game.isCheckMate()) {
            return turn ? -Float.MAX_VALUE : Float.MAX_VALUE;
        }
        if (deepLevel >= limitRecursiveLevel || game.isGameOver()) {
            return getEvaluation(game);
        }
        Move bestMove = null;
        for (Move m : movesList) {
            this.cloneTime += 1;
            if (deepLevel < 0.1 && movesList.size() > 1) {
                if (m.equals(lastMoves[1])) {
                    continue;
                }
            }
            game.makeMove(m);
            float buffer = minMax(game, deepLevel + 1, alpha, beta);
            game.unMove(m);
            if (buffer > minMax && turn || buffer < minMax && !turn) {
                minMax = buffer;
                bestMove = m;
            }
            if (game.getTurn()) {
                alpha = Math.max(alpha, buffer);
            }
            else {
                beta = Math.min(beta, buffer);
            }
            if (beta < alpha) {
                break;
            }
        }
        if (bestMove == null) {
            return getEvaluation(game);
        }
        if (deepLevel < 0.1) {
            this.game.makeMove(bestMove);
            lastMoves[1] = lastMoves[0];
            lastMoves[0] = bestMove;
        }
        return minMax;
    }

    public void changeGame(Board game) {
        this.game = null;
        this.game = game;
    }

    public void makeMove() {
      //  long startTime = System.nanoTime();
      //  cloneTime = 1;
      //  breakCount = 0;
        minMax(game, 0, -Float.MAX_VALUE, Float.MAX_VALUE);
      //  long stopTime = System.nanoTime() - startTime;
     //   System.out.println(cloneTime);
      //  System.out.println(cloneTime * 1000000000 / stopTime);
    //    System.out.println(stopTime / 1000);
     //   System.out.println(breakCount);
    }

    public Move getLastMove() {
        return lastMoves[0];
    }

    public void mutate(float mutationRate) {
        nn.mutate(mutationRate);
    }

    public NeuralNetwork getNN() {
        return nn;
    }

    public boolean updateNN(String path) {
        return nn.loadData(path);
    }

    @Override
    public Bot clone() {
        Bot copyBot = new Bot(limitRecursiveLevel);
        copyBot.nn = nn.clone();
        return copyBot;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLastScore() {
        return score;
    }

    public double getEv() {
        return getEvaluation(game);
    }
}

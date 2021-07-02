package GUI;

import logic.Chess;
import logic.Move;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game implements Window {
    public Game(Stage primaryStage, Group root) {
        this.primaryStage = primaryStage;
        this.root = root;
        loadImages();

        player1 = (String) ((ComboBox) root.getChildren().get(Start.groupIndex.PLAYER1.getIndex())).getValue();
        player2 = (String) ((ComboBox) root.getChildren().get(Start.groupIndex.PLAYER2.getIndex())).getValue();
        game = new Chess
                (((TextField) root.getChildren().get(Start.groupIndex.FEN.getIndex())).getCharacters().toString());

    }

    private Stage primaryStage;
    private Group root;

    private final Color borderColor = new Color(0.64, 0.64, 0.64, 1);
    private final Color whiteColor = new Color(255 / 255.0, 222 / 255.0, 173 / 255.0, 1);
    private final Color blackColor = new Color(205 / 255.0, 133 / 255.0, 63 / 255.0, 1);
    private final Color move = new Color(0.58, 0.81, 0.1, 1.0);
    private final Color lastMoveColor = new Color(15 / 255.0, 153 / 255.0, 207 / 255.0, 1.0);

    private double width;
    private double height;
    private double cellSize;
    private double frameSizeSide;
    private double frameSizeUp;

    private Chess game;
    private String player1;
    private String player2;
    private Move lastMove = null;
    private GUI.Button selectedFigure = null;

    private List<GUI.Button> buttons = new ArrayList<>(64);
    private boolean movesDraw;
    private Image[] figureImages;

    @Override
    public void init(final List<Window> window) {
        root.getChildren().clear();
        root.getChildren().add(new Canvas(1920, 1080));
        root.getChildren().add(new Canvas(1920, 1080));
        root.getChildren().add(new Canvas(1920, 1080));
        root.getChildren().add(new Canvas(1920, 1080));

        Text gameOver = new Text("G A M E    O V E R ");
        gameOver.setOpacity(0.0);
        gameOver.setFill(new Color(0.8, 0.3, 0.1, 1.0));
        root.getChildren().add(gameOver);

        Button bigButton = new Button();
        bigButton.setOpacity(0.0);
        bigButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reset();
            }
        });
        root.getChildren().add(bigButton);

        Button unMoveButton = new Button();
        unMoveButton.setOpacity(0.4);
        unMoveButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!game.isBotRunning()) {
                    root.getChildren().get(groupIndex.GAMEOVER.getIndex()).setOpacity(0.0);
                    if ((player1.equals("Computer") ^ player2.equals("Computer"))) {
                        game.takeMoveBack();
                        game.takeMoveBack();
                        lastMove = null;
                        selectedFigure = null;
                        reset();
                    } else {
                        if (!(player1.equals("Computer") && player2.equals("Computer"))) {
                            game.takeMoveBack();
                            lastMove = null;
                            selectedFigure = null;
                            reset();
                        }
                    }
                }
            }
        });
        root.getChildren().add(unMoveButton);

        Button returnButton = new Button();
        returnButton.setOpacity(0.4);
        returnButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!game.isBotRunning()) {
                    window.set(0, new Start(primaryStage, root));
                    window.get(0).init(window);
                }
            }
        });
        root.getChildren().add(returnButton);

        reset();
    }

    @Override
    public void update() {
        width = primaryStage.getWidth() - 20;
        height = primaryStage.getHeight() - 40;
        cellSize = Math.min(width, height) * 0.75 / 8;
        frameSizeSide = (width - cellSize * 8) / 2;
        frameSizeUp = (height - cellSize * 8) / 2;

        drawBackground();
        drawPieces();
        drawLastMove();
        drawMoves();
        drawBigButton();
        drawUnMoveButton();
        drawReturnButton();
        drawGameOver();
        drawButtons();
    }

    private void drawBackground() {
        final Canvas canvas = (Canvas) root.getChildren().get(groupIndex.BACKGROUND.getIndex());
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width + 40, height + 40);

        gc.setFill(borderColor);
        gc.fillRect(0, 0, width + 40, height + 40);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(whiteColor);
                    gc.fillRect(frameSizeSide + j * cellSize, frameSizeUp + i * cellSize,
                            cellSize, cellSize);
                } else {
                    gc.setFill(blackColor);
                    gc.fillRect(frameSizeSide + j * cellSize, frameSizeUp + i * cellSize,
                            cellSize, cellSize);
                }
            }
        }
    }

    private void drawPieces() {
        final Canvas canvas = (Canvas) root.getChildren().get(groupIndex.PIECES.getIndex());
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width + 40, height + 40);

        String[][] board = this.game.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String figure = board[i][j];
                if (!figure.equals("0")) {
                    if (figure.equals("P")) {
                        gc.drawImage(figureImages[0], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("R")) {
                        gc.drawImage(figureImages[1], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("N")) {
                        gc.drawImage(figureImages[2], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("B")) {
                        gc.drawImage(figureImages[3], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("Q")) {
                        gc.drawImage(figureImages[4], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("K")) {
                        gc.drawImage(figureImages[5], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                    }
                    if (figure.equals("p")) {
                        gc.drawImage(figureImages[6], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("r")) {
                        gc.drawImage(figureImages[7], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("n")) {
                        gc.drawImage(figureImages[8], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("b")) {
                        gc.drawImage(figureImages[9], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("q")) {
                        gc.drawImage(figureImages[10], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                        continue;
                    }
                    if (figure.equals("k")) {
                        gc.drawImage(figureImages[11], j * cellSize + frameSizeSide, (7 - i) * cellSize + frameSizeUp,
                                cellSize, cellSize);
                    }
                }
            }
        }
    }

    private void drawLastMove() {
        final Canvas canvas = (Canvas) root.getChildren().get(groupIndex.LAST_MOVE.getIndex());
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width + 40, height + 40);
        gc.setFill(lastMoveColor);
        if (lastMove != null) {
            gc.setGlobalAlpha(0.6);
            gc.fillOval(frameSizeSide + lastMove.getFromX() * cellSize + cellSize * 0.35
                    , frameSizeUp + (7 - lastMove.getFromY()) * cellSize + cellSize * 0.35, cellSize * 0.3, cellSize * 0.3);
            gc.fillOval(frameSizeSide + lastMove.getX() * cellSize + cellSize * 0.35
                    , frameSizeUp + (7 - lastMove.getY()) * cellSize + cellSize * 0.35, cellSize * 0.3, cellSize * 0.3);
        }
        if (selectedFigure != null) {
            gc.setGlobalAlpha(0.4);
            gc.fillRoundRect(frameSizeSide + selectedFigure.getX() * cellSize + cellSize * 0.05
                    , frameSizeUp + (7 - selectedFigure.getY()) * cellSize + cellSize * 0.05,
                    cellSize * 0.9, cellSize * 0.9, cellSize * 0.35, cellSize * 0.35);
        }
    }

    private void drawMoves() {
        final Canvas canvas = (Canvas) root.getChildren().get(groupIndex.MOVES.getIndex());
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setGlobalAlpha(0.95);
        gc.clearRect(0, 0, width + 40, height + 40);

        if (!movesDraw) {
            return;
        }

        for (GUI.Button btn : buttons) {
            int j = btn.getX();
            int i = btn.getY();
            gc.setFill(move);
            gc.fillOval(frameSizeSide + j * cellSize + cellSize * 0.35
                    , frameSizeUp + (7 - i) * cellSize + cellSize * 0.35, cellSize * 0.3, cellSize * 0.3);
        }
    }

    private void drawBigButton() {
        Button btn = (Button) root.getChildren().get(groupIndex.BIG_BUTTON.getIndex());
        btn.setPrefSize(width + 40, height + 40);
    }

    private void drawUnMoveButton() {
        Button button = (Button) root.getChildren().get(groupIndex.UNMOVE_BUTTON.getIndex());
        button.setLayoutX(width * 0.005);
        button.setLayoutY(height * 0.005);
        button.setPrefSize(cellSize * 0.8, cellSize * 0.8);
    }

    private void drawReturnButton() {
        Button button = (Button) root.getChildren().get(groupIndex.TO_START_BUTTON.getIndex());
        button.setLayoutX(width * 0.005 + cellSize * 0.9);
        button.setLayoutY(height * 0.005);
        button.setPrefSize(cellSize * 0.8, cellSize * 0.8);
    }

    private void drawGameOver() {
        Text text = (Text) root.getChildren().get(groupIndex.GAMEOVER.getIndex());
        text.setLayoutY(frameSizeUp * 0.6);
        text.setFont(new Font(Math.min(frameSizeUp * 0.4, width * 0.1)));
        text.setWrappingWidth(width);
        text.setTextAlignment(TextAlignment.CENTER);
    }

    private void drawButtons() {
        root.getChildren().remove(groupIndex.BUTTONS.getIndex(), root.getChildren().size());
        for (GUI.Button btn : buttons) {
            Button button = btn.getButton();
            button.setPrefSize(cellSize, cellSize);
            button.setLayoutX(frameSizeSide + btn.getX() * cellSize);
            button.setLayoutY(frameSizeUp + (7 - btn.getY()) * cellSize);
            root.getChildren().add(button);
        }
    }

    private void createButtonsSelect() {
        buttons.clear();
        String[][] board = this.game.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!board[i][j].equals("0")) {
                    Button btn = new Button();
                    btn.setOpacity(0.0);
                    btn.addEventHandler(ActionEvent.ACTION, selectEvent(j, i));
                    buttons.add(new GUI.Button(j, i, btn));
                }
            }
        }
    }

    private EventHandler<ActionEvent> selectEvent(final int x, final int y) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastMove = null;
                selectedFigure = new GUI.Button(x, y, new Button());
                createButtonsMove(x, y);
            }
        };
    }

    private void createButtonsMove(int x, int y) {
        buttons.clear();
        List<Move> moves = game.getMoves(x, y);
        if (moves.size() == 0) {
            reset();
            return;
        }
        for (final Move m : moves) {
            if (m.getPromotionPiece() == 5 || m.getPromotionPiece() == 0) {
                Button btn = new Button();
                btn.setOpacity(0.0);
                btn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        game.makeMove(m);
                        reset();
                    }
                });
                buttons.add(new GUI.Button(m.getX(), m.getY(), btn));
            }
        }
        movesDraw = true;
        update();
    }

    private void reset() {
        if (!game.isGameOver()) {
            if ((game.getTurn() && player1.equals("Computer")
                    || !game.getTurn() && player2.equals("Computer")) && !game.isBotRunning()) {
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        game.makeBotMove();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lastMove = game.getLastMove();
                                reset();

                            }
                        });
                    }
                };
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            } else {
                createButtonsSelect();
            }
        } else {
            root.getChildren().get(groupIndex.GAMEOVER.getIndex()).setOpacity(1.0);
        }
        selectedFigure = null;
        movesDraw = false;
        update();
    }

    private void loadImages() {
        figureImages = new Image[12];
        try {
            for (int i = 0; i < 12; i++) {
                figureImages[i] = new Image(Game.class.getResourceAsStream("/" + Integer.toString(i) + ".png"));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    enum groupIndex {
        BACKGROUND(0),
        PIECES(1),
        LAST_MOVE(2),
        MOVES(3),
        GAMEOVER(4),
        BIG_BUTTON(5),
        UNMOVE_BUTTON(6),
        TO_START_BUTTON(7),
        BUTTONS(8);

        private int index;

        groupIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}

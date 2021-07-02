package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class Start implements Window {

    public Start(final Stage primaryStage, final Group root) {
        this.primaryStage = primaryStage;
        this.root = root;
    }

    private Stage primaryStage;
    private Group root;


    @Override
    public void init(final List<Window> window) {
        root.getChildren().clear();
        ObservableList<String> optionsPlayerSelect =
                FXCollections.observableArrayList(
                        "Player",
                        "Computer"
                );
        ComboBox<String> player1 = new ComboBox<>(optionsPlayerSelect);
        ComboBox<String> player2 = new ComboBox<>(optionsPlayerSelect);

        player1.setValue("Player");
        player1.setPrefWidth(100);
        player1.setLayoutX(primaryStage.getWidth() / 4 - 65);
        player1.setLayoutY(primaryStage.getHeight() / 4);

        player2.setValue("Player");
        player2.setPrefWidth(100);
        player2.setLayoutX(primaryStage.getWidth() / 4 * 3 - 55);
        player2.setLayoutY(primaryStage.getHeight() / 4);

        TextField fenField = new TextField();
        fenField.setPromptText("Enter FEN here (if nothing, then default Start position will be used)");
        fenField.setPrefSize(primaryStage.getWidth()  - (primaryStage.getWidth() / 3) > 550 ? 483 : primaryStage.getWidth() - 2 * (-112.5 + primaryStage.getWidth() / 6 * 2) - 17, 20);
        fenField.setLayoutX(primaryStage.getWidth() - (primaryStage.getWidth() / 3) > 550 ? primaryStage.getWidth() / 2 - 250 : -112.5 + primaryStage.getWidth() / 6 * 2);
        fenField.setLayoutY(primaryStage.getHeight() / 3);

        Button startButton = new Button();
        startButton.setPrefSize(200, 150);
        startButton.setLayoutY(primaryStage.getHeight() / 2);
        startButton.setLayoutX(primaryStage.getWidth() / 2 - 110);
        startButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.set(0, new Game(primaryStage, root));
                window.get(0).init(window);
            }
        });

        root.getChildren().add(player1);
        root.getChildren().add(player2);
        root.getChildren().add(fenField);
        root.getChildren().add(startButton);
    }

    @Override
    public void update() {
        ComboBox player1 = (ComboBox) root.getChildren().get(groupIndex.PLAYER1.getIndex());
        ComboBox player2 = (ComboBox) root.getChildren().get(groupIndex.PLAYER2.getIndex());

        player1.setLayoutX(primaryStage.getWidth() / 4 - 65);
        player1.setLayoutY(primaryStage.getHeight() / 4);
        player2.setLayoutX(primaryStage.getWidth() / 4 * 3 - 55);
        player2.setLayoutY(primaryStage.getHeight() / 4);

        TextField fenField = (TextField) root.getChildren().get(groupIndex.FEN.getIndex());
        fenField.setPrefSize(primaryStage.getWidth()  - (primaryStage.getWidth() / 3) > 550 ? 480 : primaryStage.getWidth() - 2 * (-112.5 + primaryStage.getWidth() / 6 * 2) - 20, 20);
        fenField.setLayoutX(primaryStage.getWidth() - (primaryStage.getWidth() / 3) > 550 ? primaryStage.getWidth() / 2 - 250 : -112.5 + primaryStage.getWidth() / 6 * 2);
        fenField.setLayoutY(primaryStage.getHeight() / 3);

        Button startButton = (Button) root.getChildren().get(groupIndex.START.getIndex());
        startButton.setLayoutY(primaryStage.getHeight() / 2);
        startButton.setLayoutX(primaryStage.getWidth() / 2 - 110);
    }

    enum groupIndex {
        PLAYER1(0),
        PLAYER2(1),
        FEN(2),
        START(3);

        private int index;

        groupIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}

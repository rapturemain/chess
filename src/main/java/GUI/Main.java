package GUI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class Main extends Application {


    public static void main(String[] args) {
        Application.launch(args);
    }

    private static final Group root = new Group();

    @Override
    public void start(final Stage primaryStage) {
        // Init max/min window size
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(391);
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);

        // Init stage and scene
        Scene scene = new Scene(root, 712, 701);
        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Init window
        final List<Window> window = new LinkedList<>();
        window.add(new Start(primaryStage, root));
        window.get(0).init(window);

        // Init resize listener
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
               window.get(0).update();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
               window.get(0).update();
            }
        });
    }
}

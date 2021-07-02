package GUI;

public class Button {
    public Button(int x, int y, javafx.scene.control.Button button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }

    private int x;
    private int y;
    private javafx.scene.control.Button button;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public javafx.scene.control.Button getButton() {
        return button;
    }
}

package component;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class FloatingButton extends Button {

    /**
     * Instantiates a new hover_button.
     */
    private final ImageView addImage;

    public FloatingButton(String url) {
        this.setEffect(new DropShadow());
        this.getStylesheets().add("/application/application.css");
        this.getStyleClass().add("floating-button");
        addImage = new ImageView(new Image(url));
        addImage.setCache(true);
        addImage.setSmooth(true);
        addImage.setFitHeight(25);
        addImage.setFitWidth(25);
        this.setGraphic(addImage);
        this.setShape(new Circle(50));
        this.setPrefSize(50, 50);

    }

    public void setGraphics(String url) {
        this.addImage.setImage(new Image(url));
    }

}

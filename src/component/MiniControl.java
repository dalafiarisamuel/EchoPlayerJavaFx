package component;

import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MiniControl extends Hyperlink {
    private final ImageView icon;

    public MiniControl(String url) {
        this.getStyleClass().add("navigation_button");
        this.setGraphicTextGap(0);
        this.setVisited(false);
        this.setUnderline(false);

        icon = new ImageView(new Image(url));
        icon.setFitHeight(25);
        icon.setFitWidth(25);
        this.setGraphic(icon);

    }

    public void setGraphics(String url) {
        this.icon.setImage(new Image(url));

    }

}

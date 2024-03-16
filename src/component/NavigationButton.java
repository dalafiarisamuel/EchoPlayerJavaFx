package component;

import javafx.scene.Cursor;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NavigationButton extends Hyperlink {

    public NavigationButton(String url) {
        this.getStyleClass().add("navigation_button");
        this.setGraphicTextGap(0);
        this.setVisited(false);
        this.setUnderline(false);
        this.setCursor(Cursor.HAND);

        ImageView icon = new ImageView(new Image(url));
        icon.setFitHeight(35);
        icon.setFitWidth(35);
        this.setGraphic(icon);
    }

}

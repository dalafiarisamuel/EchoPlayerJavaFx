package component;

import application.MusicPlayer;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class OptionsMenu extends HBox {

    public OptionsMenu(String label, String url) {

        this.setAlignment(Pos.CENTER);
        this.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);
        this.setSpacing(10);
        ImageView icon = new ImageView(new Image(url));
        icon.setFitHeight(30);
        icon.setFitWidth(30);
        Text action = new Text(label);
        action.setId("option-action");
        this.getStyleClass().add("options-menu");

        this.getChildren().addAll(icon, action);
    }

}

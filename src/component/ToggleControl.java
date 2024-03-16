package component;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToggleControl extends ToggleButton {

	public ToggleControl(String url) {

		this.getStyleClass().add("toggle-controls");
		ImageView icon = new ImageView(new Image(url));
		icon.setFitHeight(20);
		icon.setFitWidth(20);
		this.setGraphic(icon);

	}

}

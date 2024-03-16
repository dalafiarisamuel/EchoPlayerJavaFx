package component;

import application.MusicPlayer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Toast extends BorderPane {

	private final Pos position;
	private final Text toastMessage;

	public Toast(Node parent) {
		this.position = Pos.CENTER;
		this.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);
		this.toastMessage = new Text();
		this.toastMessage.getStyleClass().add("toast-message");
		this.getStyleClass().add("toast");
		this.setEffect(new DropShadow());

		HBox messageContainer = new HBox();
		messageContainer.getStyleClass().add("toast-message-container");
		messageContainer.setAlignment(Pos.CENTER);
		messageContainer.getChildren().add(this.toastMessage);
		messageContainer.setPadding(new Insets(5, 10, 5, 10));
		this.setCenter(messageContainer);

		setParent(parent);
		animation(parent);

	}

	private void setParent(Node parent) {

		if (parent instanceof StackPane) {

			((StackPane) parent).getChildren().add(this);
			StackPane.setAlignment(parent, this.position);
		}
	}


	public void setToastMessage(String message) {
		this.toastMessage.setText(message);

	}

	private void animation(Node parent) {
		FadeTransition fadeIn = new FadeTransition();
		fadeIn.setNode(this);
		fadeIn.setDuration(Duration.seconds(0.5));
		fadeIn.setFromValue(0);
		fadeIn.setToValue(0.7);
		fadeIn.setCycleCount(1);
		fadeIn.setInterpolator(Interpolator.EASE_OUT);
		fadeIn.setAutoReverse(true);
		fadeIn.setOnFinished(e -> {
			if (parent instanceof StackPane) {
				try {
					Thread.sleep(1100);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				((StackPane) parent).getChildren().remove(this);
			}
		});
		fadeIn.play();
	}
}

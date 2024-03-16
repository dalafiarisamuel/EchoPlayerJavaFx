package component;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Alert extends BorderPane {

    public Button acceptClose, cancelClose;
    private final BorderPane alert;
    public FadeTransition fadeOut;
    public HBox progressContainer;
    public Timeline task;

    /**
     * Instantiates a new alert.
     *
     * @param pane the stack-pane
     */
    public Alert(StackPane pane) {

        alert = new BorderPane();
        alert.getStyleClass().add("alert-view");
        alert.setOpacity(0.8);
        alert.getStylesheets().add("/application/application.css");
        alert.setTranslateY(-70);
        alert.setEffect(new DropShadow());
        alert.setMaxHeight(190);
        alert.setMaxWidth(350);

        task = new Timeline();
        // fade in
        FadeTransition fadeIn = new FadeTransition();
        fadeIn.setNode(alert);
        fadeIn.setDuration(Duration.seconds(0.5));
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.8);
        fadeIn.play();

        // fade out
        fadeOut = new FadeTransition();
        fadeOut.setNode(alert);
        fadeOut.setDuration(Duration.seconds(0.5));
        fadeOut.setFromValue(0.8);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pane.getChildren().remove(alert));

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(30, 0, 0, 0));
        ImageView icon = new ImageView();
        icon.setFitHeight(60);
        icon.setFitWidth(60);
        alert.setTop(content);

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new Insets(5));
        buttonContainer.setAlignment(Pos.CENTER);

        alert.setBottom(buttonContainer);
        StackPane.setAlignment(alert, Pos.CENTER);
        pane.getChildren().add(alert);

        alert.getStyleClass().add("alert-view-closing_request");
        icon.setImage(new Image("/images/ic_error_outline_black_36dp.png"));
        Text closeNotice = new Text("Do you want to exit\n EchoPlayer?");
        closeNotice.setTextAlignment(TextAlignment.LEFT);

        HBox closingContainer = new HBox();
        closingContainer.setAlignment(Pos.CENTER);
        closingContainer.setSpacing(10);
        closingContainer.getChildren().addAll(icon, closeNotice);
        content.setCenter(closingContainer);

        progressContainer = new HBox();
        progressContainer.setAlignment(Pos.CENTER);

        ProgressBar bar = new ProgressBar(0);
        bar.setPadding(new Insets(15, 0, 0, 0));
        bar.setPrefWidth(270);
        progressContainer.getChildren().add(bar);
        progressContainer.setVisible(false);
        alert.setCenter(progressContainer);

        task.getKeyFrames().addAll(new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 10)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 20)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 30)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 40)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 50)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 60)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 70)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 80)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 90)), new KeyFrame(Duration.seconds(2), new KeyValue(bar.progressProperty(), 100)));

        acceptClose = new Button("Exit");
        acceptClose.getStyleClass().addAll("material-button", "material-button-delete");
        acceptClose.setPrefWidth(90);

        cancelClose = new Button("Cancel");
        cancelClose.getStyleClass().addAll("material-button", "material-button-save");
        cancelClose.setPrefWidth(90);
        buttonContainer.getChildren().addAll(cancelClose, acceptClose);

    }
}

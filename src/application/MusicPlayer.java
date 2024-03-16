package application;

import animations.RoundBalls;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The Class MusicPlayer.
 */
public class MusicPlayer extends Application {

    /**
     * The accessor.
     */
    private Stage accessor;

    /**
     * The Constant STYLESHEET_MUSIC.
     */
    public static final String STYLESHEET_MUSIC = "/application/application.css";

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("/images/icon.png"));
        primaryStage.setResizable(false);

        accessor = primaryStage;
        initialiseScreen();
        primaryStage.setScene(initialiseScreen());
        primaryStage.show();

    }

    /**
     * Initialize screen.
     *
     * @return the scene
     */
    public Scene initialiseScreen() {

        VBox container = new VBox();
        container.getStyleClass().add("front-container");
        // container.setStyle("-fx-background-color: red");
        container.setSpacing(50);
        container.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);

        RoundBalls balls = new RoundBalls();
        balls.createCircles();
        balls.timeline.setOnFinished(e -> changeScene());

        container.setAlignment(Pos.CENTER);
        ImageView icon = new ImageView(new Image("/images/front-screen.png"));

        Text title = new Text("Echo Player");
        title.setTextAlignment(TextAlignment.CENTER);
        title.getStyleClass().add("front-title");
        container.getChildren().addAll(icon, title, balls.createCircles());


        return new Scene(container, 655, 690);

    }

    /**
     * Change scene.
     */
    private void changeScene() {

        MusicLayout player = new MusicLayout(accessor);

        Scene scene = new Scene(player, 655, 690);
        scene.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);

        accessor.setTitle("Echo Player");
        accessor.setScene(scene);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

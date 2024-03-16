package animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class RoundBalls {
	public Timeline timeline;

	private final Balls one = new Balls();
	private final Balls two = new Balls();
	private final Balls three = new Balls();
	private final Balls four = new Balls();
	private final Balls five = new Balls();
	private final Balls six = new Balls();
	private final Balls seven = new Balls();
	private final Balls eight = new Balls();
	private final Balls nine = new Balls();

	private final Balls[] balls = { one, two, three, four, five, six, seven, eight, nine };

	public StackPane createCircles() {

		StackPane container = new StackPane();

		FlowPane pane = new FlowPane();
		pane.setEffect(new DropShadow());
		pane.setMaxSize(70, 70);
		pane.setStyle("-fx-background-color: transparent");
		pane.setShape(new Circle(70));
		pane.setAlignment(Pos.CENTER);

		Group ball_container = createTickMarks(balls);

		pane.getChildren().add(ball_container);

		timeline = new Timeline();
		timeline.getKeyFrames().addAll(

				new KeyFrame(Duration.seconds(1.5), new KeyValue(nine.fillProperty(), Color.web("#FFFFFF"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(eight.fillProperty(), Color.web("#6DAA2E"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(seven.fillProperty(), Color.web("#FF33FF"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(six.fillProperty(), Color.web("#ED702F"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(five.fillProperty(), Color.web("#DD5044"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(four.fillProperty(), Color.web("#FFCE44"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(three.fillProperty(), Color.web("#20A464"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(two.fillProperty(), Color.web("#4C8BF5"))),

				new KeyFrame(Duration.seconds(1.5), new KeyValue(one.fillProperty(), Color.web("#00FFFF"))),

				new KeyFrame(Duration.seconds(3.1), new KeyValue(ball_container.rotateProperty(), 1440))

		);
		timeline.setCycleCount(1);
		timeline.setAutoReverse(true);
		timeline.play();

		container.getChildren().add(pane);

		return container;
	}

	private Circle createTic(double angle, Balls ball) {
		ball.setRotate(angle);
		double radius = 30;
		ball.setLayoutX(radius * Math.cos(Math.toRadians(angle)));
		ball.setLayoutY(radius * Math.sin(Math.toRadians(angle)));
		return ball;

	}

	private Group createTickMarks(Balls[] ball) {
		Group group = new Group();
		for (int i = 0; i < ball.length; i++) {
			double angle = ((double) 360 / ball.length) * (i);
			group.getChildren().add(createTic(angle, ball[i]));
		}
		return group;
	}
}

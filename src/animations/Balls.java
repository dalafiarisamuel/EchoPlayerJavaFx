package animations;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * The Class Balls. extends the Circle class, with default radius size of 5px,
 * and color WHITE
 */
public class Balls extends Circle {

	public Balls() {
		super(5, Color.WHITE);
		setCenterX(0);
		setCenterY(0);
	}

}

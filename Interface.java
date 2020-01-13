package tetris;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Interface extends VBox {
	final static int CELL_COUNT_YP = 3;
	final static int CELL_COUNT_XP = 4;

	public static Text score = new Text();
	public static Text level = new Text();
	public static Text clearedLines = new Text();
	Text controls = new Text();
	Text nextPiece = new Text();
	public static int scoreValue;
	
	
	public  Interface() {
		setMinWidth(275);
		setMinHeight(500);
		setMaxWidth(275);
		setMaxHeight(500);
		setSpacing(10);
		
		nextPiece.setFont(Font.font("Impact", 25));
		nextPiece.setText("Next Piece:");
		nextPiece.setFill(Color.YELLOW);
		getChildren().add(nextPiece);

		getChildren().addAll(GameWindow.preview);

		GameWindow.preview.setVisible(true);
		GameWindow.preview.setTranslateX(75);
		GameWindow.preview.setTranslateY(25);
		
		controls.setFont(Font.font("Impact", 25));
		controls.setScaleX(1);
		controls.setScaleY(1);
		controls.setText(
				"P: PAUSE\n\n"
				+ "Left Arrow: LEFT\n\n"
				+ "Right Arrow: RIGHT\n\n"
				+ "Down Arrow: DOWN\n\n"
				+ "Space Bar: DROP PIECE\n\n"
				+ "A: ROTATE LEFT\n\n"
				+ "D: ROTATE RIGHT");
		controls.setFill(Color.YELLOW);
		
		score.setFont(Font.font("Impact", 26));
		score.setScaleX(1);
		score.setScaleY(1);
		score.setFill(Color.LAWNGREEN);
		setScore(0);
		level.setFont(Font.font("Impact", 26));
		level.setScaleX(1);
		level.setScaleY(1);
		level.setFill(Color.MAGENTA);
		level.setText("Level: 1" );
		clearedLines.setFont(Font.font("Impact", 26));
		clearedLines.setScaleX(1);
		clearedLines.setScaleY(1);
		clearedLines.setFill(Color.MAGENTA);
		clearedLines.setText("Lines Cleared: 0" );
		getChildren().add(controls);
		getChildren().add(score);
		getChildren().add(level);
		getChildren().add(clearedLines);
	}
	
	public static void setScore(int sc) {
		score.setText("Score: " + sc);
	}
	
	public static void setLevel(int l) {
		level.setText("Level: " + l);
	}
	
	public static void setLines(int li) {
		clearedLines.setText("Lines Cleared: " + li);
	}
}
	


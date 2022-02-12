/* Main application
* Author: Stephen Williams
* 
* Purpose: A tetris game clone created for a final project in COSC 2030 at Laramie County Community College
* 
*/

package tetris;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class Tetris extends Application {

	public void start(Stage primaryStage) {

		GameWindow gamePane = new GameWindow();
		BorderPane mainPane = new BorderPane();
		Interface ui = new Interface();
		mainPane.setLeft(gamePane);
		mainPane.setRight(ui);
		BorderPane.setMargin(ui, new Insets(20, 20, 20,20));
		BorderPane.setAlignment(ui, Pos.TOP_RIGHT);
		mainPane.setPadding(new Insets(10, 10, 10, 10));
		mainPane.setStyle("-fx-background-color: dimgray");
		gamePane.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.E) {
				gamePane.newPiece();
			}
			if (e.getCode() == KeyCode.ENTER) {
				gamePane.KeyPressed(KeyCode.ENTER);
			}

			if (e.getCode() == KeyCode.RIGHT) {
				gamePane.KeyPressed(KeyCode.RIGHT);
			}
			if (e.getCode() == KeyCode.LEFT) {
				gamePane.KeyPressed(KeyCode.LEFT);
			}
			if (e.getCode() == KeyCode.A) {
				gamePane.KeyPressed(KeyCode.A);
			}
			if (e.getCode() == KeyCode.D) {
				gamePane.KeyPressed(KeyCode.D);
			}
			if (e.getCode() == KeyCode.DOWN) {
				gamePane.KeyPressed(KeyCode.DOWN);
			}
			if (e.getCode() == KeyCode.SPACE) {
				gamePane.KeyPressed(KeyCode.SPACE);
			}
			if (e.getCode() == KeyCode.R) {
				if (gamePane.stopped) {
					restartGame(primaryStage);
				}
			}
		});
		// Create a scene and place it in the stage
		Scene scene = new Scene(mainPane, 640, 768);
		primaryStage.setTitle("Tetris"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
		gamePane.requestFocus();
	}

	// reset score and level, turn background music off and back on, restart the stage
	void restartGame(Stage primaryStage) {
		start(primaryStage);
		GameWindow.setAudio(1);
		GameWindow.setAudio(0);
		Interface.setScore(0);
		Interface.setLevel(1);
	}

	public static void main(String[] args) {
		launch(args);
	}
}

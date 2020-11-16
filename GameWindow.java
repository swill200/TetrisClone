package tetris;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import tetris.GamePiece.*;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tetris.Interface;

public class GameWindow extends Pane {
	// set variables for preview window
	final static int CELL_COUNT_YP = 3;
	final static int CELL_COUNT_XP = 4;
	public static Group previewGroup;
	public int cellHeightP, cellWidthP, previewPieceX = 0, previewPieceY = 0;
	
	// set variables for main game window
	final static int CELL_COUNT_Y = 20;
	final static int CELL_COUNT_X = 10;
	private int cellHeight, cellWidth;
	private Tetrominoes[] board;
	public int curPieceX = 0, curPieceY = 0, levelValue = 1, totalClearedLines = 0;
	public double tempClearedLines = 0;
	private GamePiece curPiece, previewPiece;
	public boolean stopped = false, paused = false, pieceFalling = false, rotating = false;
	public Group curGroup;
	public Timeline gamePlay;
	public StackPane gameOver, pause;
	// Need to add music
// 	public static AudioClip background = new AudioClip(new File("src/tetris/audio/tetris.wav").toURI().toString());
// 	public AudioClip lineClear = new AudioClip(new File("src/tetris/audio/line_clear.wav").toURI().toString());
// 	public AudioClip gameEnd = new AudioClip(new File("src/tetris/audio/game_end.wav").toURI().toString());;
	public static PreviewWindow preview = new PreviewWindow();

	
	public GameWindow() {
		// set dimensions of the game window
		setFocused(true);
		setMinWidth(35 * CELL_COUNT_X);
		setMinHeight(35 * CELL_COUNT_Y);
		setMaxWidth(35 * CELL_COUNT_X);
		setMaxHeight(35 * CELL_COUNT_Y);
		
		// background music, volume turned down to accommodate quiet effects
		background.setCycleCount(Timeline.INDEFINITE);
		background.play(.25);
		
		// establish the game animation
		gamePlay = new Timeline(new KeyFrame(Duration.millis(1000), e -> moveDown()));
		gamePlay.setCycleCount(Timeline.INDEFINITE);
		gamePlay.setRate(1);
		gamePlay.playFromStart();

		// declare objects
		gameOver = new StackPane();
		pause = new StackPane();
		curPiece = new GamePiece();
		previewPiece = new GamePiece();
		
		// board array for tracking piece locations
		board = new Tetrominoes[CELL_COUNT_X * CELL_COUNT_Y + 20];

		// establish the piece cell width and height
		setCellWidth();
		setCellWidthPreview();
		setCellHeight();
		setCellHeightPreview();
		
		// build the background, paint it and place starting piece and preview
		buildBoard();
		for(int i = 0; i < CELL_COUNT_X; i++) {
			for(int j = 0; j < CELL_COUNT_Y; j++) {
				drawGrid(Color.DIMGRAY, i, j, (i * cellWidth), (j * cellWidth));
			}
		}
		paintBoard();
		buildPause();
		newPreview();
		newPiece();

	}

	// game over method, called when the starting piece is placed and can't move
	public void GameOver() {
		gameOver.setMinWidth(35 * CELL_COUNT_X);
		gameOver.setMinHeight(35 * CELL_COUNT_Y);
		gameOver.setVisible(true);
		gameOver.setBackground(new Background(new BackgroundFill(Paint.valueOf("Transparent"),CornerRadii.EMPTY, Insets.EMPTY)));
		Text textGameOver = new Text();
		textGameOver.setText("       GAME OVER\nPress 'R' to Restart...");
		textGameOver.setFont(Font.font("Impact", 18));
		textGameOver.setScaleX(2.3);
		textGameOver.setScaleY(2);
		textGameOver.setFill(Color.WHITE);
		gameOver.getChildren().add(textGameOver);
		getChildren().add(gameOver);
	}

	// draw the background grid, also used to show the stored location of dropped pieces
	public void drawGrid(Color fill, int i, int j, int x, int y) {
		Rectangle grid;
		grid = new Rectangle(cellWidth, cellHeight);
		Lighting lighting = new Lighting(new Light.Distant(235,30, Color.WHITE)); // adds depth to the blocks
		lighting.setSurfaceScale(2);
		grid.setEffect(lighting);
		grid.setX(x);
		grid.setY(y);
		grid.setStroke(Color.BLACK);
		grid.setStrokeWidth(1.5);
		grid.setFill(fill);
		getChildren().add(grid);
	}

	public int setCellHeight() {
		cellHeight = (int)this.getMaxHeight() / CELL_COUNT_Y;
		return cellHeight;
	}

	public int setCellWidth() {
		cellWidth = (int)this.getMaxWidth() / CELL_COUNT_X;
		return cellWidth;
	}

	// method to return the piece type at a given location on the board
	public Tetrominoes pieceAt(int x, int y) { return board[(int) ((y * CELL_COUNT_X) + x)]; }

	// builds an array used for collision checking (stores game piece types)
	private void buildBoard() {
		for (int i = 0; i < board.length; ++i) {
			board[i] = GamePiece.Tetrominoes.Blank;
		}
	}

	// special paint method used to paint the board when rows are being cleared, reads the board array and applies color to the cells based on piece type
	private void repaintBoard() {
		for (int i = 0; i < CELL_COUNT_Y; i++) {
			for (int j = 0; j < CELL_COUNT_X; j++) {
				Tetrominoes gamePiece = pieceAt(j, CELL_COUNT_Y - i - 1);
				if (gamePiece != GamePiece.Tetrominoes.Blank) {
					Rectangle rec = new Rectangle(cellWidth, cellHeight);
					drawPiece(rec, j * cellWidth, i * cellHeight, gamePiece);
					getChildren().add(rec);
				}
			}
		}
	}
	
	// builds 4 new rectangles, calls drawPiece to build the rectangles, and applies them to a group as the current piece
	private void paintBoard() {
		if (curPiece.getPiece() != GamePiece.Tetrominoes.Blank) {
			curGroup = new Group();
			Rectangle rec[] = new Rectangle[4];
			for (int i = 0; i < rec.length; i++) {
				rec[i] = new Rectangle(cellWidth, cellHeight);
			}
			for (int i = 0; i < 4; i++) {
				int x = curPieceX + curPiece.x(i);
				int y = curPieceY - curPiece.y(i);
				drawPiece(rec[i], (0 + x * cellWidth - (cellWidth * 2)), (CELL_COUNT_Y - y - 1) * cellHeight, curPiece.getPiece());
				curGroup.getChildren().add(rec[i]);
			}
			getChildren().add(curGroup);
		}
	}

	// creates the individual rectangles that are used to build pieces
	private void drawPiece (Rectangle rec, int x, int y, Tetrominoes piece) {
		Color colors[] = { Color.DIMGRAY, Color.CYAN, Color.RED, Color.YELLOW, Color.BLUE,
				Color.ALICEBLUE, Color.MEDIUMPURPLE, Color.LAWNGREEN };
		Lighting lighting = new Lighting(new Light.Distant(245, 50, Color.WHITE));
		lighting.setSurfaceScale(50);
		rec.setEffect(lighting);
		rec.setStroke(Color.BLACK);
		rec.setStrokeWidth(1.5);
		rec.setLayoutX(x);
		rec.setLayoutY(y);
		rec.setFill(colors[piece.ordinal()]);
	}

	// set the current piece to the preview piece, call for a new preview, and handle game over situation
	public void newPiece() {
		curPiece.setPiece(previewPiece.getPiece()); 
		newPreview();
		curPieceX = CELL_COUNT_X / 2 + 1;
		curPieceY = CELL_COUNT_Y - 1 + curPiece.minY();
		paintBoard();
		if (!tryMove(curPiece, curPieceX, curPieceY)) {
			curPiece.setPiece(GamePiece.Tetrominoes.Blank);
			gamePlay.stop();
			background.stop();
			gameEnd.setCycleCount(1);
			gameEnd.play();
			stopped = true;
			GameOver();
		}
	}
	
	public boolean stopped() {
		stopped = true;
		return stopped;
	}

	// similar to game over, text box to alert to pause state
	public void buildPause() {
		pause.setMinWidth(35 * CELL_COUNT_X);
		pause.setMinHeight(35 * CELL_COUNT_Y);
		pause.setVisible(true);
		pause.setBackground(new Background(new BackgroundFill(Paint.valueOf("Transparent"),CornerRadii.EMPTY, Insets.EMPTY)));
		Text textPause = new Text();
		textPause.setFont(Font.font("Impact", 20));
		textPause.setText("          Paused\n'Enter' to Resume...");
		textPause.setScaleX(2.3);
		textPause.setScaleY(2);
		textPause.setFill(Color.WHITE);
		pause.getChildren().add(textPause);
	}
	
	public void pause() {
		if (!paused) {
			paused = true;
			getChildren().add(pause);
			gamePlay.pause();
			background.stop();
		}
		else {
			getChildren().remove(pause);
			paused = false;
			gamePlay.play();
			background.play(.25);
		}
	}

	// obtain keyboard input and handle
	public void KeyPressed(KeyCode e) {
		if (stopped || curPiece.getPiece() == GamePiece.Tetrominoes.Blank) {
			return;
		}
		switch(e) {
		case ENTER:
			pause();
			break;
		case RIGHT:
			tryMove(curPiece, curPieceX + 1, curPieceY);
			break;
		case LEFT:
			tryMove(curPiece, curPieceX - 1, curPieceY);
			break;
		case DOWN:
			moveDown();
			break;
		case A:
			tryMove(curPiece.leftRotate(), curPieceX, curPieceY);	
			break;
		case D:
			tryMove(curPiece.rightRotate(), curPieceX, curPieceY);
			break;
		case SPACE:
			dropDown();
			break;
		default:
			break;

		}
	}

	// collision checking
	private boolean tryMove(GamePiece piece, int newX, int newY) {
		for (int i = 0; i < 4; i++) {
			int x = newX + piece.x(i);
			int y = newY - piece.y(i);
			if (x < 0 + 2 || x >= CELL_COUNT_X + 2|| y < 0 || y >= CELL_COUNT_Y) // boundary checks
				return false;
			if (pieceAt(x -2, y) != GamePiece.Tetrominoes.Blank) // if the piece on board array isn't a blank, stop the movement
				return false;
		}
		// remove the group, establish new coordinates and replace the piece to the window
		getChildren().remove(curGroup);
		curPiece = piece;
		curPieceX = newX;
		curPieceY = newY;
		paintBoard();
		return true;
	}

	// 
	private void pieceMoveFinished() {
		for (int i = 0; i < 4; i++) {
			int x = curPieceX + curPiece.x(i);
			int y = curPieceY - curPiece.y(i);
			board[(y * CELL_COUNT_X - 2) + x] = curPiece.getPiece();
		}

		checkFilled();
		// prevents a piece from randomly being painted to the top of the board
		if (!pieceFalling) {
			newPiece();
		}
	}

	private void moveDown() {
		if (!tryMove(curPiece, curPieceX, curPieceY - 1)) {
			pieceMoveFinished();
		}
	}

	// drop the piece all the way to the bottom (or on top of existing pieces)
	private void dropDown() {
		int newY = curPieceY;
		while (newY > 0) {
			if (!tryMove(curPiece, curPieceX, newY - 1))
				break;
			newY --;
		}
		pieceMoveFinished();
	}

	
	private void checkFilled() {
		int fullLines = 0;
		// loop through board array values checking if any row is filled with non-blank pieces
		for (int i = CELL_COUNT_Y; i >= 0; i--) {
			boolean lineFull = true;

			for (int j = 0; j < CELL_COUNT_X; j++) {
				if (pieceAt(j, i) == GamePiece.Tetrominoes.Blank) {
					lineFull = false;
					break;
				}
			}
			// when a full line is found, increase fullLine count, and loop through the board array reading the pieces above that line
			// to apply to the full line
			if (lineFull) {
				fullLines ++;
				for (int k = i; k < CELL_COUNT_Y - 1; k++) {
					for (int j = 0; j < CELL_COUNT_X; j++) {
						board[(k * CELL_COUNT_X) + j] = pieceAt(j, k + 1);
					}
				}
				for(int n = 0; n < CELL_COUNT_X; n++) {
					for(int j = 0; j < CELL_COUNT_Y; j++) {
						drawGrid(Color.DIMGRAY, n, j, (n * cellWidth), (j * cellWidth));
					}
				}
				repaintBoard();

			}
		}
		// update line count, level, and score, reset values
		if (fullLines > 0) {
			tempClearedLines += (double)fullLines;
			totalClearedLines += fullLines;
			Interface.setLines(totalClearedLines);
			if (tempClearedLines / 10 >= 1) {
				gamePlay.setRate(gamePlay.getRate() + .1);
				tempClearedLines = tempClearedLines % 10;
				levelValue += 1;
				Interface.setLevel(levelValue);
			}
			lineClear.setCycleCount(1);
			lineClear.play();
			Interface.scoreValue += (int)(Math.pow(fullLines, 3) * 312.5);
			Interface.setScore(Interface.scoreValue);
			fullLines = 0;
			curPiece.setPiece(GamePiece.Tetrominoes.Blank);
		}
	}
	// used for game restarts
	public static void setAudio(int a) {
		if (a == 1) {
			background.stop();
		}
		else {
			background.play(.25);
		}
	}
	
	// set preview cell height and width
	public int setCellHeightPreview() {
		cellHeightP = (int)preview.getMaxHeight() / CELL_COUNT_YP;
		return cellHeightP;
	}

	public int setCellWidthPreview() {
		cellWidthP = (int)preview.getMaxWidth() / CELL_COUNT_XP;
		return cellWidthP;
	}
	
	// call gamePiece method for a new random preview piece
	public void newPreview() {
		previewPiece.setRandomPreview();
		previewPieceX = CELL_COUNT_XP / 2;
		previewPieceY = CELL_COUNT_YP - 1 + previewPiece.minY();
		paintPreview();
	}
	
	// same as the paintBoard method, but for the preview window
	private void paintPreview() {
		preview.getChildren().remove(previewGroup);
		if (previewPiece.getPiece() != GamePiece.Tetrominoes.Blank) {
			previewGroup = new Group();
			Rectangle recP[] = new Rectangle[4];
			for (int i = 0; i < recP.length; i++) {
				recP[i] = new Rectangle(cellWidthP, cellHeightP);
			}
			for (int i = 0; i < 4; i++) {
				int x = previewPieceX + previewPiece.x(i);
				int y = previewPieceY - previewPiece.y(i);
				drawPreview(recP[i], (0 + x * cellWidthP - (cellWidthP * 2)), (CELL_COUNT_YP - y - 1) * cellHeightP, previewPiece.getPiece());
				previewGroup.getChildren().add(recP[i]);
			}
			preview.getChildren().add(previewGroup);
		}
	}
	
	private void drawPreview (Rectangle rec, int x, int y, Tetrominoes piece) {
		Color colors[] = { Color.DIMGRAY, Color.CYAN, Color.RED, Color.YELLOW, Color.BLUE,
				Color.ALICEBLUE, Color.MEDIUMPURPLE, Color.LAWNGREEN };
		Lighting lighting = new Lighting(new Light.Distant(245, 50, Color.WHITE));
		lighting.setSurfaceScale(50);
		rec.setEffect(lighting);
		rec.setStroke(Color.BLACK);
		rec.setStrokeWidth(1.5);
		rec.setLayoutX(x);
		rec.setLayoutY(y);
		rec.setFill(colors[piece.ordinal()]);
	}
}




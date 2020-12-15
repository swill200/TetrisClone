package tetris;

import java.util.Random;

import javafx.scene.Group;

public class GamePiece extends Group implements Cloneable {
	// establish names for the pieces
	enum Tetrominoes { Blank, Z, S, Line, 
		T, Square, L, J };

		private Tetrominoes gamePiece;
		private int windowCoords[][];
		private int[][][] gamePieceCoords;
		public int color, curColor, x = 0, y = 0;

		// game piece constructor
		public GamePiece() {
			windowCoords = new int[4][2];
			setPiece(Tetrominoes.Blank);
		}

		// x, y coordinates (about 0) for the different pieces
		public void setPiece(Tetrominoes piece) {
			gamePieceCoords = new int[][][] {
				{ { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
				{ { -1, 0 },  { 0, 0 },   { 0, -1 },  { 1, -1 } },
				{ { -1, 0 },  { 0, 0 },   { 0, 1 },   { 1, 1 } },
				{ { -1, 0 },  { 0, 0 },   { 1, 0 },   { 2, 0 } },
				{ { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
				{ { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
				{ { -1, -1 }, { -1, 0 },  { 0, 0 },   { 1, 0 } },
				{ { 1, -1 },  { -1, 0 },  { 0, 0 },   { 1, 0 } }
			};

			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 2; j++) {
					windowCoords[i][j] = gamePieceCoords[piece.ordinal()][i][j];
				}
			}
			gamePiece = piece;
		}
		public void setX(int a, int x) { windowCoords[a][0] = x; }
		public void setY(int a, int y) { windowCoords[a][1] = y; }
		public int x(int a) { return windowCoords[a][0]; }
		public int y(int a) { return windowCoords[a][1]; }
		public Tetrominoes getPiece() { return gamePiece; }
		public void setIndex(int index) { y = index; }
		
		public void setRandomPreview() {
			Random randP = new Random();
			int x = Math.abs(randP.nextInt()) % 7 + 1;
//			 x = 3; // debugging line
			Tetrominoes[] valueP = Tetrominoes.values();
			setPiece(valueP[x]);
		}
		
		// determines the Y height of the piece for collision checking
		public int minY() {
			int a = windowCoords[0][1];
			for (int i=0; i < 4; i++) {
				a = Math.min(a, windowCoords[i][1]);
			}
			return a;
		}

		// loop through and set the piece coordinate y to -x and the x to y
		public GamePiece leftRotate() {
			if (gamePiece == Tetrominoes.Square)
				return this;

			GamePiece value = new GamePiece();
			value.gamePiece = gamePiece;

			for (int i = 0; i < 4; ++i) {
				value.setX(i, y(i));
				value.setY(i, -x(i));
			}
			return value;
		}

		// loop through and set the piece coordinate x to -y and the y to x
		public GamePiece rightRotate() {
			if (gamePiece == Tetrominoes.Square)
				return this;

			GamePiece value = new GamePiece();
			value.gamePiece = gamePiece;

			for (int i = 0; i < 4; ++i) {
				value.setX(i, -y(i));
				value.setY(i, x(i));
			}
			return value;
		}
}


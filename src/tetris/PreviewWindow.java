package tetris;

import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class PreviewWindow extends Pane {
	final static int CELL_COUNT_YP = 3;
	final static int CELL_COUNT_XP = 4;
	public Group previewGroup;
	public int cellHeightP, cellWidthP, previewPieceX = 0, previewPieceY = 0;
	

	public PreviewWindow() {
		setMinWidth(35 * CELL_COUNT_XP);
		setMinHeight(35 * CELL_COUNT_YP);
		setMaxWidth(35 * CELL_COUNT_XP);
		setMaxHeight(35 * CELL_COUNT_YP);
		setCellHeightPreview();
		setCellWidthPreview();
	}

	public int setCellHeightPreview() {
		cellHeightP = (int)this.getMaxHeight() / CELL_COUNT_YP;
		return cellHeightP;
	}

	public int setCellWidthPreview() {
		cellWidthP = (int)this.getMaxWidth() / CELL_COUNT_XP;
		return cellWidthP;
	}
	
}

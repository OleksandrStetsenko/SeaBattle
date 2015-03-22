package view;


import group11.protocol.model.*;
import model.GameModel;

import java.awt.*;

public class PanelFieldOpponent extends PanelField {

    private GameModel gameModel;

	public PanelFieldOpponent(GameModel gameModel) {
        this.gameModel = gameModel;
	}

	protected Color getColorByStateElement(int state) {

        switch (state) {
		case Cell.CELL_BORDER:
            return new Color(109, 161, 255);
		case Cell.CELL_WATER:
            return new Color(109, 161, 255);
		case Cell.CELL_WELL:
            return new Color(109, 161, 255);
		case Cell.CELL_BROKEN:
			return Color.red;
		case Cell.CELL_KILLED:
			return Color.gray;
		case Cell.CELL_MISSED:
			return Color.black;
		}

        return new Color(109, 161, 255);
		
	}

    @Override
    public Field getField() {
        return gameModel.getOpponentFieldModel();
    }
}

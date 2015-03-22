package view;

import group11.protocol.model.*;
import model.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

abstract public class PanelField extends JPanel implements ModelSubscriber {

    private static final Logger log = Logger.getLogger(PanelField.class.getName());

	abstract public Field getField();

	private int getCellWidth() {
		return getWidth() / getField().getWidth();
	}

	private int getCellHeight() {
		return getHeight() / getField().getHeight();
	}

    //the field of each player has its own coloring
	abstract protected Color getColorByStateElement(int state);
	
	@Override
	protected void paintComponent(Graphics g) {

        log.debug(Settings.DBG_Draw);

        super.paintComponent(g);

        // draw vertical lines
        for(int i = 0; i < getField().getWidth() + 1; i++) {
            g.drawLine(i * getCellWidth(), 0, i * getCellWidth(), getCellHeight() * getField().getHeight());
        }

        // draw horizontal lines
        for(int i = 0; i < getField().getHeight() + 1; i++) {
            g.drawLine(0, i * getCellHeight(), getCellWidth() * getField().getWidth(), i * getCellHeight());
        }

        // draw cells
        for(int j = 0; j < getField().getHeight(); j++) {
            for(int i = 0; i < getField().getWidth(); i++) {
                int state = getField().getCell(i, j).getState();
                g.setColor(getColorByStateElement(state));
                if (state == Cell.CELL_MISSED) {
                    g.fillRect(i * getCellWidth() + (getCellWidth() / 2) - 1,
                            j * getCellHeight() + (getCellHeight() / 2) - 1,
                            4,
                            4);
                } else {
                    g.fillRect(i * getCellWidth()+1,
                            j * getCellHeight()+1,
                            getCellWidth() - 1,
                            getCellHeight() - 1);
                }

            }
        }

	}

	@Override
	public void update() {
		this.repaint();
	}
	
}

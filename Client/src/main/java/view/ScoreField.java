package view;

import group11.protocol.model.*;
import model.GameModel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScoreField extends JPanel implements ModelSubscriber {

    static Logger log = Logger.getLogger(ScoreField.class.getName());

    private String currentUser;
    private GameModel model;

	public ScoreField(GameModel model, String currentUser) {
		this.model = model;
        this.currentUser = currentUser;
	}
	
	@Override
	protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        int numShip;
        if (currentUser.equals(model.getMe())) {
            numShip = model.getOpponentFieldModel().getMaxShip();
        } else {
            numShip = model.getMyFieldModel().getMaxShip();
        }

		int[] m = new int[numShip];
		for(int i = 0; i < 0; i++) {
			m[i] = 0;
		}


        ArrayList<Ship> ships;
        if (currentUser.equals(model.getMe())) {
            ships = model.getOpponentFieldModel().getShips();
        } else {
            ships = model.getMyFieldModel().getShips();
        }

		for(Ship ship : ships) {
			if (ship.getState() != Ship.SHIP_KILLED) {
				m[ship.getSize() - 1] ++;
			}
		}

		for(int i = 0; i < numShip; i++) {
            for(int j = 0; j < (i+1); j++) {
                g.setColor(Color.gray);
                g.fillRect(j * 10 + 8, i * 10 + 5, 8, 8);
            }
            g.setColor(Color.black);
            g.drawString(String.valueOf(m[i]), 78, i * 10 + 12);
        }

		//int so = model.getOpponentFieldModel().getNumLiveShips();
        log.debug("opponent live ships "+ model.getOpponentFieldModel().getNumLiveShips());
		//int sp = model.getMyFieldModel().getNumLiveShips();
        log.debug("my live ships "+ model.getMyFieldModel().getNumLiveShips());

	}

	@Override
	public void update() {
		this.repaint();
	}

}

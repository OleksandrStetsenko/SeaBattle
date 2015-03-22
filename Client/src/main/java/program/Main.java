package program;

import java.io.IOException;

import controller.EventLoop;
import controller.SeaBattleController;
import model.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;

public class Main {

    static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        log.debug("Starting program...");

        try {
            Settings.readSettings();
        } catch (Exception e) {
            log.error(Settings.ERR_CannotReadSettings);
        }

        try {
            SeaBattleController seaBattleController = new SeaBattleController();
            EventLoop eventLoop = new EventLoop(seaBattleController);
            eventLoop.setDaemon(true);
            eventLoop.start();

            //wait 3 sec before receiving
            Thread.sleep(Settings.MILLIS_WAIT);
        } catch (IOException e) {
            log.error(e);
            JOptionPane.showMessageDialog(null, Settings.ERR_ConnectionRefused, "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e) {
            log.debug(e);
        }


    }

}

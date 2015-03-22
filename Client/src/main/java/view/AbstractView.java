package view;

import model.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;

public abstract class AbstractView {

    static Logger log = Logger.getLogger(AbstractView.class.getName());

    public static final String ACTION_LEAVE_GAME = "leave game";
    public static final String ACTION_EXIT  = "exit";
    private boolean wasClose = false;

    /**
     * Returns frame of view
     * @return frame
     */
    public abstract JFrame getFrame();

    /**
     * Initializing user interface has its own procedure
     */
    protected abstract void initializeUI();

    /**
     * Updating of each frame has its own procedure
     */
    public abstract void update();

    /**
     * Make as visible current frame
     */
    public void showView() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getFrame().setVisible(true);
                log.debug(Settings.DBG_ShowView);
            }
        });

    }

    /**
     * If current frame is not visible - make as invisible and dispose resources
     */
    public void closeView() {

        if (!getFrame().isVisible()) {
            return;
        }

        getFrame().setVisible(false);
        getFrame().dispose();

        log.debug(Settings.DBG_ClosingView);

        setClose(true);

    }


    public void showError(String message) {
        log.error("error message: " + message);
        JOptionPane.showMessageDialog(getFrame(), message, "Error",
                JOptionPane.ERROR_MESSAGE);

    }

    public void showInfoMessage(String message) {
        log.debug("info message: " + message);
        JOptionPane.showMessageDialog(getFrame(), message, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void setClose(boolean wasClose) {
        this.wasClose = wasClose;
    }

    public boolean wasClose() {
        return wasClose;
    }
}

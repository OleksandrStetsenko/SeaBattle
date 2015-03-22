package controller;

import group11.protocol.*;
import group11.protocol.model.*;
import group11.protocol.pack.*;
import model.GameModel;
import model.Settings;
import model.UserModel;
import org.apache.log4j.Logger;
import view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

/**
 * Manages events
 */
public class SeaBattleController extends MouseAdapter implements ActionListener {

    private static final Logger log = Logger.getLogger(SeaBattleController.class.getName());
    // static block, executes when class is loaded first time
    static {
        try {
            // Set system gui skin
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.error(e);
            System.exit(2);
        }
    }

    private Transport transport;
    public SeaBattleView seaBattleView;
    private AuthorizationDialog authDialog;
    private UserListView userListView;
    private UserModel userModel;
    private GameModel gameModel;
    private String myUsername;
    private String opponentUsername;


    public SeaBattleController() throws IOException {
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(Settings.HOST, Settings.PORT), Settings.SO_TIMEOUT);
        transport = new TransportXml(clientSocket);
	}

    public Transport getTransport() {
        return transport;
    }

    private void createSeaBattleView(Field field) {

        gameModel = new GameModel(field, myUsername, opponentUsername);

        seaBattleView = new SeaBattleView(gameModel);
        seaBattleView.addActionListener(this);
        seaBattleView.addMouseListener(this);

    }


    public void updateSeaBattleModel(Field field, String username) {

        if (seaBattleView == null || seaBattleView.wasClose()) {
            createSeaBattleView(field);
        } else {
            //update GameModel
            gameModel.updateSeaBattleModel(field, username);
        }

        closeUserList();
        showGameWindow();

    }

    public void createUserListView(Set<User> userSet) {

        userModel = new UserModel(userSet, myUsername);

        userListView = new UserListView(userModel);
        userListView.setListener(this);

    }

    public void showUserList() {
        userListView.showView();
        userListView.setEnabledConnect(true);
    }
    
    public void closeUserList() {
        userListView.closeView();
    }

    public void showGameWindow() {
        seaBattleView.showView();
    }

    public void closeGameWindow() {
        seaBattleView.closeView();
    }
    
    public void updateUserSet(Set<User> userSet) {

        if (userListView == null) {
            createUserListView(userSet);
        } else {
            userModel.updateUserSet(userSet);
        }

        showUserList();

    }

    /**
     * Send request to the opponent
     * @param opponent
     */
    public void sendRequest(User opponent) {
        opponentUsername = opponent.getNick();
        try {
            getTransport().send(new PlayRequest(opponentUsername));
        } catch (IOException e) {
            showError(Settings.ERR_ConnectionErr);
        }
    }

	@Override
	public void actionPerformed(ActionEvent event) {

        if (event.getActionCommand().equals(SeaBattleView.ACTION_LEAVE_GAME)) {
            log.debug(Settings.DBG_LeavingGame);

            try {
                getTransport().send(new Msg("ExitGame"));
            } catch (IOException e) {

            }

            seaBattleView.showInfoMessage(Settings.INF_LeavingGame);
            closeGameWindow();
            showUserList();
		}

		if (event.getActionCommand().equals(UserListView.ACTION_EXIT) ||
            event.getActionCommand().equals(AuthorizationDialog.EXIT)) {

            log.debug(Settings.DBG_ExitProgram);
            try {
                getTransport().send(new Msg("Exit"));
            } catch (IOException e) {

            }

            try {
                Settings.writeSettings();
            } catch (Exception e) {
                showError(Settings.ERR_SaveSettingsErr);
            }
            System.exit(0);
		}

	}

	public void mousePressed(MouseEvent e) {

        //get two fields: the first is visible to a user; the cursor coordinates can be calculated on it
        //the second is a model field that contains cells with indexes
        PanelField panelField =  seaBattleView.getOpponentPanel();
        String currentUser = gameModel.getCurrentPlayer();

        Field field = gameModel.getOpponentFieldModel();

        //to find the x coordinate of the cell on which player clicked
        //divide the field width in pixels by the number of  game field cells
        //then divide the x coordinate of the mouse location where the user clicked by the number of
        //pixels one cell. The result is the x index of the game field cell
		int x = e.getX() / (panelField.getWidth() / field.getWidth());
        //do the same with the y coordinate
		int y = e.getY() / (panelField.getHeight() / field.getHeight());

        //check the existence of this cell on the game field
		if ( field.isBound(x, y) ) {
            //if it is true then shoot
            try {
                getTransport().send(new ShootMessage(currentUser, x, y));
            } catch (IOException e1) {
                showError(Settings.ERR_ConnectionErr);
                closeGameWindow();
                showUserList();
            }
        }

	}

    public static void showError(String message) {

        log.error(message);
        JOptionPane.showMessageDialog(null, message, "Error",
                JOptionPane.ERROR_MESSAGE);

    }

    public void showAuthorizationWindow() {

        if (authDialog == null) {
            //create new if it first time
            authDialog = new AuthorizationDialog(this);
        }
        authDialog.setDefaultUser();

        if (authDialog.showDialog(null, Settings.INF_DialogTitle)) {
            // if accepted, retrieve user input
            log.debug("myUsername " + authDialog.getUsername() + ", pass: " + authDialog.getPassword());

            myUsername = authDialog.getUsername();
            String pass = authDialog.getPassword();

            if (authDialog.getReg()) {
                log.debug("Registration: username - " + myUsername + " pass - " + pass);
                try {
                    getTransport().send(new RegAuth(myUsername, pass, RegAuth.Type.Register));
                } catch (IOException e) {
                    showError(Settings.ERR_ConnectionErr);
                    System.exit(0);
                }
            } else {
                log.debug("Authorization: username - " + myUsername + " pass - " + pass);
                try {
                    getTransport().send(new RegAuth(myUsername, pass, RegAuth.Type.Authorization));
                } catch (IOException e) {
                    showError(Settings.ERR_ConnectionErr);
                    System.exit(0);
                }
            }
        }

    }

    public void showConfirm(String message, String nick) {

        int answer = JOptionPane.showConfirmDialog(userListView.getFrame(), message);
        try {
            if (answer == JOptionPane.YES_OPTION) {
                opponentUsername = nick;
                getTransport().send(new Msg("YesBattle"));
            } else {
                getTransport().send(new Msg("NoBattle"));
            }
        } catch (IOException e) {
            showError(Settings.ERR_ConnectionErr);
        }
    }

    public void lostAction() {
        seaBattleView.showInfoMessage(Settings.INF_Looser);
        closeGameWindow();
        showUserList();
    }

    public void wonAction() {
        seaBattleView.showInfoMessage(Settings.INF_Winner);
        closeGameWindow();
        showUserList();
    }

    public void showInfoMessage(String message) {
        seaBattleView.showInfoMessage(message);
    }

}

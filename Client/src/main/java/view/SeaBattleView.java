package view;

import model.GameModel;
import model.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SeaBattleView extends AbstractView implements Observer {

    private static final String FRAME_TITLE = "Sea battle game";
    static Logger log = Logger.getLogger(SeaBattleView.class.getName());
    private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private ArrayList<MouseListener> mouseListeners = new ArrayList<MouseListener>();


    private GameModel gameModel;

    private JFrame frame;

    private PanelFieldPlayer playerPanel;
    private PanelFieldOpponent opponentPanel;
    private ScoreField panelScore;

    /**
     * Creating the view
     * @param gameModel
     */
    public SeaBattleView(GameModel gameModel) {

        this.gameModel = gameModel;
        this.gameModel.observable().addObserver(this);
        initializeUI();

        log.debug(Settings.DBG_CreatingView);

    }

    /**
     * creating frame
     */
    @Override
    protected void initializeUI() {

        frame = new JFrame(FRAME_TITLE + " [" + gameModel.getMe() + "]");

        frame.setResizable(false);
        frame.setBounds(400, 300, 550, 280);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2,
                (screenSize.height - frame.getHeight()) / 2);

        //name of opponent
        JPanel panelOpponent = new JPanel();
        panelOpponent.setLayout(new FlowLayout());

        String opponentName = gameModel.getOpponent();

        JLabel nameLabel = new JLabel("Your opponent: ");
        JLabel name = new JLabel(opponentName);
        name.setFont(name.getFont().deriveFont(
                name.getFont().getStyle() | Font.BOLD));
        panelOpponent.add(nameLabel);
        panelOpponent.add(name);

        frame.add(panelOpponent, BorderLayout.NORTH);

        //game panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new FlowLayout());

        //my panel
        playerPanel = new PanelFieldPlayer(gameModel);
        playerPanel.setBorder(new EmptyBorder(0, 0, 151, 151));
        gamePanel.add(playerPanel);

        //score
        panelScore = new ScoreField(gameModel, gameModel.getCurrentPlayer());
        panelScore.setBorder(new EmptyBorder(0, 0, 75, 75));
        gamePanel.add(panelScore);

        //opponent panel
        opponentPanel = new PanelFieldOpponent(gameModel);
        opponentPanel.setBorder(new EmptyBorder(0, 0, 151, 151));
        opponentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                fireMouseAction(e);
            }
        });
        gamePanel.add(opponentPanel);

        frame.add(gamePanel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 477, 21);
        frame.setJMenuBar(menuBar);

        JMenu menuGame = new JMenu("Game");
        menuBar.add(menuGame);

        JMenuItem menuLeaveGame = new JMenuItem("Leave game");
        menuLeaveGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireAction(ACTION_LEAVE_GAME);
            }
        });
        menuGame.add(menuLeaveGame);

        JMenuItem menuExit = new JMenuItem("Exit");
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireAction(ACTION_LEAVE_GAME);
            }
        });
        menuGame.add(menuExit);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fireAction(ACTION_LEAVE_GAME);
            }
        });

    }

    /** Add listener of seaBattleView */
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /** Add listener of seaBattleView */
    public void addMouseListener(MouseListener listener) {
        mouseListeners.add(listener);
    }

    /**
     * Update model data and show them in seaBattleView
     */
    @Override
    public void update() {
        update(null, null);
    }

    /**
     * Update model data and show them in seaBattleView
     */
    public void update(Observable source, Object arg) {
        log.debug(Settings.DBG_UpdatingView);
        playerPanel.repaint();
        opponentPanel.repaint();
        panelScore.repaint();
    }

    /**
     * Do action in controller when button is pressed
     * @param command
     */
    protected void fireAction(String command) {

        ActionEvent actionEvent = new ActionEvent(this, 0, command);
        for (Object listener : actionListeners) {
            ((ActionListener) listener).actionPerformed(actionEvent);
        }

    }

    /**
     * Do action when mouse is pressed
     * @param event
     */
    protected void fireMouseAction(MouseEvent event) {

        for (Object listener : mouseListeners) {
            ((MouseListener) listener).mousePressed(event);
        }

    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    public PanelFieldOpponent getOpponentPanel() {
        return opponentPanel;
    }

}


package view;

import controller.SeaBattleController;
import group11.protocol.User;
import model.Settings;
import model.UserModel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;


public class UserListView extends AbstractView implements Observer {

    static Logger log = Logger.getLogger(UserListView.class.getName());

    private UserModel model;
    private UserListTableModel tableModel;
    private JPanel connectPanel;
    private JButton connectButton;
    private JScrollPane userListScrollPane;
    private JTable userListTable;
    private SeaBattleController listener;
    private int rowIndex;
    private User selectedUser;
    private JFrame frame;

    /** Creating */
    public UserListView(UserModel model) {

        this.model = model;

        this.model.observable().addObserver(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initializeUI();
                update();
            }
        });
    }

    /** Initialize components */
    @Override
    protected void initializeUI() {

        frame = new JFrame();
        frame.setBounds(300, 300, 300, 300);
        frame.setTitle("Sea battle " + "[" + model.getMyUsername() + "]");

        connectPanel = new JPanel();
        connectButton = new JButton();
        userListScrollPane = new JScrollPane();

        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(200, 100));

        //======== connectPanel ========
        {
            connectPanel.setLayout(new FlowLayout());

            //---- Connect ----
            connectButton.setText("Connect");
            connectButton.addActionListener(new ConnectAction());
            connectPanel.add(connectButton);

        }
        frame.add(connectPanel, BorderLayout.NORTH);

        //======== userListScrollPane ========
        tableModel = new UserListTableModel();
        userListTable = new JTable(tableModel);

        {
            userListScrollPane.setViewportView(userListTable);
        }
        frame.add(userListScrollPane, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ActionEvent actionEvent = new ActionEvent(this, 0, ACTION_EXIT);
                listener.actionPerformed(actionEvent);
            }
        });

    }

    public void setListener(SeaBattleController listener) {
        this.listener = listener;
    }

    @Override
    public void update() {
        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        tableModel.setUserSet(model.getUserSet());
        userListTable.setModel(tableModel);

        frame.validate();
        frame.repaint();
    }


    /** Listener, fire when new task are edit */
    private class ConnectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            rowIndex = userListTable.getSelectedRow();
            if (rowIndex != -1) {
                selectedUser = tableModel.getUser(rowIndex);
                listener.sendRequest(selectedUser);
                setEnabledConnect(false);
            }

        }
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    public void setEnabledConnect(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                connectButton.setEnabled(enabled);
            }
        });
    }

}

package view;

import controller.SeaBattleController;
import model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorizationDialog extends JPanel {

    public static final String EXIT = "exit";
    private final SeaBattleController listener;
    private boolean ok;
    private JDialog dialog;
    private JButton okButton;
    private final JTextField username;
    private final JPasswordField passwordField;
    private final JCheckBox regCheckBox;

    public AuthorizationDialog(final SeaBattleController listener) {

        this.listener = listener;

        setLayout(new BorderLayout());

        //username and pass
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");

        username = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginPanel.add(usernameLabel, new GBC(0,0));
        loginPanel.add(username, new GBC(1,0));
        loginPanel.add(passwordLabel, new GBC(0,1));
        loginPanel.add(passwordField, new GBC(1,1));

        regCheckBox = new JCheckBox("Register");
        loginPanel.add(regCheckBox, new GBC(0,2));

        this.add(loginPanel, BorderLayout.CENTER);

        // create Ok and Cancel buttons that terminate the dialog
        JPanel buttonPanel = new JPanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){

                if ( ! isUsernameCorrect() ) {
                    JOptionPane.showMessageDialog(dialog,
                            Settings.DBG_InvalidUsername,
                            "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ( ! isPasswordCorrect() ) {
                    JOptionPane.showMessageDialog(dialog,
                            Settings.DBG_InvalidPass,
                            "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ok = true;
                Settings.setPassword(getPassword());
                Settings.setUsername(getUsername());
                dialog.setVisible(false);
            }
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                ActionEvent actionEvent = new ActionEvent(this, 0, EXIT);
                listener.actionPerformed(actionEvent);
            }
        });
        buttonPanel.add(cancelButton);
    }

    private boolean isPasswordCorrect() {
        Pattern pattern = Settings.PASS_PATTERN;
        Matcher matcher = pattern.matcher(getPassword());
        return matcher.matches();
    }

    private boolean isUsernameCorrect() {
        Pattern pattern = Settings.USERNAME_PATTERN;
        Matcher matcher = pattern.matcher(getUsername());
        return matcher.matches();
    }

    public String getUsername() {
        return (username.getText() == null) ? "" : username.getText();
    }

    public String getPassword() {
        return (passwordField.getPassword() == null) ? "" : new String(passwordField.getPassword());
    }

    public boolean getReg() {
        return regCheckBox.isSelected();
    }

    public boolean showDialog(Component parent, String title)
    {
        ok = false;

        // locate the owner frame
        Frame owner = null;
        if (parent instanceof Frame)
            owner = (Frame) parent;
        else
            owner = (Frame)SwingUtilities.getAncestorOfClass(
                    Frame.class, parent);

        // if first time, or if owner has changed, make new dialog

        if (dialog == null || dialog.getOwner() != owner)
        {
            dialog = new JDialog(owner, true);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    ActionEvent actionEvent = new ActionEvent(this, 0, EXIT);
                    listener.actionPerformed(actionEvent);
                }
            });
            dialog.getContentPane().add(this);
            dialog.getRootPane().setDefaultButton(okButton);
            dialog.pack();
        }

        // set title and show dialog
        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;
    }

    /**
     * Import the settings, and set the default user
     */
    public void setDefaultUser() {
        this.username.setText(Settings.getUsername());
        this.passwordField.setText(Settings.getPassword());
    }
}

package view;

import group11.protocol.User;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UserListTableModel extends AbstractTableModel {

    private static final Logger log = Logger.getLogger(UserListTableModel.class.getName());

    private int columnCount = 2;
    private Set<User> userSet;
    private String[] columnNames = {"User", "Rank"};

    public UserListTableModel() {
        userSet = new HashSet<User>();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return userSet.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return getUser(rowIndex).getNick();
            case 1:
                return getUser(rowIndex).getRang();
            default:
                return "";
        }
    }

    public void setUserSet(Set<User> userList) {
        userSet =  userList;
        fireTableDataChanged();
    }

    public User getUser(int index) {

        User returnedUser = null;

        Iterator<User> it = userSet.iterator();

        int i = 0;
        while (it.hasNext()) {
            User user = it.next();
            if (i == index) {
                returnedUser = user;
                break;
            }

            i++;
        }

        return returnedUser;
    }

}

package controller;

import group11.protocol.pack.*;
import model.Settings;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Receives events from the server
 */
public class EventLoop extends Thread {

    static Logger log = Logger.getLogger(EventLoop.class.getName());

    private SeaBattleController listener;

    public EventLoop(SeaBattleController listener) {

        log.debug(Settings.DBG_CreatingEventLoop);
        this.listener = listener;

    }

    @Override
    public void run() {

        log.debug(Settings.DBG_StartEventLoop);

        while (! interrupted()) {
            try {
                dispatchEvent(listener.getTransport().receive());
            } catch (IOException e) {
                SeaBattleController.showError(Settings.ERR_ConnectionReset);
                interrupt();
            }
        }

    }

    /**
     * Dispatches events and reports the controller what to do
     * @param event event from server
     */
    protected void dispatchEvent(Object event) {

        if (event == null) {
            log.debug(Settings.DBG_NULL_EVENT);
            return;
        }

        if (event.getClass().getSimpleName().equals("ListUsers")) {
            ListUsers str = (ListUsers) event;
            if (str.users != null) {
                listener.updateUserSet(str.users);
            }
        }

        if (event.getClass().getSimpleName().equals("NickException")) {
            NickException str = (NickException) event;
            listener.showError(str.message);
            listener.showAuthorizationWindow();
        }

        if (event.getClass().getSimpleName().equals("FieldToSend")) {
            FieldToSend fieldToSend = (FieldToSend) event;
            listener.updateSeaBattleModel(fieldToSend.getField(), fieldToSend.getNick());
        }

        if (event.getClass().getSimpleName().equals("PlayRequest")) {
            PlayRequest playRequest = (PlayRequest) event;
            listener.showConfirm(Settings.ANS_PlayWith + playRequest.nick + "?", playRequest.nick);
        }

        if (event.getClass().getSimpleName().equals("Msg")) {
            Msg msg = (Msg) event;

            if (msg.message.equals("Ok")) {
                listener.showAuthorizationWindow();
            } else if (msg.message.equals("NoBattle")) {
                listener.showError(Settings.ERR_UserRefused);
            } else if (msg.message.equals("Lost")) {
                listener.lostAction();
            } else if (msg.message.equals("Won")) {
                listener.wonAction();
            } else {
                listener.showInfoMessage(msg.message);
                listener.closeGameWindow();
                listener.showUserList();
            }

        }

    }

}

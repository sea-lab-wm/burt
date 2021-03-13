package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

public class SelectAppAction extends ChatbotAction {

    @Override
    public MessageObj execute() {
        return new MessageObj("Sure. To start, please select the app that is having the problem");
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}

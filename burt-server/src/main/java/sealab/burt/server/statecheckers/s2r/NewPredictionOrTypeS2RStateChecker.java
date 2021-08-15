package sealab.burt.server.statecheckers.s2r;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;

import static sealab.burt.server.StateVariable.*;

public class NewPredictionOrTypeS2RStateChecker extends S2RDescriptionStateChecker {
    public NewPredictionOrTypeS2RStateChecker() {
        super();
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {
        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        MessageObj message = msg.getFirstMessage();
        if(message!=null && "next_predictions".equals(message.getMessage()))
            return ActionName.PREDICT_NEXT_S2R_PATH;
        else{
            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTING_S2R);
            state.remove(PREDICTED_S2R_PATHS_WITH_LOOPS);
            state.remove(PREDICTED_S2R_NUMBER_OF_PATHS);

            return super.nextAction(state);
        }
    }
}

import ApiClient from "./ApiClient";

class ActionProvider {

    constructor(createChatBotMessage, setStateFunc, createClientMessage, sessionIdObj, setStepsState, setTipState) {
        this.createChatBotMessage = createChatBotMessage;
        this.createClientMessage = createClientMessage;
        this.setState = setStateFunc;
        this.sessionId = sessionIdObj;
        this.setStepsState = setStepsState;
        this.setTipState = setTipState;
    }

    createUserMsg(msg) {
        return this.createClientMessage(msg)
    }

    createChatBotMsg(msg, options) {
        return this.createChatBotMessage(
            msg, options
        )
    }

    removeMsg(lastMsgId) {
        let fn = prevState => {
            const newMessages = [...prevState.messages];
            const idx = newMessages.findIndex(x => x.id === lastMsgId)
            newMessages.splice(idx, 1)
            ApiClient.saveMessages(newMessages)
            return {
                ...prevState, messages: [...newMessages]
            }
        };
        this.setState(fn)
    }

    updateChatbotState(message, saveMsg) {

        // NOTE: This function is set in the constructor, and is passed in
        // from the top level Chatbot component. The setState function here
        // actually manipulates the top level state of the Chatbot, so it's
        // important that we make sure that we preserve the previous state.

        let fn = prevState => {
            let newMessages = [...prevState.messages, message];
            if (saveMsg || saveMsg === undefined)
                ApiClient.saveMessages(newMessages)
            return {
                ...prevState, messages: newMessages
            }
        };
        this.setState(fn)

    }

    updateAllStepHistory(steps) {
        let fn = prevState => {
            return {
                ...prevState, steps: steps
            }
        };
        this.setStepsState(fn)
    }
}

export default ActionProvider;



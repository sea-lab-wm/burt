import ApiClient from "./ApiClient";
import config from '../config';

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
        // Store the endpoint with the step so that it can be updated with a user provided file 
        // User provided files are not initially hosted on the same server so we need to store 
        // the endpoint with the file location to effectively render images from the various servers
        steps.map((step, index) => {step.value2 = config.serverEndpoint + step.value2})

        let fn = prevState => {
            return {
                ...prevState, steps: steps
            }
        };
        this.setStepsState(fn)
    }
}

export default ActionProvider;



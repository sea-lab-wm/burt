// import Chatbot from "react-chatbot-kit";

const axios = require('axios')
// ActionProvider starter code
// import sessions from "./session/Session";
const context = "";
class ActionProvider{

    constructor(createChatBotMessage, setStateFunc, createClientMessage,  sessionIdObj, setStepsState, setTipState) {
        this.createChatBotMessage = createChatBotMessage;
        this.createClientMessage = createClientMessage;
        this.setState = setStateFunc;
        this.sessionId = sessionIdObj;
        this.setStepsState = setStepsState;
        this.setTipState = setTipState;
    }

    createUserMsg(msg){
        return this.createClientMessage(msg)
    }

    createChatBotMsg(msg, options){
        return this.createChatBotMessage(
           msg, options
        )
    }

    removeMsg(lastMsgId){
        let fn = prevState => {
            const newMessages = [...prevState.messages];
            const idx = newMessages.findIndex(x => x.id === lastMsgId)
            newMessages.splice(idx, 1)
            return {
                ...prevState, messages: [...newMessages]
            }
        };
        this.setState(fn)
    }

    updateChatbotState2(message, values, values2) {
        const messageObj = this.createChatBotMessage(
            message.message,
            {
                widget: message.widget,
            }
        );

        // const messageObj2 = this.createChatBotMessage(
        //     " Got it. Just to confirm, can you select the Chikii screen that is having the problem? " + "Please hit the “Done” button when you are done.",
        //     {
        //         widget: "OneScreenOption"
        //     })

        console.log(messageObj)
        // console.log(messageObj2)

        this.setState(prevState => ({ ...prevState, messages: [...prevState.messages, messageObj], app_list: values, app_values: values2 }))

    }

    updateChatbotState(message) {

    // NOTE: This function is set in the constructor, and is passed in
    // from the top level Chatbot component. The setState function here
    // actually manipulates the top level state of the Chatbot, so it's
    // important that we make sure that we preserve the previous state.

        let fn = prevState => {
            return {
            ...prevState, messages: [...prevState.messages, message]
            }
        };
        this.setState(fn)

        //this.saveSingleMessage(message)
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



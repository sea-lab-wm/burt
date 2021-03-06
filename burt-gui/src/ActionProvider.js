
// import Chatbot from "react-chatbot-kit";

import {createChatBotMessage} from "react-chatbot-kit";

const axios = require('axios')
// ActionProvider starter code
// import sessions from "./session/Session";

class ActionProvider{

    constructor(createChatBotMessage, setStateFunc, createClientMessage,  sessionIdObj) {
        this.createChatBotMessage = createChatBotMessage;
        this.setState = setStateFunc;
        // this.state = stateObj.current
        this.sessionId = sessionIdObj
        //console.log("Constructing action provider ", this.sessionId)

        // this.chatbot = null
    }

    createInitialMessage(){
        const message = createChatBotMessage("Got it. Just to confirm, can you select the screen that is having the" +
            " problem?", {
            widget: "OneScreenOption",
        })
        this.updateChatbotState(message)
    }

    saveSingleMessage(messageObj){

        console.log("Saving single message for ", this.sessionId, ": ", messageObj)
        //calling the API
        axios
            .post('http://localhost:8081/saveSingleMessage', {
                sessionId: this.sessionId,
                messages : [messageObj]
            })
            .then(res => {
                console.log(`Done`)
            })
            .catch(error => {
                console.error(error)
            })
    }

    greet() {
        const greetingMessage = this.createChatBotMessage("Hi, friend.")
        this.updateChatbotState(greetingMessage)
    }

    handleSave(){
        const msg = this.createChatBotMessage("I am saving...")
        this.updateChatbotState(msg)
    }

    handleOneScreenOption = () => {
        const message = this.createChatBotMessage(
            "Okay. please choose more than one screen",
            {
                widget: "MultipleScreensOptions",
            }
        );

        this.updateChatbotState(message);
    };
    handleNoneOfAboveScreensOption = () => {
        const message = this.createChatBotMessage(
            "Okay. I got it",

        );

        this.updateChatbotState(message);
    };
    handleMultipleScreensOption = () => {
        const message = this.createChatBotMessage(
            "Okay. you choose more than one screen",

        );

        this.updateChatbotState(message);
    };

    updateChatbotState(message) {

        console.log("Updating chatbot state from action provider")

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
}
export default ActionProvider;



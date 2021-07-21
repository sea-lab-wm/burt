import ApiClient from "./ApiClient";
import SessionManager from "./SessionManager";
import processResponse from "./ServerResponseProcessor";

class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
    }

    //this is only for text messages, not for pic selection
    parse(messageObj) {

        const userMsg = messageObj.message
        // console.log("This is the user message: ")
        // console.log(userMsg)

        const responsePromise = ApiClient.processUserMessage(userMsg, [])

        processResponse(responsePromise, this.actionProvider)
    }


}

export default MessageParser;


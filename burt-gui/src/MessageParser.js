import App from "./App"

// MessageParser starter code
class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
    }

    parse(messageObj) {

        // App.save

        // this.actionProvider.saveMessages(this.state);
        this.actionProvider.saveSingleMessage(messageObj)

        const message = messageObj.message

        const lowerCaseMessage = message.toLowerCase()

        if (lowerCaseMessage.includes("hello")) {
            this.actionProvider.greet()
        }
        if (lowerCaseMessage.includes("javascript")) {
            this.actionProvider.handleJavascriptList();
        }
        if(lowerCaseMessage.includes("save")){
            this.actionProvider.handleSave();
        }
    }
}

export default MessageParser;


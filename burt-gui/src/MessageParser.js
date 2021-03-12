import ApiClient from "./ApiClient";

class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
    }

    parse(messageObj) {

        const userMsg = messageObj.message

        const response = ApiClient.processUserMessage(userMsg)

        const message = this.actionProvider.createChatBotMessage(
            response.message

        );

        this.actionProvider.updateChatbotState(message)

    }
}

export default MessageParser;


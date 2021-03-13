import ApiClient from "./ApiClient";

class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
    }

    parse(messageObj) {

        const userMsg = messageObj.message

        const responsePromise = ApiClient.processUserMessage(userMsg)

        responsePromise.then(response => {

            try {
                if (response.code === -1)
                    throw response.data.message.message

                console.log(response)

                const message = this.actionProvider.createChatBotMessage(
                    response.data.message.message
                );

                this.actionProvider.updateChatbotState(message)
            } catch (errorMsg) {
                console.error(`There was an error from the server: ${errorMsg}`);
            }
        }).catch(error => {
            console.error(`There was an error: ${error}`);
        })



    }


}

export default MessageParser;


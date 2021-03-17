import ApiClient from "./ApiClient";
import SessionManager from "./SessionManager";

class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
    }

    //this is only for text messages, not for pic selection
    parse(messageObj) {

        const userMsg = messageObj.message


        console.log("This is the user message: ")
        console.log(userMsg)

        const responsePromise = ApiClient.processUserMessage(userMsg, [])

        responsePromise.then(response => {

            try {
                console.log("Response from the server: ")
                console.log(response)

                if (response.data.code === -1)
                    throw response.data.message.messageObj.message

                if(response.data.code === 100){

                    const message = this.actionProvider.createChatBotMessage(
                        response.data.message.messageObj.message
                    );

                    this.actionProvider.updateChatbotState(message)

                    SessionManager.endSession();

                    window.location.reload(false)
                    return
                }

                if(response.data.message.messageObj.widget){

                    const paths =  response.data.message.paths
                    const values = response.data.message.values

                    console.log("Processing a widget msg: ")
                    console.log( response.data.message.messageObj)

                    this.actionProvider.updateChatbotState2(response.data.message.messageObj, paths, values)

                } else {

                    const message = this.actionProvider.createChatBotMessage(
                        response.data.message.messageObj.message
                    );

                    this.actionProvider.updateChatbotState(message)
                }
            } catch (errorMsg) {
                console.error(`There was an error from the server: ${errorMsg}`);
            }
        }).catch(error => {
            console.error(`There was an error: ${error}`);
        })

    }


}

export default MessageParser;


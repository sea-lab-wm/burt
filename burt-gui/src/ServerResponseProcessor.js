import SessionManager from "./SessionManager";
import {END_CONVERSATION_CODE, ERROR_CODE} from "./App";

const processResponse = (responsePromise, actionProvider) => {
    function processResponse2(httpReponse, lastMsgId) {
        try {
            console.log("Response from the server: ")
            console.log(httpReponse)

            actionProvider.removeMsg(lastMsgId)

            let conversationResponse = httpReponse.data;
            if (conversationResponse.code === ERROR_CODE)
                throw conversationResponse.messages[0].messageObj.message

            if (conversationResponse.code === END_CONVERSATION_CODE) {

                const message = actionProvider.createChatBotMessage(
                    conversationResponse.messages[0].messageObj.message
                );

                actionProvider.updateChatbotState(message)

                SessionManager.endSession();

                setTimeout(() => {
                    window.location.reload(false);
                }, 3000);

                return
            }

            //-------------------------------------------

            let chatBotMsgs = conversationResponse.messages;

            for (const chatBotMsg of chatBotMsgs) {

                if (chatBotMsg.messageObj.widget) {

                    const valuesData = chatBotMsg.values
                    const multiple = chatBotMsg.multiple;

                    console.log("Processing a widget msg: ")
                    console.log(chatBotMsg)

                    const message = actionProvider.createChatBotMessage(
                        chatBotMsg.messageObj.message, {
                            allValues: valuesData,
                            multiple: multiple,
                            selectedValues: null,
                            widget: chatBotMsg.messageObj.widget,
                            generatedReport: chatBotMsg.generatedReport,
                        }
                    );

                    actionProvider.updateChatbotState(message)
                    // actionProvider.updateChatbotState2(conversationResponse.message.messageObj, paths, values)

                } else {

                    const message = actionProvider.createChatBotMessage(
                        chatBotMsg.messageObj.message
                    );

                    actionProvider.updateChatbotState(message)
                }

            }
        } catch (errorMsg) {
            console.error(`There was an error processing the message or response: ${errorMsg}`);
            console.trace()
        }
    }

    //----------------------------------------------

    //we show the dots showing the chatbot is processing
    //15 mins limit before the message is deleted
    let tempBotMsg = actionProvider.createChatBotMsg("",{delay: 15*60000});
    actionProvider.updateChatbotState(tempBotMsg)

    /*setTimeout(() => {
        actionProvider.removeMsg(tempBotMsg.id)
    }, 10000)*/

    responsePromise.then(response => {
        processResponse2(response, tempBotMsg.id)

    }).catch(error => {
        console.error(`There was an unexpected error: ${error}`);
    })

}

export default processResponse;
import React from "react";
import SessionManager from "./SessionManager";
import {END_CONVERSATION_CODE, ERROR_CODE} from "./App";
import updateStepHistory from "./UpdateStepsHistory";

const processResponse = (responsePromise, actionProvider, extraFunction) => {
    function processResponse2(httpReponse, lastMsgId) {
        try {
            console.log("Response from the server: ")
            console.log(httpReponse)

            actionProvider.removeMsg(lastMsgId)

            let conversationResponse = httpReponse.data;

            updateStepHistory(actionProvider);

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
                }, 2000);

                return
            }

            //-------------------------------------------

            let chatBotMsgs = conversationResponse.messages;
            let nextIntents = conversationResponse.nextIntents;
            actionProvider.setTipState(prevState => ({
                tipStateArray: [...prevState.tipStateArray, nextIntents[0]]
            }))

            for (const chatBotMsg of chatBotMsgs) {

                if (chatBotMsg.messageObj.widget) {

                    const valuesData = chatBotMsg.values
                    const multiple = chatBotMsg.multiple;

                    console.log("Processing a widget msg: ")
                    console.log(chatBotMsg)

                    let plainMsg = chatBotMsg.messageObj.message;

                    const message = actionProvider.createChatBotMessage(
                        plainMsg, {
                            allValues: valuesData,
                            multiple: multiple,
                            selectedValues: null,
                            widget: chatBotMsg.messageObj.widget,
                            generatedReport: chatBotMsg.generatedReport,
                        }
                    );

                    actionProvider.updateChatbotState(message)

                } else {

                    let plainMsg = chatBotMsg.messageObj.message;

                    const message = actionProvider.createChatBotMessage(
                        plainMsg
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
    let tempBotMsg = actionProvider.createChatBotMsg("", {delay: 15 * 60000});
    actionProvider.updateChatbotState(tempBotMsg)

    /*setTimeout(() => {
        actionProvider.removeMsg(tempBotMsg.id)
    }, 10000)*/

    responsePromise.then(response => {
        processResponse2(response, tempBotMsg.id)
        if (extraFunction !== null && extraFunction !== undefined)
            extraFunction()
    }).catch(error => {
        actionProvider.removeMsg(tempBotMsg.id)
        alert("There was an unexpected error, please try again in few moments or refresh the page.")
        console.error(`There was an unexpected error: ${error}`);
    })

}

export default processResponse;
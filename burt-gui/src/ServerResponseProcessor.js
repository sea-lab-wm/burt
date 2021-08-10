import SessionManager from "./SessionManager";
import {END_CONVERSATION_CODE, ERROR_CODE, REPORT_NO_INFO_CODE, SUCCESS_CODE} from "./App";
import ApiClient from "./ApiClient";
import config from "./config";

const processResponse = (responsePromise, actionProvider) => {
    function processResponse2(httpReponse, lastMsgId) {
        try {
            console.log("Response from the server: ")
            console.log(httpReponse)

            actionProvider.removeMsg(lastMsgId)

            let conversationResponse = httpReponse.data;
            // ask updated steps from server
            // getStepsHistory();

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
                    // actionProvider.updateChatbotState2(conversationResponse.message.messageObj, paths, values)

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
    let tempBotMsg = actionProvider.createChatBotMsg("",{delay: 15*60000});
    actionProvider.updateChatbotState(tempBotMsg)

    /*setTimeout(() => {
        actionProvider.removeMsg(tempBotMsg.id)
    }, 10000)*/

    responsePromise.then(response => {
        processResponse2(response, tempBotMsg.id)

    }).catch(error => {
        actionProvider.removeMsg(tempBotMsg.id)
        alert("There was an unexpected error, please try again in few moments or refresh the page.")
        console.error(`There was an unexpected error: ${error}`);
    })


    function getStepsHistory(){
        const responsePromise = ApiClient.processStepsHistory();
        responsePromise.then(response => {

            let conversationResponse = response.data;
            let chatbotMsgs = conversationResponse.messages;
            let chatbotMsg = chatbotMsgs[0];

            if (conversationResponse.code === SUCCESS_CODE) {
                let stepsHistory = chatbotMsg.values;
                // console.log(link);
                // window.open(config.serverEndpoint + "/" + link, "_blank");







            } else if (conversationResponse.code === ERROR_CODE) {
                window.alert(chatbotMsg.messageObj.message);
            } else {
                window.alert("There was an unexpected error");
            }
        }).catch(error => {
            console.error(`There was an unexpected error: ${error}`);
        })


    }


}

export default processResponse;
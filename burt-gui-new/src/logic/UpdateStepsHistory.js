import ApiClient from "./ApiClient";
import {ERROR_CODE, SUCCESS_CODE} from "../App";

function loadStepHistory(actionProvider){
    const responsePromise = ApiClient.processStepsHistory();
    responsePromise.then(response => {

        let conversationResponse = response.data;
        let chatbotMsgs = conversationResponse.messages;
        let chatbotMsg = chatbotMsgs[0];

        if (conversationResponse.code === SUCCESS_CODE) {
            let stepsHistory = chatbotMsg.values;
            if(stepsHistory != null)
                actionProvider.updateAllStepHistory(stepsHistory);
        } else if (conversationResponse.code === ERROR_CODE) {
            window.alert(chatbotMsg.messageObj.message);
        } else {
            window.alert("There was an unexpected error");
        }
    }).catch(error => {
        console.error(`There was an unexpected error: ${error}`);
    })

}

export default loadStepHistory;
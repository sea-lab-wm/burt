import SessionManager from "./SessionManager";

const ERROR_CODE = -1;
const END_CONVERSATION_CODE = 100;

const processResponse = (responsePromise, actionProvider) =>{
    function processResponse2(response) {
        try {
            console.log("Response from the server: ")
            console.log(response)

            let responseData = response.data;
            if (responseData.code === ERROR_CODE)
                throw responseData.message.messageObj.message

            if (responseData.code === END_CONVERSATION_CODE ) {

                const message = actionProvider.createChatBotMessage(
                    responseData.message.messageObj.message
                );

                actionProvider.updateChatbotState(message)

                SessionManager.endSession();

                window.location.reload(false)
                return
            }

            if (responseData.message.messageObj.widget) {

                const valuesData = responseData.message.values
                const multiple =  responseData.message.multiple;

                console.log("Processing a widget msg: ")
                console.log(responseData.message)

                const message = actionProvider.createChatBotMessage(
                    responseData.message.messageObj.message, {
                        allValues: valuesData,
                        multiple: multiple,
                        selectedValues: null,
                        widget: responseData.message.messageObj.widget,
                    }
                );

                actionProvider.updateChatbotState(message)
                // actionProvider.updateChatbotState2(responseData.message.messageObj, paths, values)

            } else {

                const message =actionProvider.createChatBotMessage(
                    responseData.message.messageObj.message
                );

                actionProvider.updateChatbotState(message)
            }
        } catch (errorMsg) {
            console.error(`There was an error processing the message or response: ${errorMsg}`);
            console.trace()
        }
    }

    responsePromise.then(response => {
        processResponse2(response)

    }).catch(error => {
        console.error(`There was an unexpected error: ${error}`);
    })

}

export default processResponse;
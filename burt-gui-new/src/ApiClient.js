import config from "./config";
import SessionManager from "./SessionManager";

const axios = require('axios')

class ApiClient {

    static startConversation() {
        return ApiClient.sendRequestSync(config.startService, null)
    }

    static endConversation() {
        const sessionId = SessionManager.getSessionId();
        const data = {
            sessionId: sessionId,
        }
        return ApiClient.sendRequestSync(config.endService, data)
    }

    /*
     * Receives a message object (for text-based user answers) and selected values (for one of multiple-option user
     *  answers).
     *
     * messageObj should be an object as used in the chatbot framework
     * selectedValues should be an array
     */
    static processUserMessage(messageObj) {
        const sessionId = SessionManager.getSessionId();

        const data = {
            sessionId: sessionId,
            messages: [messageObj]
        }

        return axios.post(config.serverEndpoint + config.processMessageService, data);
    }

    static processStepsHistory(){
        const sessionId = SessionManager.getSessionId();

        const data = {
            sessionId: sessionId,
        }

        return axios
            .post(config.serverEndpoint + config.getStepsHistory, data);
    }

    static processReportPreview() {
        const sessionId = SessionManager.getSessionId();
        const data = {
            sessionId: sessionId,
        }
        return axios
            .post(config.serverEndpoint + config.getBugReportPreview, data);

    }

    static sendRequestAsync(service, data) {
        console.log("Sending request...")

        //calling the API asynchronously
        axios
            .post(config.serverEndpoint + service, data)
            .then(res => {
                return res;
            })
            .catch(error => {
                throw error;
            })
    }

    static sendRequestSync(service, data) {
        let request = new XMLHttpRequest();
        const url = config.serverEndpoint + service;
        request.open('POST', url, false);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

        request.send(data !== null && data !== undefined ? JSON.stringify(data) : data);

        if (request.status === 200) {
            const messagesFromServer = request.responseText;

            if (messagesFromServer !== "") {
                try {
                    return JSON.parse(messagesFromServer)
                } catch (e) {
                    return messagesFromServer
                }
            }
        } else {
            throw `There was an error: ${request.status} - ${request.statusText}`
        }

        return null;
    }

    static saveMessages(messages) {
        const sessionId = SessionManager.getSessionId();
        console.log("Saving messages for ", sessionId, ": ", messages)

        //calling the API asynchronously
        axios.post(config.serverEndpoint + config.saveMessagesService, {
            sessionId: sessionId,
            messages: messages,
        }).then(res => {
                console.log(`Messages were saved successfully`)
            })
            .catch(error => {
                console.error(error)
            })
    }

}

export default ApiClient;
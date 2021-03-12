import config from "./config";
import SessionManager from "./SessionManager";

const axios = require('axios')

class ApiClient {

    static startConversation() {
        let response = ApiClient.sendRequestSync(config.startService, null);
        return response
    }

    static processUserMessage(userMsg) {
        const sessionId = SessionManager.getSessionId();
        // const data = {
        //     sessionId: sessionId,
        //     messages: messages
        // }
        // setTimeout(function(){ alert("Hello"); }, 3000);
        const response = {
            message: "This is a test"
        }
        return response;
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

}

export default ApiClient;
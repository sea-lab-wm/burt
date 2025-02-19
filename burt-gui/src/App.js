import React from "react";
import './App.css';
import Chatbot from "./components/Chatbot/Chatbot";
import config from './config';
import actionProvider from "./logic/ActionProvider.js";
import messageParser from "./logic/MessageParser.js";
import ApiClient from "./logic/ApiClient";
import SessionManager from "./logic/SessionManager";

function App() {

    let sessionId = null
    try {
        //creating the session
        if (SessionManager.noSession()) {
            sessionId = ApiClient.startConversation()
            SessionManager.setSessionId(sessionId)
        } else {
            sessionId = SessionManager.getSessionId()
        }
    } catch (e) {
        console.error("Unexpected error when creating the session: ", e)
        SessionManager.endSession()
    }

    //--------------------------------

    console.log("Current session id: ", sessionId)

    //--------------------------------

    function loadMessagesSync() {
        // console.log("Fetching messages...")
        let messages = null;

        try {
            let request = new XMLHttpRequest();
            const url = config.serverEndpoint + config.loadMessagesService;
            request.open('POST', url, false);
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

            const requestBody = {
                sessionId: sessionId,
                messages: null
            }

            request.send(JSON.stringify(requestBody));

            if (request.status === 200) {
                const messagesFromServer = request.responseText;

                if (messagesFromServer !== "") {
                    messages = JSON.parse(messagesFromServer)

                }
            } else {
                console.error(`There was an error retrieving the messages: ${request.status} - ${request.statusText}`)
            }
        } catch (e) {
            console.error(`Unexpected error: ${e}`)
        }

        return messages;
    }

    //--------------------------------

    const emptyStringValidator = (input) => {
        return input.trim().length > 0;
    }

    //--------------------------------

    if (sessionId != null && sessionId != undefined) {
        return (
            <Chatbot
                config={config}
                actionProvider={actionProvider}
                messageHistory={loadMessagesSync()}
                messageParser={messageParser}
                sessionId={sessionId}
                validator={emptyStringValidator}
            />
        );


    } else
        return (
            <div>I am sorry, BURT cannot be loaded at this moment. Try loading the page in a few seconds.</div>
        );
}

//--------------------------------

/*
//FIXME: this function does not work with the framework
function loadMessagesAsync(setState) {
    console.log("Fetching messages (async)...")
    const url = config.serverEndpoint + config.loadMessagesService;

    axios
        .post(url, {
            sessionId: sessionId,
            messages : null
        }).then(res => {
        // console.log(res)
        if(res.data !== "") {
            // console.log(res.data)
            const messages = res.data
            console.log("Loading messages (async): ", messages)
            setState((prevState) => ({ ...prevState, messages: messages }));
        }
    })
        .catch(error => {
            console.error(`There was an error retrieving the messages: ${error}`)
        })

}
*/



export default App;
export const ERROR_CODE = -1;
export const SUCCESS_CODE = 0;
export const END_CONVERSATION_CODE = 100;
export const REPORT_NO_INFO_CODE = -2;
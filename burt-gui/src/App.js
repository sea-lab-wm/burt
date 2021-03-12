import React from "react";
import './App.css';
import Chatbot from "react-chatbot-kit";
import config from './config';
import actionProvider from "./ActionProvider.js";
import messageParser from "./MessageParser.js";
import ApiClient from "./ApiClient";
import SessionManager from "./SessionManager";

const axios = require('axios')

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

    function saveMessages(messages) {
        console.log("Saving messages for ", sessionId, ": ", messages)

        //calling the API asynchronously
        axios
            .post(config.serverEndpoint + config.saveMessagesService, {
                sessionId: sessionId,
                messages: messages
            })
            .then(res => {
                console.log(`Messages were saved sucessfully`)
            })
            .catch(error => {
                console.error(error)
            })
    }

    //--------------------------------

    function loadMessagesSync() {
        console.log("Fetching messages...")
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

    if (sessionId != null && sessionId != undefined)
        return (
            <div className="App">
                {
                    <Chatbot
                        config={config}
                        actionProvider={actionProvider}
                        messageHistory={loadMessagesSync()}
                        messageParser={messageParser}
                        sessionId={sessionId}
                        saveMessages={saveMessages}
                    />
                }
            </div>
        );
    else
        return (
            <div>I am sorry, BURT cannot be loaded</div>
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

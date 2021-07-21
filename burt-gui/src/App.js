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
                messages: messages,
            })
            .then(res => {
                console.log(`Messages were saved successfully`)
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

        console.log(messages)

        return messages;
    }

    //--------------------------------

    const emptyStringValidator = (input) => {
        return input.trim().length > 0;
    }

    //--------------------------------

    if (sessionId != null && sessionId != undefined) {
        window.onload = function () {
            let button = document.getElementById('reportPreview');
            button.onclick = function () {

                const responsePromise = ApiClient.processReportPreview();
                responsePromise.then(response => {
                    let conversationResponse = response.data;
                    if (conversationResponse.code === 0) {
                        let chatbotMsgs = conversationResponse.messages;
                        let chatbotMsg = chatbotMsgs[0];
                        let link = chatbotMsg.generatedReport;
                        console.log(link);
                        window.open(config.serverEndpoint + "/" + link, "_blank");
                    } else if (conversationResponse.code === -1) {
                            window.alert("Oops, the bug report preview can't be generated at this moment as more" +
                                " information is needed. Please select an app first.");
                    } else {
                        window.alert("There was an unexpected error");
                    }
                }).catch(error => {
                    console.error(`There was an unexpected error: ${error}`);
                })
            }
        }

        return (
            <div className="App center-screen">
                {
                    <Chatbot
                        config={config}
                        actionProvider={actionProvider}
                        messageHistory={loadMessagesSync()}
                        messageParser={messageParser}
                        sessionId={sessionId}
                        saveMessages={saveMessages}
                        validator={emptyStringValidator}
                    />
                }
            </div>
        );
    } else
        return (
            <div>I am sorry, BURT cannot be loaded. Try loading the page in a few seconds.</div>
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

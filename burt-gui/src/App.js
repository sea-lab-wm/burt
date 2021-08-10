import React from "react";
import './App.css';
import Chatbot from "react-chatbot-kit";
import config from './config';
import actionProvider from "./ActionProvider.js";
import messageParser from "./MessageParser.js";
import ApiClient from "./ApiClient";
import SessionManager from "./SessionManager";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

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
            let previewButton = document.getElementById('reportPreview');
            previewButton.onclick = function () {

                const responsePromise = ApiClient.processReportPreview();
                responsePromise.then(response => {

                    let conversationResponse = response.data;
                    let chatbotMsgs = conversationResponse.messages;
                    let chatbotMsg = chatbotMsgs[0];

                    if (conversationResponse.code === SUCCESS_CODE) {
                        let link = chatbotMsg.generatedReport;
                        console.log(link);
                        window.open(config.serverEndpoint + "/" + link, "_blank");
                    } else if (conversationResponse.code === REPORT_NO_INFO_CODE) {
                        window.alert(chatbotMsg.messageObj.message);
                    } else if (conversationResponse.code === ERROR_CODE) {
                        window.alert(chatbotMsg.messageObj.message);
                    } else {
                        window.alert("There was an unexpected error");
                    }
                }).catch(error => {
                    console.error(`There was an unexpected error: ${error}`);
                })
            }

            //------------------------------------

            let restartButton = document.getElementById('restartConversation');
            restartButton.onclick = function () {
                // let response = window.confirm("Are you sure you want to restart the conversation?");
                //
                // if(!response) return;

                try {
                    const conversationResponse = ApiClient.endConversation();

                    if (conversationResponse === SUCCESS_CODE) {
                        SessionManager.endSession();
                        window.location.reload(false);
                    } else if (conversationResponse === ERROR_CODE) {
                        window.alert("I am sorry, I couldn't restart the conversation." +
                            "Please try again in a few moments.");
                    } else {
                        window.alert("There was an unexpected error");
                    }
                } catch (e) {
                    console.error(`There was an unexpected error: ${e}`);
                }
            }
        }
        if (sessionId != null && sessionId != undefined) {
            window.onload = function () {
                const socket = new SockJS("http://localhost:8081/gs-guide-websocket");
                const stompClient = Stomp.over(socket);
                stompClient.connect({}, function (frame) {
                    console.log(frame);
                    stompClient.subscribe('/stepsHistory/' + sessionId, function (body) {
                        console.log(body);
                    });
                });

            }
        }


        return (
            <div className="container-fluid">
                    <div className="row-fluid">
                        <div className="span6" >
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
                       <div className="span8">
                           <div className="steps-history sidebar-nav">
                               <li class="nav-header"> Steps history</li>
                               <ul class="nav nav-list">

                                   <li className="list-group-item">
                                       <small>
                                           "I click some button"
                                           <a href=""  class="" title=""></a>

                                       </small>
                                        </li>

                               </ul>

                               <ul className="nav nav-list">

                               </ul>


                           </div>
                           <div className="steps-history sidebar-nav">
                               <li className="nav-header"> a few last steps you provided</li>
                               <ul className="screenshots">
                               </ul>


                           </div>

                       </div>
                    </div>

            </div>

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
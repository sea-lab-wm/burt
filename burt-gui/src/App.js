import React, { useState } from "react";
import './App.css';
import Chatbot from "react-chatbot-kit";
import config from './config';
import actionProvider from "./ActionProvider.js";
import messageParser from "./MessageParser.js";
import sessions from "./session/Session";
import Cookies from 'universal-cookie';
import {v4 as uuid} from 'uuid';

//const request = require('request');


const cookies =new Cookies();

let counter = 2;
let sessionId = null

function App() {

    if (cookies.get("userID") === undefined) {
        sessionId = uuid()
        // sessionId = "454545"
        cookies.set('userID', sessionId, {path: '/'});
    }else {
        sessionId = cookies.get("userID")
    }

    console.log("Session id is", sessionId)

    // const [appState, toggleBot] = useState(false);

    //actionProvider.setSession(sessionId)

    function loadMessages(){
        console.log("loading messages...")

        // const msgs = sessions.get(counter);
        // if (typeof msgs !== 'undefined')
        //     console.log("Messages for "+ counter, msgs)
        // else
        //     console.log("Couldn't find msgs for "+ counter)

        //const messagesLS = JSON.parse(localStorage.getItem("chat_messages"));

        // let response = axios
        //     .post('http://localhost:8081/loadMessages', {
        //         sessionId: sessionId,
        //         messages : null
        //     }).then(res => {
        //         console.log(`Done loading`)
        //         if(res.data !== "") {
        //             // console.log(res.data)
        //             let messagesFromServer = res.data;
        //             console.log("Msgs from server", messagesFromServer)
        //             console.log("Msgs from local storage", messagesLS)
        //             // return JSON.parse(datum);
        //             // return messagesFromServer;
        //             return messagesLS;
        //         }
        //         console.log("Returning null!!! warning!")
        //         return null;
        //     })
        //     .catch(error => {
        //         console.error(error)
        //         return null;
        //     })

        var request = new XMLHttpRequest();
        request.open('POST', 'http://localhost:8081/loadMessages', false);  // `false` makes the request synchronous
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

        const req = {
                    sessionId: sessionId,
                    messages : null
                }

        request.send(JSON.stringify(req));

        let response = null;

        if (request.status === 200) {
            const messagesFromServer =request.responseText;

            console.log("Msgs from server", messagesFromServer)
            if(messagesFromServer !== "")
                response = JSON.parse(messagesFromServer)
                //response = messagesFromServer
        }else{
            console.error("There was an error")
        }

        console.log("After post!", response)


        return response;
        //retrieve the messages in a DB
    }

    const chatbot =  <Chatbot
        config={config}
        actionProvider={actionProvider}
        messageHistory={loadMessages()}
        messageParser={messageParser}
        sessionId={sessionId}
        // saveMessages={saveMessages}
    />;

   // actionProvider.setChatbot(chatbot)

    return (
        <div className="App">
            {
                chatbot
            }
            {/*<button onClick={() => toggleBot((prev) => !prev)}>Bot</button>*/}
        </div>
    );
}
//
//
// function App() {
//     return (
//         <div className="App">
//             <header className="App-header">
//                 <Chatbot config={config} actionProvider={ActionProvider} messageParser={MessageParser} />
//             </header>
//         </div>
//     );
// }
export default App;

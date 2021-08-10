import React, {useEffect, useRef, useState} from "react";

import Chat from "../Chat/Chat";

import WidgetRegistry from "../WidgetRegistry/WidgetRegistry";
import ChatbotError from "../ChatbotError/ChatbotError";
import {createChatBotMessage, createClientMessage} from "../Chat/chatUtils";
import {getBotName, getCustomComponents, getCustomStyles, getInitialState, getWidgets, validateProps,} from "./utils";
import StepsPanel from "../Steps/StepsPanel";

const axios = require('axios')

const Chatbot = ({
  actionProvider,
  messageParser,
  config,
  headerText,
  placeholderText,
  saveMessages,
  messageHistory,
  validator,
  sessionId
}) => {
  if (!config || !actionProvider || !messageParser) {
    return (
      <ChatbotError message="I think you forgot to feed me some props. Did you remember to pass a config, a messageparser and an actionprovider?" />
    );
  }

  const propsErrors = validateProps(config, messageParser);

  if (propsErrors.length) {
    const errorMessage = propsErrors.reduce((prev, cur) => {
      prev += cur;
      return prev;
    }, "");

    return <ChatbotError message={errorMessage} />;
  }

  const initialState = getInitialState(config);

  if (messageHistory && Array.isArray(messageHistory)) {
    config.initialMessages = [...messageHistory];
  }

  const [state, setState] = useState({
    messages: [...config.initialMessages],
    ...initialState,
  });
  const messagesRef = useRef(state.messages);

  useEffect(() => {
    messagesRef.current = state.messages;
  });

  useEffect(() => {
    if (messageHistory && Array.isArray(messageHistory)) {
      console.log("Loading messages: ", messageHistory)
      setState((prevState) => ({ ...prevState, messages: messageHistory }));
    }

    //--------------------------------
    //alert the user if she/he wants to leave and save the messages

    const onunload = () => saveMessages(messagesRef.current);
    window.onbeforeunload =  onunload
    window.onunload = onunload

    window.addEventListener("beforeunload", function (e) {
      var confirmationMessage = 'It looks like your conversation has not been saved.';
      confirmationMessage += 'Do you want to continue?';

      (e || window.event).returnValue = confirmationMessage; //Gecko + IE
      return confirmationMessage; //Gecko + Webkit, Safari, Chrome etc.
    });

    //-----------------------------------------

    return () => {
      if (saveMessages && typeof saveMessages === "function") {
        saveMessages(messagesRef.current);
      }
    };
  }, []);

  const customStyles = getCustomStyles(config);
  const customComponents = getCustomComponents(config);
  const botName = getBotName(config);

  const [stepsState, setStepsState] = useState({steps: []});

  const actionProv = new actionProvider(
    createChatBotMessage,
    setState,
    createClientMessage,
    sessionId,
    setStepsState,
  );

    if (stepsState.steps.length === 0) {
        let endPoint = config.serverEndpoint + config.getStepsHistory
        getStepHistory(endPoint, sessionId, setStepsState, actionProv)
    }

  const widgetRegistry = new WidgetRegistry(setState, actionProv);
  const messagePars = new messageParser(actionProv, state);

  const widgets = getWidgets(config);
  widgets.forEach((widget) => widgetRegistry.addWidget(widget))

  console.log("Running a modified version of the framework")

  //--------------


  return (
      <div className="container-fluid App center-screen">
        <div className="row-fluid">
          <div className="span6" >
            <Chat
              state={state}
              setState={setState}
              widgetRegistry={widgetRegistry}
              actionProvider={actionProv}
              messageParser={messagePars}
              customComponents={{ ...customComponents }}
              botName={botName}
              customStyles={{ ...customStyles }}
              headerText={headerText}
              placeholderText={placeholderText}
              validator={validator}
            />
          </div>
          <StepsPanel
              stepsState ={stepsState}
          />
        </div>

      </div>
  );
};

function getStepHistory(endPoint, sessionId, setStepState, actionProvider){
  const data = {
    sessionId: sessionId,
  }
  const responsePromise =  axios.post(endPoint, data);
  responsePromise.then(response => {

    let conversationResponse = response.data;
    let chatbotMsgs = conversationResponse.messages;
    let chatbotMsg = chatbotMsgs[0];

    if (conversationResponse.code === 0) {
      let stepsHistory = chatbotMsg.values;
      if(stepsHistory != null)
        actionProvider.updateAllStepHistory(stepsHistory);
    } else if (conversationResponse.code === -1) {
      window.alert(chatbotMsg.messageObj.message);
    } else {
      window.alert("There was an unexpected error");
    }
  }).catch(error => {
    console.error(`There was an unexpected error: ${error}`);
  })

}

export default Chatbot;

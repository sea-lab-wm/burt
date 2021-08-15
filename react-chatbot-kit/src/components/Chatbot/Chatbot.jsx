import React, {useEffect, useRef, useState} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import Chat from "../Chat/Chat";
import './Chatbot.css'
import WidgetRegistry from "../WidgetRegistry/WidgetRegistry";
import ChatbotError from "../ChatbotError/ChatbotError";
import {createChatBotMessage, createClientMessage, updateStepsHistory} from "../Chat/chatUtils";
import {getBotName, getCustomComponents, getCustomStyles, getInitialState, getWidgets, validateProps,} from "./utils";
import StepsPanel from "../Steps/StepsPanel";
import TipsOptionsPanel from "../TipsOptions/TipsOptionsPanel";

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
  sessionId,
  SessionManager,
  processResponse,
  ApiClient

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
  const [tipState, setTipState] = useState({tipStateArray:[]});


  const actionProv = new actionProvider(
    createChatBotMessage,
    setState,
    createClientMessage,
    sessionId,
    setStepsState,
      setTipState,
  );

  if (stepsState.steps.length === 0) {
      let endPoint = config.serverEndpoint + config.getStepsHistory
      updateStepsHistory(endPoint, sessionId, actionProv)
  }

  const widgetRegistry = new WidgetRegistry(setState, actionProv);
  const messagePars = new messageParser(actionProv, state);

  const widgets = getWidgets(config);
  widgets.forEach((widget) => widgetRegistry.addWidget(widget))

  //--------------

  return (
      <div className="container-fluid">
        <div className="row-fluid">
            <StepsPanel
                config={config}
                stepsState ={stepsState}
                sessionId={sessionId}
                actionProvider={actionProv}
                ApiClient={ApiClient}
                processResponse={processResponse}
                messagesState={state}
                setState={setState}
            />
            <div className="span6">
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
            <TipsOptionsPanel
                SessionManager={SessionManager}
                config={config}
                sessionId={sessionId}
                actionProvider={actionProv}
                processResponse={processResponse}
                messageParser={messagePars}
                tipState={tipState}
            />

        </div>

      </div>
  );
};

export default Chatbot;

import React, { useState, useRef, useEffect } from "react";
import { ConditionallyRender } from "react-util-kit";

import UserChatMessage from "../UserChatMessage/UserChatMessage";
import ChatbotMessage from "../ChatbotMessage/ChatbotMessage";
import ChatBotMessageWithWidget from "../ChatbotMessageWithWidget/ChatbotMessageWithWidget";

import { botMessage, createChatMessage } from "./chatUtils";

import ChatIcon from "../../assets/icons/paper-plane.svg";

import "./Chat.css";
import ChatbotIcon from "../../assets/icons/chatbot-2.svg";

const Chat = ({
  state,
  setState,
  widgetRegistry,
  messageParser,
  customComponents,
  actionProvider,
  botName,
  customStyles,
  headerText,
  placeholderText,
  validator,
}) => {
  const { messages } = state;
  const chatContainerRef = useRef(null);

  const [input, setInputValue] = useState("");

  const scrollIntoView = () => {
    setTimeout(() => {
      // get div element by chatContainerRef.current
      // add animation to make scroll to the bottom slowly
      // chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight - chatContainerRef.current.clientHeight;
      const startTop = chatContainerRef.current.scrollTop;
      const endTop = chatContainerRef.current.scrollHeight - chatContainerRef.current.clientHeight;
      let  scrollAnimationFn=doAnimation(startTop,endTop,300);
      let interval=setInterval(()=>{
        scrollAnimationFn(interval)
      },10)

    }, 50);
  };

  const scrollIntoViewNotToBottom = () => {

    setTimeout(() => {
      setInputValue("");
      // get div element by chatContainerRef.current
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight - 30;
    }, 50);
  };
  function doAnimation(startValue,endValue,duration){
    let dy=0;
    let step=(endValue-startValue)/(duration/10);
    return function(interval){
      dy+=step;
      if(dy>=endValue-startValue){
        clearInterval(interval);
      }
      chatContainerRef.current.scrollTop+=step;
    }
  }

  useEffect(() =>{
    scrollIntoViewNotToBottom();
  }, [messages])

  useEffect(() => {
    scrollIntoView();
  },[input]);


  const showAvatar = (messages, index) => {
    if (index === 0) return true;

    const lastMessage = messages[index - 1];

    if (lastMessage.type === "bot" && !lastMessage.widget) {
      return false;
    }
    return true;
  };

  const renderMessages = () => {
    return messages.map((messageObject, index) => {
      if (!botMessage(messageObject))
        return (
          <UserChatMessage
            message={messageObject.message}
            key={messageObject.id}
            customComponents={customComponents}
          />
        );

      //after this point, we are dealing with a chatbot message

      let withAvatar = true;
      if (messageObject.withAvatar) {
        withAvatar = messageObject.withAvatar;
      }
      /*else {
        withAvatar = showAvatar(messages, index, messageObject.withAvatar);
      }*/

      const chatBotMessageProps = {
        passDownProps: { ...messageObject }, //messageObject.disabled, messageObject.id
        setState,
        state,
        customComponents,
        widgetRegistry,
        messages,
      };


      if (messageObject.widget) {

        // console.log("Re-rendering widget msg: " + messageObject.id+" - " )
        // console.log(chatBotMessageProps)
        return (
          <ChatBotMessageWithWidget
            customStyles={customStyles}
            withAvatar={withAvatar}
            {...chatBotMessageProps}
            key={messageObject.id}
          />
        );
      }

      return (
        <ChatbotMessage
          customStyles={customStyles.botMessageBox}
          key={messageObject.id}
          withAvatar={withAvatar}
          {...chatBotMessageProps.passDownProps}
          customComponents={customComponents}
          messages={messages}
          setState={setState}
        />
      );
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let userMsg = createChatMessage(input, "user");

    if (validator && typeof validator === "function") {
      if (validator(input)) {
        handleValidMessage(userMsg);
        messageParser.parse(userMsg);
      }
    } else {
      handleValidMessage(userMsg);
      messageParser.parse(userMsg);
    }
  };

  const handleValidMessage = (userMsg) => {
    setState((state) => ({
      ...state,
      messages: [...state.messages, userMsg],
    }));
    // scroll down to the bottom when submit some message, because we empty the input here
    scrollIntoViewNotToBottom();
  };

  const customButtonStyle = {};
  if (customStyles && customStyles.chatButton) {
    customButtonStyle.backgroundColor = customStyles.chatButton.backgroundColor;
  }

  let header = `${botName}`;
  if (headerText) {
    header = headerText;
  }

  let placeholder = "Write your message here";
  if (placeholderText) {
    placeholder = placeholderText;
  }

  const inputElement = useRef(null);

  useEffect(() => {
    if (inputElement.current) {
      inputElement.current.focus();
    }
  }, []);
  return (
    <div className="react-chatbot-kit-chat-container App screen-center">
      <div className="react-chatbot-kit-chat-inner-container">
        <ConditionallyRender
          ifTrue={customComponents.header}
          show={
            customComponents.header && customComponents.header(actionProvider)
          }
          elseShow={

            <div className="react-chatbot-kit-chat-header">
              <ChatbotIcon className="react-chatbot-kit-chat-bot-avatar-icon"/>
            {header}
              {/*<button className="bn632-hover bn22 bn-margin-left" id="restartConversation">Restart the conversation</button>*/}
              {/*<button className="bn632-hover bn22" id="reportPreview">View the bug report</button>*/}
            </div>
          }
        />

        <div
          className="react-chatbot-kit-chat-message-container"
          // this div element is referred as chatContainerRef
          ref={chatContainerRef}
        >
          {renderMessages()}
          <div style={{ paddingBottom: "15px" }} />
        </div>

        <div className="react-chatbot-kit-chat-input-container">
          <form
            className="react-chatbot-kit-chat-input-form"
            onSubmit={handleSubmit}
          >
            <input
              className="react-chatbot-kit-chat-input"
              placeholder={placeholder}
              value={input}
              onChange={(e) => setInputValue(e.target.value)}
              autoFocus
              ref={inputElement}
            />
            <button
              className="react-chatbot-kit-chat-btn-send"
              style={customButtonStyle}
            >
              <ChatIcon className="react-chatbot-kit-chat-btn-send-icon" />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Chat;

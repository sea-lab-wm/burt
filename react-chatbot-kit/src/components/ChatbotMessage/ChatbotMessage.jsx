import React, { useEffect, useState } from "react";
import { ConditionallyRender } from "react-util-kit";

import ChatbotMessageAvatar from "./ChatBotMessageAvatar/ChatbotMessageAvatar";
import ChatbotIcon from "../../assets/icons/chatbot-2.svg";
import Loader from "../Loader/Loader";

import "./ChatbotMessage.css";
import { callIfExists } from "../Chat/chatUtils";
import Parser from "html-react-parser";

const ChatbotMessage = ({
  message,
  withAvatar,
  loading,
  messages,
  customComponents,
  setState,
  customStyles,
  delay,
  id,
}) => {
  const [show, toggleShow] = useState(true);

  useEffect(() => {
    const disableLoading = (messages, setState) => {
      let defaultDisableTime = 300;
      if (delay) {
        defaultDisableTime += delay;
      }

      setTimeout(() => {
        const newMessages = [...messages];
        const message = newMessages.find((message) => message.id === id);

        if (!message) return;
        message.loading = false;
        message.delay = undefined;

        setState((state) => {
          const freshMessages = state.messages;
          const messageIdx = freshMessages.findIndex(
            (message) => message.id === id
          );
          freshMessages[messageIdx] = message;

          return { ...state, messages: freshMessages };
        });
      }, defaultDisableTime);
    };

    disableLoading(messages, setState);
  }, [delay, id]);

 /* useEffect(() => {
    if (delay) {
      setTimeout(() => toggleShow(true), delay);
    } else {
      toggleShow(true);
    }
  }, [delay]);*/

  const chatBoxCustomStyles = {};
  const arrowCustomStyles = {};

  if (customStyles) {
    chatBoxCustomStyles.backgroundColor = customStyles.backgroundColor;
    arrowCustomStyles.borderRightColor = customStyles.backgroundColor;
  }

  let parsedMsg = Parser(message)

  return (
    <ConditionallyRender
      ifTrue={show}
      show={
        <div className="react-chatbot-kit-chat-bot-message-container">
          <ConditionallyRender
            ifTrue={withAvatar}
            show={
              <ConditionallyRender
                ifTrue={customComponents.botAvatar}
                show={callIfExists(customComponents.botAvatar)}
                elseShow={
                  <ChatbotIcon className="react-chatbot-kit-chat-bot-avatar-icon"/>
                  // <object type="image/svg+xml" data="../../assets/icons/chatbot-2.svg" style="display:block;width:330px;height:240px" >
                  // <param name="src" value="../../assets/icons/chatbot-2.svg" />
                  // </object>
                }
              />
            }
          />

          <ConditionallyRender
            ifTrue={customComponents.botChatMessage}
            show={callIfExists(customComponents.botChatMessage, {
              parsedMsg,
              loader: <Loader />,
            })}
            elseShow={
              <div
                className="react-chatbot-kit-chat-bot-message"
                style={chatBoxCustomStyles}
              >
                <ConditionallyRender
                  ifTrue={loading}
                  show={<Loader />}
                  elseShow={<span>{parsedMsg}</span>}
                />
                <ConditionallyRender
                  ifTrue={withAvatar}
                  show={
                    <div
                      className="react-chatbot-kit-chat-bot-message-arrow"
                      style={arrowCustomStyles}
                    ></div>
                  }
                />
              </div>
            }
          />
        </div>
      }
    />
  );
};

export default ChatbotMessage;

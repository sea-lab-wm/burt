import React, { Fragment } from "react";

import ChatbotMessage from "../ChatbotMessage/ChatbotMessage";
import { ConditionallyRender } from "react-util-kit";

const ChatbotMessageWithWidget = ({
  passDownProps,
  messages,
  setState,
  scrollIntoView,
  state,
  customComponents,
  customStyles,
  widgetRegistry,
  withAvatar,
}) => {

    console.log("Re-rendering widget msg: " + passDownProps.id+" - " )
    console.log(passDownProps)

  return (
    <Fragment>
      <ChatbotMessage
        {...passDownProps}
        customStyles={customStyles.botMessageBox}
        messages={messages}
        withAvatar={withAvatar}
        setState={setState}
        customComponents={customComponents}
      />
      <ConditionallyRender
        ifTrue={!passDownProps.loading}
        show={widgetRegistry.getWidget(passDownProps.widget, {
            ...passDownProps,
          ...state,
          scrollIntoView,
        })}
      />
    </Fragment>
  );
};

export default ChatbotMessageWithWidget;

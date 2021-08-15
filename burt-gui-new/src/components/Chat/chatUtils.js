import axios from "axios";

export const uniqueId = () => {
  return Math.round(Date.now() * Math.random());
};

export const botMessage = (message) => {
  if (message.type === "bot") {
    return true;
  }
  return false;
};

export const createChatMessage = (message, type) => {
  return {
    message: message,
    type: type,
    id: uniqueId(),
  };
};

export const createChatBotMessage = (message, options) => {
  return {
    ...createChatMessage(message, "bot"),
    ...options,
    loading: true,
  };
};

export const createClientMessage = (message, options) => {
  return {
    ...createChatMessage(message, "user"),
    ...options,
    loading: true,
  };
};

export const callIfExists = (func, ...args) => {
  if (func) {
    return func(...args);
  }
};

export const updateStepsHistory = (endPoint, sessionId, actionProvider) =>{
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

export const updateTips = (endPoint, sessionId, actionProvider) =>{
  const data = {
    sessionId: sessionId,
  }
  const responsePromise =  axios.post(endPoint, data);
  responsePromise.then(response => {

    let conversationResponse = response.data;
    let chatbotMsgs = conversationResponse.messages;
    let chatbotMsg = chatbotMsgs[0];

    if (conversationResponse.code === 0) {
      let tipsValues = chatbotMsg.values;
      let tips =[];
      if(tipsValues != null){
        for (const t of tipsValues){
          tips.push(t.value2)
        }
      }
      let fn = prevState => {
        return {
          ...prevState, tipStateArray: tips
        }
      };
      actionProvider.setTipState(fn)
    } else if (conversationResponse.code === -1) {
      window.alert(chatbotMsg.messageObj.message);
    } else {
      window.alert("There was an unexpected error");
    }
  }).catch(error => {
    console.error(`There was an unexpected error: ${error}`);
  })

}

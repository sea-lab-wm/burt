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

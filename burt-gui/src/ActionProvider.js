// ActionProvider starter code
class ActionProvider{
    constructor(createChatBotMessage, setStateFunc) {
        this.createChatBotMessage = createChatBotMessage;
        this.setState = setStateFunc;

    }

    greet() {
        const greetingMessage = this.createChatBotMessage("Hi, friend.")
        this.updateChatbotState(greetingMessage)
    }

    handleOneScreenOption = () => {
        const message = this.createChatBotMessage(
            "Okay. please choose more than one screen",
            {
                widget: "MultipleScreensOptions",
            }
        );

        this.updateChatbotState(message);
    };
    handleNoneOfAboveScreensOption = () => {
        const message = this.createChatBotMessage(
            "Okay. I got it",

        );

        this.updateChatbotState(message);
    };
    handleMultipleScreensOption = () => {
        const message = this.createChatBotMessage(
            "Okay. you choose more than one screen",

        );

        this.updateChatbotState(message);
    };

    updateChatbotState(message) {

    // NOTE: This function is set in the constructor, and is passed in
    // from the top level Chatbot component. The setState function here
    // actually manipulates the top level state of the Chatbot, so it's
    // important that we make sure that we preserve the previous state.

        this.setState(prevState => ({
            ...prevState, messages: [...prevState.messages, message]
        }))
    }
}
export default ActionProvider;



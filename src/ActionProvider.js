// ActionProvider starter code
class ActionProvider {
    constructor(createChatBotMessage, setStateFunc) {
        this.createChatBotMessage = createChatBotMessage;
        this.setState = setStateFunc;
    }

    greet() {
        const greetingMessage = this.createChatBotMessage("Hi, friend.")
        this.updateChatbotState(greetingMessage)
    }

    handleScreenList = () => {
        const message = this.createChatBotMessage(
            "Okay. Can you please tell me how the app is supposed to work instead?",
            // {
            //     widget: "javascriptLinks",
            // }
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
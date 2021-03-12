class SelectAppAction implements ChatbotAction{
    public static String execute(messageObj messageObject){
        String reponseMsg = messageObject.message
        changeState("WELCOME_GIVEN");
        return String;
    }
}
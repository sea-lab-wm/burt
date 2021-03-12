// MessageParser starter code
const fs = require("fs");


class MessageParser {
    constructor(actionProvider, state) {
        this.actionProvider = actionProvider;
        this.state = state
        this.intents = this.getIntents();
    }
    getIntents(){
        const intents_rawdata = fs.readFileSync("/intents.json", 'utf8');
        return JSON.parse(intents_rawdata);
    }
    parse(messageObj) {

        // App.save

        // this.actionProvider.saveMessages(this.state);
        //this.actionProvider.saveSingleMessage(messageObj)

        const message = messageObj.message

        const lowerCaseMessage = message.toLowerCase()
        // check message intent
        for (let i = 0 ; i < this.intents; i++) {
            if (lowerCaseMessage in this.intents[i].get("training phrases")) {
                let intent = this.intents[i].get("intent");
                if (intent === "AffirmativeAnswer" ){
                    const context = localStorage.getItem("context")
                    if (context === "askForOB"){
                        window["this"]["actionProvider"][context]();
                    }
                    if (context === "confirmOB"){
                        localStorage.setItem("OB","true");
                    }
                }
                window["this"]["actionProvider"][context]();
                let action = this.intents[i].get("action");
                window["this"]["actionProvider"][action]();

                if (this.intents[i].hasOwnProperty("next_question")){
                    let context = this.intents[i].get("next_question");
                    localStorage.setItem("context",context);
                }
            }
        }
        // handle the answers that does have an intent, basically it is an answer to some question
        const context= localStorage.getItem("context")
        if (context === "askForOB"){
            this.actionProvider.confirmOB();
            localStorage.setItem("context","confirmOB");
        }

        window["this"]["actionProvider"][nextQuestion]();
    }
}

export default MessageParser;


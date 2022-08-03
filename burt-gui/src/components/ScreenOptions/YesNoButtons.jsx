import React , { useState, useEffect }  from "react";
import "./AppSelector.css";
import ApiClient from "../../logic/ApiClient";
import processResponse from "../../logic/ServerResponseProcessor";

const OneScreenYesNoButtons = (props) => {

    const [disabled, setDisable] = useState(props.disabled)

    const handleButton = (choice) => {
        let message = props.actionProvider.createChatBotMessage(choice);

        const responsePromise = ApiClient.processUserMessage(message)
        processResponse(responsePromise, props.actionProvider)
        setDisable(true)
    }

    return (
        <div className="center-screen">
            <div className="button-layout">
                <button type="button" className="button" onClick={() => handleButton("yes")}
                        disabled={disabled}>yes
                </button>
                <button type="button" className="button left-margin" onClick={() => handleButton("no")}
                        disabled={disabled}>no
                </button>
            </div>

        </div>
    )

}


export default OneScreenYesNoButtons;
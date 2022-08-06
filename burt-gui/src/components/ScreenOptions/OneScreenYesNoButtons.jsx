import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../logic/ApiClient";
import processResponse from "../../logic/ServerResponseProcessor";
import config from "../../config";

let logos = require.context('../../../../data/app_logos', true);

const OneScreenYesNoButtons = (props) => {


    const [screen, setScreen] = useState({});
    const [disabled, setDisable] = useState(props.disabled)

    const pickImageHandler = (image) => {
        setScreen(image);

    }

    const handleButton = (choice) => {
        let message = props.actionProvider.createChatBotMessage(choice);

        const responsePromise = ApiClient.processUserMessage(message)
        processResponse(responsePromise, props.actionProvider)
        setDisable(true)
    }

    const getImageStyle = (width, height) => {
        return {
            width,
            height,
            objectFit: "cover"
        }
    }
    const dataValues = props.allValues; // only one screenshot
    const selectedValues = props.selectedValues
    const multiple = props.multiple;

    return (
        <div className="center-screen">
            <ImagePicker
                // images={dataValues.map((image, i) => ({src: logos("./" + image.value2).default, text: image.value1, id: image.key}))}
                images={dataValues.map((image, i) => ({src: config.serverEndpoint + image.value2, text: image.value1, id: image.key}))}
                style={getImageStyle(180, 320)}
                selected={selectedValues}
                onPick={pickImageHandler}
                multiple = {multiple}
                disabled={true}
            />
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
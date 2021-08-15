import React, {useState} from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";
import config from "../../config";

const OBScreenSelector = (props) => {

    const [screen, setScreen] = useState({});
    const [disabled, setDisable] = useState(props.disabled)

    const pickImageHandler = (image) => {
        setScreen(image);

    }
    const handleConfirmButton = (choice) => {
        if (screen.length > 0) {
            let selectedValues = screen.map(s => s.id);
            let message = props.actionProvider.createChatBotMessage(choice, {selectedValues: selectedValues});

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)

            const idx = props.messages.findIndex(x => x.id === props.id)
            props.messages[idx].selectedValues = selectedValues
            props.messages[idx].disabled = true
            setDisable(true)
        } else {
            alert("Please select one option")
        }
    }
    const handleNegativeButton = (choice) => {
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
                images={dataValues.map((image, i) => ({
                    src: config.serverEndpoint + image.value2,
                    text: image.value1,
                    id: image.key
                }))}
                style={getImageStyle(180, 320)}
                selected={selectedValues}
                onPick={pickImageHandler}
                multiple={multiple}
                disabled={disabled}
            />
            <div className="button-layout">
                <button type="button" className="button" onClick={() => handleConfirmButton("done")}
                        disabled={disabled}>done
                </button>
                <button type="button" className="button left-margin" onClick={() => handleNegativeButton("none of the above")}
                        disabled={disabled}>none of the above
                </button>
            </div>
        </div>
    )

}


export default OBScreenSelector;
import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";
import config from "../../config";

//let logos = require.context('../../../../data/app_logos', true);

const AppSelector = (props) => {
    //console.log(props.disabled);
    //console.log(props.messages);

    const [screen, setScreen] = useState({});
    const [disabled, setDisable] = useState(props.disabled);


    const pickImageHandler = (image) => {
        setScreen(image);
    }

    const handleConfirmButton =() => {
        console.log( props.messages)
        if (screen.length > 0) {

            setDisable(true);
            let selectedValues = screen.map(s => s.id);
            let message = props.actionProvider.createChatBotMessage(null, {selectedValues: selectedValues , disabled: true});

            const responsePromise = ApiClient.processUserMessage(message);
            processResponse(responsePromise, props.actionProvider);

            const idx = props.messages.findIndex(x => x.id === props.id);
            props.messages[idx].selectedValues = selectedValues;
            props.messages[idx].disabled = true;

            console.log(props.messages)
        }else{
            alert("Please select one option")
        }
    }

    const getImageStyle = (width, height) => {
        return {
            width,
            height,
            objectFit: "cover"
        }
    }


    const dataValues = props.allValues;
    const selectedValues = props.selectedValues
    const multiple = props.multiple

    return (
        <div className="center-screen">
            <ImagePicker
                // images={dataValues.map((image, i) => ({src: logos("./" + image.value2).default, text: image.value1, id: image.key}))}
                images={dataValues.map((image, i) => ({src: config.serverEndpoint + "/app_logos/" + image.value2, text: image.value1, id: image.key}))}
                onPick={pickImageHandler}
                style={getImageStyle(100, 100)}
                selected={selectedValues}
                multiple = {multiple}
                disabled={disabled}

            />
            <button type="button" className="button" onClick={handleConfirmButton} disabled={disabled}>done</button>
        </div>
    )
}

export default AppSelector;
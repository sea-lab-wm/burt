import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";

let logos = require.context('../../../../data/app_logos', true);

const AppSelector = (props) => {

    const [screen, setScreen] = useState({});
    const [disabled, setDisable] = useState(false)

    const pickImageHandler = (image) => {
        setScreen(image);
    }

    const handleConfirmButton =() => {
        // setTimeout(() => {
        if (screen.length > 0) {
            let selectedValues = screen.map(s => s.value);
            let message = props.actionProvider.createChatBotMessage(null, {selectedValues: selectedValues });

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)
            const idx = props.messages.findIndex(x => x.id === props.id)
            props.messages[idx].selectedValues = selectedValues
            setDisable(true)
            // console.log(props.messages)
        }else{
            alert("please select one screenshot!")
        }
        // }, 1000)
    }

    const getImageStyle = (width, height) => {
        return {
            width,
            height,
            objectFit: "cover"
        }
    }

    // console.log("All properties: ")
    // console.log(props)

    const dataValues = props.allValues;
    const selectedValues = props.selectedValues
    const multiple = props.multiple


    return (
        <div>
            <ImagePicker
                //images={imageList.map((image, i) => ({src: image, value: i}))}
                images={dataValues.map((image, i) => ({src: logos("./" + image.value).default, value: image.key}))}
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
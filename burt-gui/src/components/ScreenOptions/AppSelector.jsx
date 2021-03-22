import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";

let logos = require.context('../../../../data/app_logos', true);

const AppSelector = (props) => {

    const [screen, setScreen] = useState({});
    // const [imageChanged, setImageChanged] = useState(false);

    const pickImageHandler = (image) => {
        setScreen(image);
    }

    const handleConfirmButton =() => {

        setTimeout(() => {

            let message = props.actionProvider.createChatBotMessage(null, {selectedValues: [screen.value]});

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)

            const idx = props.messages.findIndex(x => x.id === props.id)
            props.messages[idx].selectedValues = [screen.value]

        }, 1000)
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

    return (
        <div>
            <ImagePicker
                //images={imageList.map((image, i) => ({src: image, value: i}))}
                images={dataValues.map((image, i) => ({src: logos("./" + image.value).default, value: image.key}))}
                onPick={pickImageHandler}
                style={getImageStyle(100, 100)}
                selected={selectedValues}

            />
            <button type="button" className="button" onClick={handleConfirmButton}>done</button>
        </div>
    )
}

export default AppSelector;
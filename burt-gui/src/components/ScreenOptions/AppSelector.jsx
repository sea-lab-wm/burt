import React , { useState, useEffect }  from "react";
import SingleImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";

let logos = require.context('../../../../data/app_logos', true);

const AppSelector = (props) => {

    const [image, setImage] = useState([]);
    // const [imageChanged, setImageChanged] = useState(false);

    // useEffect(() => {
    //
    //         let message = props.actionProvider.createChatBotMessage(null, {selectedValues: [image.value]});
    //
    //         const responsePromise = ApiClient.processUserMessage(message)
    //         processResponse(responsePromise, props.actionProvider)
    //
    //         const idx = props.messages.findIndex(x => x.id === props.id)
    //         props.messages[idx].selectedValues = [image.value]
    //
    // }, [image]);

    const pickImageHandler = (image) => {
        setImage({image});

        console.log("Image selected: ")
        console.log(image.value)

        setTimeout(() => {
            let message = props.actionProvider.createChatBotMessage(null, {selectedValues: [image.value]});

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)

            const idx = props.messages.findIndex(x => x.id === props.id)
            props.messages[idx].selectedValues = [image.value]

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
            <SingleImagePicker
                //images={imageList.map((image, i) => ({src: image, value: i}))}
                images={dataValues.map((image, i) => ({src: logos("./" + image.value).default, value: image.key}))}
                onPick={pickImageHandler}
                style={getImageStyle(100, 100)}
                selected={selectedValues}

            />
            {/*<button type="button" className="button" onClick={nonePickedHandler}>none of above</button>*/}
        </div>
    )
}

export default AppSelector;
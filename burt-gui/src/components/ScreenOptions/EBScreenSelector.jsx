import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";


let logos = require.context('../../../../data/app_logos', true);

const EBScreenSelector = (props) => {

    const [screen, setScreen] = useState({});
    // const [imageChanged, setImageChanged] = useState(false);

    const pickImageHandler = (image) => {
        setScreen(image);

    }
    const handleConfirmButton= () => {

        setTimeout(() => {
            console.log(screen[0].value);
            console.log(screen);

            let message = props.actionProvider.createChatBotMessage("yes", {selectedValues: [screen[0].value]});

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)

            const idx = props.messages.findIndex(x => x.id === props.id)
            props.messages[idx].selectedValues = [screen[0].value]

        }, 1000)
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



    return (
        <div>
            <ImagePicker
                images={dataValues.map((image, i) => ({src: logos("./" + image.value).default, value: image.key}))}
                style={getImageStyle(100, 200)}
                selected={selectedValues}
                onPick={pickImageHandler}
                multiple = {true}
                // onClick={this.props.actionProvider.handleOneScreenOption()}/>;
            />
            <center><button type="button" class="button" onClick={() => handleConfirmButton()}>yes</button></center>


        </div>
    )

}


export default EBScreenSelector;
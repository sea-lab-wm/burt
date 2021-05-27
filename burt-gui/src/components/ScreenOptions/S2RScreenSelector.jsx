import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";


let logos = require.context('../../../../data/app_logos', true);

const S2RScreenSelector = (props) => {

    const [screen, setScreen] = useState({});
    const [disabled, setDisable] = useState(props.disabled)

    const pickImageHandler = (image) => {
        setScreen(image);

    }
    const handleConfirmButton= (choice) => {
        setTimeout(() => {
            if (screen.length > 0){
                let selectedValues = screen.map(s => s.value);
                let message = props.actionProvider.createChatBotMessage(choice, {selectedValues: selectedValues});
                const responsePromise = ApiClient.processUserMessage(message)
                processResponse(responsePromise, props.actionProvider)

                const idx = props.messages.findIndex(x => x.id === props.id)

                props.messages[idx].selectedValues = selectedValues;
                props.messages[idx].disabled = true
                setDisable(true)
            } else {
                alert("please select one screenshot!")
            }
        }, 1000)
    }


    const handleNegativeButton = (choice)=>{
        setTimeout(() => {
            let message = props.actionProvider.createChatBotMessage(choice);

            const responsePromise = ApiClient.processUserMessage(message)
            processResponse(responsePromise, props.actionProvider)
            setDisable(true)

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
    const multiple = props.multiple;

    return (
        <div className="center-screen">
            <ImagePicker
                images={dataValues.map((image, i) => ({src: logos("./" + image.value).default, value: image.key}))}
                style={getImageStyle(150, 300)}
                selected={selectedValues}
                onPick={pickImageHandler}
                multiple = {multiple}
                disabled={disabled}
                // onClick={this.props.actionProvider.handleOneScreenOption()}/>;
            />
            <button type="button" className="button" onClick={() => handleConfirmButton("done")} disabled={disabled}>done</button>
            <button type="button" className="button" onClick={() => handleNegativeButton("none of above")} disabled={disabled}>none of above</button>

        </div>
    )

}


export default S2RScreenSelector;
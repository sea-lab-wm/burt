import React , { useState, useEffect }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";
import ApiClient from "../../ApiClient";
import processResponse from "../../ServerResponseProcessor";
import config from "../../config";

let logos = require.context('../../../../data/app_logos', true);

const EBScreenSelector = (props) => {


    const [screen, setScreen] = useState({});

    const pickImageHandler = (image) => {
        setScreen(image);

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

        </div>
    )

}


export default EBScreenSelector;
import React , { useState }  from "react";
import ImagePicker from './../ImagePicker/ImagePicker'
import "./AppSelector.css";

let logos = require.context('../../app_logos', true);

const AppSelector = (props) =>{

    const [image, setImage] = useState([]);

    const applist = props.app_list
    const appValues = props.app_values

    const pickImageHandler = (image) =>{
        setImage({image});
        console.log("Image selected: ")
        console.log(image)

        //TODO: handle the selected image
    }

    const nonePickedHandler = () =>{
        console.log("None were selected")
    }

    console.log(props)

    const ImageStyle = (width, height) => {
        return {
            width,
            height,
            objectFit: "cover"
        }
    }

    return (
        <div>
            <ImagePicker
                //images={imageList.map((image, i) => ({src: image, value: i}))}
               images={applist.map((image, i) => ({src: logos( "./" + image).default, value: appValues[i]}))}
               onPick={pickImageHandler}
               style={ImageStyle(100 ,100)}
            />
            {/*<button type="button" className="button" onClick={nonePickedHandler}>none of above</button>*/}
        </div>
    )
}

export default AppSelector;
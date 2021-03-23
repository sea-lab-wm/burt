import React , { useState, useEffect }  from "react";
import "./EBScreen.css";



let logos = require.context('../../../../data/app_logos', true);

const EBScreenSelector = (props) => {

    const dataValues = props.allValues; // only one screenshot
    console.log(dataValues);


    return (
        <div>
            <img className="screen" src = {logos("./" + dataValues[0].value).default} />
        </div>
    )

}


export default EBScreenSelector;
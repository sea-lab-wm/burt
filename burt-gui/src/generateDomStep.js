import React from "react";

function generateDomStep(props){
    const step = props.step;
    const stringElement = step.key;
    const originalElement =step.value1;
    const screenshotPath =step.value2;
    return(
           <div> {stringElement}</div>
    )



}


export default generateDomStep;
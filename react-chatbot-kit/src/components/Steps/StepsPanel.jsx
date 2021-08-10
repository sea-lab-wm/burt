import React from "react";

const StepsPanel = ({
                        stepsState
                    }) => {

    function renderSteps() {
        return stepsState.steps.map((step) => {

                let key = step.key;
                let stepDescription = step.value1;
                let stepImage = step.value2;

                return <li key={Math.random()} className="list-group-item">{stepDescription}</li>
            }
        )
    }

    return (
        <div className="span8">
            <div className="steps-history sidebar-nav" id="stepsHistoryPanel">
                <li className="nav-header"> Steps history</li>
                <ul className="nav nav-list">
                    {renderSteps()}
                </ul>

            </div>
            <div className="steps-history sidebar-nav">
                <li className="nav-header">Last three steps</li>
                <ul className="screenshots">
                </ul>
            </div>

        </div>
    )

}


export default StepsPanel;
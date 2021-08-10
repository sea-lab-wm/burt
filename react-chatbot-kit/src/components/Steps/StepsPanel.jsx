import React , { useState, useEffect }  from "react";

const StepsPanel = ({
                  actionProvider,
                        stepsState
              }) => {

    function renderSteps(){
        const listItems = stepsState.steps.map((step) =>
            <li key={Math.random()} className="list-group-item">{step}</li>
        );
      return listItems
    }

    return (
        <div className="span8">
            <div className="steps-history sidebar-nav" id="stepsHistoryPanel">
                <li className="nav-header"> Steps history</li>
                <ul className="nav nav-list" >
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
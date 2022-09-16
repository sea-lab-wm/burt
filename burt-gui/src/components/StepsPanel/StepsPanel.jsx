import React, {useEffect, useRef} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './StepsPanel.css';
import Modal from 'react-modal';
import {ConditionallyRender} from "react-util-kit";
import {updateStepsHistory} from "../Chat/chatUtils";
import processResponse from "../../logic/ServerResponseProcessor";
import ApiClient from "../../logic/ApiClient";

import  {ReactComponent as StepsIcon} from "../../assets/icons/list.svg"
import  {ReactComponent as LastStepsIcon} from "../../assets/icons/phone.svg"
import  {ReactComponent as DeleteStepIcon} from "../../assets/icons/delete_step_icon.svg"
import  {ReactComponent as ShowScreenshotStepIcon1} from "../../assets/icons/show_screenshot_step_icon1.svg"
import  {ReactComponent as ShowScreenshotStepIcon2} from "../../assets/icons/show_screenshot_step_icon2.svg"
import StepComponent from "./StepComponent";


const StepsPanel = ({
                        config,
                        stepsState,
                        actionProvider,
                        sessionId,
                        messagesState,
                        setState,
                        setStepsState
                    }) => {

    const stepsContainer = useRef(null);

    useEffect(()=>{
        stepsContainer.current.scrollTop = stepsContainer.current.scrollHeight
    })

    function renderSteps() {

        return stepsState.steps.map((step, index) => {

            let stepNumber = index + 1
            const isLastStep = stepNumber === stepsState.steps.length && stepsState.steps.length !== 1

            return <StepComponent
                key={stepNumber}
                step={step}
                index={index}
                actionProvider={actionProvider}
                config={config}
                isLastStep={isLastStep}
                stepsSize={stepsState.steps.size}
                messagesState={messagesState}
                setStateMsgs={setState}
                sessionId={sessionId}
                stepsState={stepsState}
                setStepsState={setStepsState}
            />
            }
        )
    }

    function renderScreenshots() {
        let lastThreeSteps;
        const numOfSteps = stepsState.steps.length;

        if (stepsState.steps.length >= 3) {
            lastThreeSteps = stepsState.steps.slice(-3)
        } else {
            lastThreeSteps = stepsState.steps
        }
        const subLen = lastThreeSteps.length;
        return lastThreeSteps.map((step, index) => {
            let stepImage = step.value2;
            let stepIndex = numOfSteps - subLen + index + 1;
            return <div key={index} className="screenshot-display">
                <img className="screenshot" src={stepImage} alt=""/>
                <div className="screen-index">{stepIndex}</div>
            </div>
        })
    }


    return (
        <div className="span-steps App screen-center">
            <div className="steps-history" id="stepsHistoryPanel">
                <div className="subpanel-header">
                    <StepsIcon width="20px" height="20px"/>
                    &nbsp;
                    Reported steps
                </div>
                <div ref={stepsContainer} className="subpanel-list2">
                    {renderSteps()}
                </div>

            </div>
            <div className="last-steps">
                <div className="subpanel-header">
                    <LastStepsIcon width="25px" height="25px"/>
                    &nbsp;
                    Last three reported steps
                </div>
                <div className="screenshots">
                    {renderScreenshots()}
                </div>
            </div>
        </div>
    )
}

export default StepsPanel;
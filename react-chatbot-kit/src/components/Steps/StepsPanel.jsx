import React, {useEffect, useRef} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Steps.css';
import Modal from 'react-modal';
import ChatbotIcon from "../../assets/icons/chatbot-2.svg";
import {ConditionallyRender} from "react-util-kit";
import {updateStepsHistory} from "../Chat/chatUtils";

const axios = require('axios')
const customStyles = {
    content: {
        top: '50%',
        left: '50%',
        right: 'auto',
        bottom: 'auto',
        marginRight: '-50%',
        transform: 'translate(-50%, -50%)',
        borderRadius: '5px',
    },
}
Modal.setAppElement('#root');

const StepsPanel = ({
                        config,
                        stepsState,
                        actionProvider,
                        sessionId,
                        ApiClient,
                        processResponse,
                        messagesState,
                        setState
                    }) => {

    const stepsContainer = useRef(null);

    const [modalIsOpens, setIsOpens] = React.useState(Array(stepsState.steps.size).fill(false));
    const setIsOpen = (i, v) => {
        setIsOpens(Object.assign([...modalIsOpens], {[i]: v}));
    };

    useEffect(()=>{
        stepsContainer.current.scrollTop = stepsContainer.current.scrollHeight
    })

    function renderSteps() {

        function getFullDescription(ind, stepDescription) {
            return ind + ". " + stepDescription;
        }

        function getCroppedDescription(ind, stepDescription) {
            let desc = getFullDescription(ind, stepDescription);
            let MAX_LENGTH = 45;
            if(desc.length> MAX_LENGTH){
                desc = desc.substring(0, MAX_LENGTH) + "..."
            }
            return desc;
        }

        return stepsState.steps.map((step, index) => {

            let stepDescription = step.value1;
            let stepImage = config.serverEndpoint + step.value2;
            let stepNumber = index + 1
            const isLastStep = stepNumber === stepsState.steps.length && stepsState.steps.length !== 1
            let fullStepDescription = getFullDescription(stepNumber, stepDescription);
            let croppedStepDescription = getCroppedDescription(stepNumber, stepDescription);

            function openModal(e) {
                window.onbeforeunload = null;
                e.preventDefault();
                setIsOpen(index, true);
            }

            // function afterOpenModal() {
            //     // references are now sync'd and can be accessed.
            //     subtitle.style.color = '#f00';
            // }

            function closeModal(e) {
                e.stopPropagation();
                setIsOpen(index, false);
            }

            function deleteLastStep() {

                //disable all prior widget messages/gui components
                let allMsgs = messagesState.messages
                for (let i = 0; i < allMsgs.length ; i++) {
                    allMsgs[i].disabled = true
                }

                let fn = prevState => {
                    return {
                        ...prevState, messages: allMsgs
                    }
                };
                setState(fn)

                console.log("Messages after disabling them")
                console.log(allMsgs)

                //--------------------------

                let message = actionProvider.createChatBotMessage("Delete step x");
                const responsePromise = ApiClient.processUserMessage(message, [])
                processResponse(responsePromise, actionProvider, () => {
                    let endPoint = config.serverEndpoint + config.getStepsHistory
                    updateStepsHistory(endPoint, sessionId, actionProvider)
                })
            }

            return <li key={stepNumber} className="list-group-item">
                <small>
                    <span title={fullStepDescription}>{croppedStepDescription}</span>
                    <a href={stepImage}  title={"See a screenshot of this step"} onClick={openModal}>
                        <Modal
                            isOpen={modalIsOpens[index]}
                            // onAfterOpen={afterOpenModal}
                            onRequestClose={closeModal}
                            style={customStyles}
                            contentLabel="Example Modal"
                            onHide={closeModal}
                            backdrop="static"
                            keyboard={false}
                        >
                            <div className="popup-display">
                                <div className="popup-title" title={fullStepDescription}>{fullStepDescription}</div>
                                <img height="533px" width="300px" src={stepImage}  alt={fullStepDescription}/>
                                <button onClick={closeModal}>close</button>
                            </div>
                        </Modal>


                        {/*-------------------------*/}
                    <span className="label label-left label-info">
                      <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                           className="bi bi-file-earmark-image" viewBox="0 0 16 16" >
                        <path d="M6.502 7a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3z"/>
                        <path
                          d="M14 14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5V14zM4 1a1 1 0 0 0-1 1v10l2.224-2.224a.5.5 0 0 1 .61-.075L8 11l2.157-3.02a.5.5 0 0 1 .76-.063L13 10V4.5h-2A1.5 1.5 0 0 1 9.5 3V1H4z"/>
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                             className="bi bi-zoom-in" viewBox="0 0 16 16">
                          <path fillRule="evenodd"
                                d="M6.5 12a5.5 5.5 0 1 0 0-11 5.5 5.5 0 0 0 0 11zM13 6.5a6.5 6.5 0 1 1-13 0 6.5 6.5 0 0 1 13 0z"/>
                          <path
                              d="M10.344 11.742c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1 6.538 6.538 0 0 1-1.398 1.4z"/>
                          <path fillRule="evenodd"
                                d="M6.5 3a.5.5 0 0 1 .5.5V6h2.5a.5.5 0 0 1 0 1H7v2.5a.5.5 0 0 1-1 0V7H3.5a.5.5 0 0 1 0-1H6V3.5a.5.5 0 0 1 .5-.5z"/>
                        </svg>
                     </span>
                    </a>

                    {/*-------------------------*/}
                    <ConditionallyRender
                        ifTrue={isLastStep}
                        show={
                            <a href="#" className="label label-danger" onClick={deleteLastStep} title="Delete this step">
                                <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                                     className="bi bi-x-circle" viewBox="0 0 16 16">
                                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                                    <path
                                        d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                                </svg>
                            </a>
                        }
                    />

                </small>
                </li>

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
            let stepImage = config.serverEndpoint + step.value2;
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
                <li className="nav-header">
                    <img width="20px" height="20px" src="data:image/svg+xml;base64,PHN2ZyBpZD0iQ2FwYV8xIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA1MTIgNTEyIiBoZWlnaHQ9IjUxMiIgdmlld0JveD0iMCAwIDUxMiA1MTIiIHdpZHRoPSI1MTIiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGc+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtMTU4LjIwNSA0OTUuMjJoMjkzLjI2OGwyOC4wNC01My41ODEtMjguMDQtNTMuNjFoLTI5My4yNjh6IiBmaWxsPSIjZmZkNzcyIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtMTU4LjIwNSA0OTUuMjJ2LTEwNy4xOTEtNC4xMDhjMC0yLjg0NC0yLjMyNy01LjE0My01LjE3MS01LjE0M2gtMTE1LjQwNWMtMi44NDQgMC01LjE0MyAyLjI5OC01LjE0MyA1LjE0M3YxMTUuNDA4YzAgMi44MTYgMi4yNyA1LjE0MyA1LjA4NSA1LjE3MWguMDU3IDExNS40MDUuMDU3YzIuODE1LS4wMjkgNS4xMTQtMi4zNTYgNS4xMTQtNS4xNzF2LTQuMTA5eiIgZmlsbD0iI2Y5ZjdmOCIgZmlsbC1ydWxlPSJldmVub2RkIi8+PC9nPjxnPjxwYXRoIGNsaXAtcnVsZT0iZXZlbm9kZCIgZD0ibTM3LjYyOSAzMTguODQ3aDExNS40MDVjMi44NDQgMCA1LjE3MS0yLjMyNyA1LjE3MS01LjE0M3YtNC4xMDgtMTA3LjIyLTQuMDhjMC0yLjg0NC0yLjMyNy01LjE3MS01LjE3MS01LjE3MWgtMTE1LjQwNWMtMi44NDQgMC01LjE0MyAyLjMyNy01LjE0MyA1LjE3MXYxMTUuNDA4YzAgMi44MTYgMi4yOTkgNS4xNDMgNS4xNDMgNS4xNDN6IiBmaWxsPSIjZjlmN2Y4IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtMTU4LjIwNSAyMDIuMzc1djEwNy4yMjFoMjkzLjI2OGwyOC4wNC01My42MS0yOC4wNC01My42MTF6IiBmaWxsPSIjNzVjZWY4IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtMTU4LjIwNSAxMjMuOTQzaDI5My4yNjhsMjguMDQtNTMuNTgyLTI4LjA0LTUzLjYxaC0yOTMuMjY4eiIgZmlsbD0iI2ViNTQ2OCIgZmlsbC1ydWxlPSJldmVub2RkIi8+PC9nPjxnPjxwYXRoIGNsaXAtcnVsZT0iZXZlbm9kZCIgZD0ibTE1OC4yMDUgMTIzLjk0M3YtMTA3LjE5Mi00LjEwOGMwLTIuODQ0LTIuMzI3LTUuMTQzLTUuMTcxLTUuMTQzaC0xMTUuNDA1Yy0yLjg0NCAwLTUuMTQzIDIuMjk4LTUuMTQzIDUuMTQzdjExNS40MDhjMCAyLjg0NCAyLjI5OCA1LjE3MSA1LjE0MyA1LjE3MWgxMTUuNDA1YzIuODQ0IDAgNS4xNzEtMi4zMjcgNS4xNzEtNS4xNzF6IiBmaWxsPSIjZjlmN2Y4IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtMTIxLjcxOSAxNi43NTF2MTA3LjE5MiA0LjEwOGMwIDIuODQ0LTIuMzI3IDUuMTcxLTUuMTcxIDUuMTcxaDM2LjQ4NmMyLjg0NCAwIDUuMTcxLTIuMzI3IDUuMTcxLTUuMTcxdi00LjEwOC0xMDcuMTkyLTQuMTA4YzAtMi44NDQtMi4zMjctNS4xNDMtNS4xNzEtNS4xNDNoLTM2LjQ4NmMyLjg0NCAwIDUuMTcxIDIuMjk4IDUuMTcxIDUuMTQzeiIgZmlsbD0iI2RkZGFlYyIgZmlsbC1ydWxlPSJldmVub2RkIi8+PC9nPjxnPjxwYXRoIGNsaXAtcnVsZT0iZXZlbm9kZCIgZD0ibTE1My4wMzQgMTkzLjEyNGgtMzYuNDg2YzIuODQ0IDAgNS4xNzEgMi4zMjcgNS4xNzEgNS4xNzF2NC4wOCAxMDcuMjIgNC4xMDhjMCAyLjgxNi0yLjMyNyA1LjE0My01LjE3MSA1LjE0M2gzNi40ODZjMi44NDQgMCA1LjE3MS0yLjMyNyA1LjE3MS01LjE0M3YtNC4xMDgtMTA3LjIyLTQuMDhjMC0yLjg0My0yLjMyNy01LjE3MS01LjE3MS01LjE3MXoiIGZpbGw9IiNkZGRhZWMiIGZpbGwtcnVsZT0iZXZlbm9kZCIvPjwvZz48Zz48cGF0aCBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Im0xNTMuMDM0IDM3OC43NzhoLTM2LjQ4NmMyLjg0NCAwIDUuMTcxIDIuMjk4IDUuMTcxIDUuMTQzdjQuMTA4IDEwNy4xOTEgNC4xMDhjMCAyLjgxNi0yLjI5OCA1LjE0My01LjExNCA1LjE3MWgzNi40MjkuMDU3YzIuODE1LS4wMjkgNS4xMTQtMi4zNTYgNS4xMTQtNS4xNzF2LTQuMTA4LTEwNy4xOTEtNC4xMDhjMC0yLjg0NS0yLjMyNy01LjE0My01LjE3MS01LjE0M3oiIGZpbGw9IiNkZGRhZWMiIGZpbGwtcnVsZT0iZXZlbm9kZCIvPjwvZz48Zz48cGF0aCBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Im00MDUuODUxIDE2Ljc1MWM2LjY2NSAxOC4zODcgMTAuNCAzOC44NzIgMTAuNCA2MC40NzcgMCAxNi4zNzYtMi4xMjYgMzIuMDkxLTYuMDYyIDQ2LjcxNWg0MS4yODRsMjguMDQtNTMuNTgxLTI4LjA0LTUzLjYxaC00NS42MjJ6IiBmaWxsPSIjZTUzODRmIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PGc+PHBhdGggY2xpcC1ydWxlPSJldmVub2RkIiBkPSJtNDA1Ljg1MSAyMDIuMzc1YzYuNjY1IDE4LjM4NyAxMC40IDM4Ljg3MiAxMC40IDYwLjUwNSAwIDE2LjM0Ny0yLjEyNiAzMi4wNjMtNi4wNjIgNDYuNzE1aDQxLjI4NGwyOC4wNC01My42MS0yOC4wNC01My42MXoiIGZpbGw9IiM2MGI3ZmYiIGZpbGwtcnVsZT0iZXZlbm9kZCIvPjwvZz48Zz48cGF0aCBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Im00MDUuODUxIDM4OC4wMjljNi42NjUgMTguMzg3IDEwLjQgMzguODcyIDEwLjQgNjAuNDc3IDAgMTYuMzQ3LTIuMTI2IDMyLjA5MS02LjA2MiA0Ni43MTVoNDEuMjg0bDI4LjA0LTUzLjU4MS0yOC4wNC01My42MWgtNDUuNjIyeiIgZmlsbD0iI2ZiYmE1OCIgZmlsbC1ydWxlPSJldmVub2RkIi8+PC9nPjxnPjxwYXRoIGQ9Im0zNy42MjkgMTQwLjcyM2gxMTUuNDA1YzUuODExIDAgMTAuNzA4LTMuOTM3IDEyLjE5Ni05LjI4aDI4Ni4yNDNjMi43OTEgMCA1LjM1MS0xLjU1IDYuNjQ2LTQuMDIybDI4LjA0LTUzLjU4MWMxLjE0LTIuMTc4IDEuMTQtNC43NzUgMC02Ljk1NGwtMjguMDQtNTMuNjFjLTEuMjkzLTIuNDc0LTMuODU0LTQuMDI0LTYuNjQ2LTQuMDI0aC0xMjQuNTEyYy00LjE0MyAwLTcuNSAzLjM1OC03LjUgNy41czMuMzU3IDcuNSA3LjUgNy41aDExOS45NzJsMjQuMTE2IDQ2LjEwOS0yNC4xMTUgNDYuMDgyaC0yODEuMjI5di05Mi4xOTJoMTI2LjE3N2M0LjE0MyAwIDcuNS0zLjM1OCA3LjUtNy41cy0zLjM1Ny03LjUtNy41LTcuNWgtMTI2LjY1M2MtMS40OTEtNS4zMjctNi4zODctOS4yNTEtMTIuMTk1LTkuMjUxaC0xMTUuNDA1Yy02Ljk3MSAwLTEyLjY0MyA1LjY3MS0xMi42NDMgMTIuNjQzdjExNS40MDhjMCA2Ljk4NyA1LjY3MiAxMi42NzIgMTIuNjQzIDEyLjY3MnptMi4zNTctMTI1LjcyM2gxMTAuNzE5djExMC43MjNoLTExMC43MTl6Ii8+PHBhdGggZD0ibTQ1OC4xMTkgMTk4Ljg5OWMtMS4yOTMtMi40NzQtMy44NTQtNC4wMjQtNi42NDYtNC4wMjRoLTI4Ni4yNWMtMS40OTgtNS4zMjgtNi4zODgtOS4yNTEtMTIuMTg5LTkuMjUxaC0xMTUuNDA1Yy02Ljk3MSAwLTEyLjY0MyA1LjY4NS0xMi42NDMgMTIuNjcxdjExNS40MDhjMCA2Ljk3MSA1LjY3MSAxMi42NDMgMTIuNjQzIDEyLjY0M2gxMTUuNDA1YzUuODA4IDAgMTAuNzAzLTMuOTI0IDEyLjE5NC05LjI1MWgyODYuMjQ1YzIuNzkyIDAgNS4zNTItMS41NSA2LjY0Ni00LjAyNGwyOC4wNC01My42MWMxLjE0LTIuMTc3IDEuMTQtNC43NzUgMC02Ljk1MnptLTMwNy40MTQgMTEyLjQ0OGgtMTEwLjcxOXYtMTEwLjcyMmgxMTAuNzE5em0yOTYuMjI3LTkuMjUxaC0yODEuMjI3di05Mi4yMmgyODEuMjI3bDI0LjExNyA0Ni4xMXoiLz48cGF0aCBkPSJtMTAxLjIzNSAzNS4yNTFoLTExLjgwN2MtNC4xNDIgMC03LjUgMy4zNTgtNy41IDcuNXMzLjM1OCA3LjUgNy41IDcuNWg0LjMwOHY0Ny42OWMwIDQuMTQyIDMuMzU4IDcuNSA3LjUgNy41czcuNS0zLjM1OCA3LjUtNy41di01NS4xOWMtLjAwMS00LjE0Mi0zLjM1OC03LjUtNy41MDEtNy41eiIvPjxwYXRoIGQ9Im0xMTAuMzkgMjc2LjQ5OGMtNC42NDYuMDYtOS41NDMuMTAzLTEzLjg4MS4xMjEgMi45Mi0zLjk0IDYuNzM4LTkuMjAyIDExLjY0My0xNi4yMTIgMy42MjEtNS4xNjQgNS45OTMtMTAuMDY3IDcuMDUxLTE0LjU3My4wNjMtLjI3LjExMi0uNTQzLjE0NS0uODE5bC4yNTktMi4xNTVjLjAzNi0uMjk3LjA1NC0uNTk2LjA1NC0uODk1IDAtMTEuOTE0LTkuNzA1LTIxLjYwNi0yMS42MzUtMjEuNjA2LTEwLjMyMiAwLTE5LjI0MSA3LjMzNi0yMS4yMDkgMTcuNDQzLS43OTIgNC4wNjYgMS44NjMgOC4wMDQgNS45MjggOC43OTUgNC4wNjkuNzkzIDguMDA0LTEuODYyIDguNzk1LTUuOTI4LjU5OS0zLjA3NyAzLjMyNy01LjMxMSA2LjQ4Ni01LjMxMSAzLjUzNSAwIDYuNDM0IDIuNzY4IDYuNjI1IDYuMjQxbC0uMTQgMS4xNjRjLS40OTEgMS44NTItMS43IDQuODM4LTQuNjQ0IDkuMDM3LTguNzY2IDEyLjUyOC0xMy45NzEgMTkuMzM0LTE2Ljc2NyAyMi45OTItMy40NjkgNC41MzgtNS4zOCA3LjAzNy00LjE0NSAxMS4xMDUuNzM1IDIuNDE5IDIuNTUzIDQuMjgzIDUuMDMyIDUuMTI4IDEgLjMzMyAxLjg0OS42MTcgMTIuNjM1LjYxNyA0LjI0NCAwIDEwLjAyNi0uMDQ0IDE3Ljk2LS4xNDYgNC4xNDItLjA1MyA3LjQ1Ny0zLjQ1NCA3LjQwMy03LjU5Ni0uMDUzLTQuMTQxLTMuNDQ3LTcuNDg4LTcuNTk1LTcuNDAyeiIvPjxwYXRoIGQ9Im05NS4zNDYgNDA1Ljg2OWMtMTAuMjkxIDAtMTkuMjIgNy4zMzUtMjEuMjMxIDE3LjQ0MS0uODA5IDQuMDYyIDEuODI5IDguMDExIDUuODkxIDguODIgNC4wNjYuODEyIDguMDEyLTEuODI5IDguODItNS44OTEuNjItMy4xMTIgMy4zNjItNS4zNyA2LjUyMS01LjM3IDMuNjQzIDAgNi42MDYgMi45NzcgNi42MDYgNi42MzUgMCAzLjY1OS0yLjk2MyA2LjYzNS02LjYwNiA2LjYzNS00LjE0MiAwLTcuNSAzLjM1OC03LjUgNy41czMuMzU4IDcuNSA3LjUgNy41YzMuNjQzIDAgNi42MDYgMi45NjQgNi42MDYgNi42MDYgMCAzLjY1OS0yLjk2MyA2LjYzNS02LjYwNiA2LjYzNS0zLjI5NCAwLTYuMTE1LTIuNDQxLTYuNTU0LTUuNjItLjA1OC0uNDQyLS4wODEtLjczNi0uMDgxLTEuMDE1IDAtNC4xNDItMy4zNTgtNy41LTcuNS03LjVzLTcuNSAzLjM1OC03LjUgNy41YzAgLjk0NC4wNjMgMS44NTQuMjE0IDMuMDA4IDEuNDY3IDEwLjYxOSAxMC42NzUgMTguNjI3IDIxLjQyMSAxOC42MjcgMTEuOTE0IDAgMjEuNjA2LTkuNzA2IDIxLjYwNi0yMS42MzUgMC01LjM5MS0xLjk4OS0xMC4zMjMtNS4yNjUtMTQuMTEyIDMuMjc2LTMuNzk0IDUuMjY1LTguNzMyIDUuMjY1LTE0LjEzLS4wMDEtMTEuOTI5LTkuNjk0LTIxLjYzNC0yMS42MDctMjEuNjM0eiIvPjxwYXRoIGQ9Im0xOTkuMzE3IDYwLjAyaDQ2LjI1NGM0LjE0MiAwIDcuNS0zLjM1OCA3LjUtNy41cy0zLjM1OC03LjUtNy41LTcuNWgtNDYuMjU0Yy00LjE0MiAwLTcuNSAzLjM1OC03LjUgNy41czMuMzU4IDcuNSA3LjUgNy41eiIvPjxwYXRoIGQ9Im0xOTkuMzE3IDk2Ljg1Mmg5NC4wM2M0LjE0MyAwIDcuNS0zLjM1OCA3LjUtNy41cy0zLjM1Ny03LjUtNy41LTcuNWgtOTQuMDNjLTQuMTQyIDAtNy41IDMuMzU4LTcuNSA3LjVzMy4zNTggNy41IDcuNSA3LjV6Ii8+PHBhdGggZD0ibTE5OS4zMTcgMjQ0LjQzOGg0Ni4yNTRjNC4xNDIgMCA3LjUtMy4zNTggNy41LTcuNXMtMy4zNTgtNy41LTcuNS03LjVoLTQ2LjI1NGMtNC4xNDIgMC03LjUgMy4zNTgtNy41IDcuNXMzLjM1OCA3LjUgNy41IDcuNXoiLz48cGF0aCBkPSJtMjkzLjM0NyAyNjYuMjk4aC05NC4wM2MtNC4xNDIgMC03LjUgMy4zNTgtNy41IDcuNXMzLjM1OCA3LjUgNy41IDcuNWg5NC4wM2M0LjE0MyAwIDcuNS0zLjM1OCA3LjUtNy41cy0zLjM1Ny03LjUtNy41LTcuNXoiLz48cGF0aCBkPSJtMTk5LjMxNyA0MzEuMjRoNDYuMjU0YzQuMTQyIDAgNy41LTMuMzU4IDcuNS03LjVzLTMuMzU4LTcuNS03LjUtNy41aC00Ni4yNTRjLTQuMTQyIDAtNy41IDMuMzU4LTcuNSA3LjVzMy4zNTggNy41IDcuNSA3LjV6Ii8+PHBhdGggZD0ibTE5OS4zMTcgNDY4LjA3Mmg5NC4wM2M0LjE0MyAwIDcuNS0zLjM1OCA3LjUtNy41cy0zLjM1Ny03LjUtNy41LTcuNWgtOTQuMDNjLTQuMTQyIDAtNy41IDMuMzU4LTcuNSA3LjVzMy4zNTggNy41IDcuNSA3LjV6Ii8+PHBhdGggZD0ibTQ1OC4xMTkgMzg0LjU1M2MtMS4yOTQtMi40NzQtMy44NTQtNC4wMjQtNi42NDYtNC4wMjRoLTEyNC41MTJjLTQuMTQzIDAtNy41IDMuMzU4LTcuNSA3LjVzMy4zNTcgNy41IDcuNSA3LjVoMTE5Ljk3MmwyNC4xMTYgNDYuMTA5LTI0LjExNSA0Ni4wODNoLTI4MS4yMjl2LTkyLjE5MWgxMjYuMTc3YzQuMTQzIDAgNy41LTMuMzU4IDcuNS03LjVzLTMuMzU3LTcuNS03LjUtNy41aC0xMjYuNjUzYy0xLjQ5MS01LjMyNy02LjM4Ni05LjI1MS0xMi4xOTQtOS4yNTFoLTExNS40MDZjLTYuOTcxIDAtMTIuNjQzIDUuNjcyLTEyLjY0MyAxMi42NDN2MTE1LjQwOGMwIDYuOTg3IDUuNjcxIDEyLjY3MSAxMi42NDMgMTIuNjcxaDExNS40MDVjNS44MTEgMCAxMC43MDgtMy45MzcgMTIuMTk2LTkuMjhoMjg2LjI0M2MyLjc5MSAwIDUuMzUxLTEuNTUgNi42NDYtNC4wMjJsMjguMDQtNTMuNTgyYzEuMTQtMi4xNzggMS4xNC00Ljc3NiAwLTYuOTU0em0tMzA3LjQxNCAxMTIuNDQ3aC0xMTAuNzE5di0xMTAuNzIzaDExMC43MTl6Ii8+PC9nPjwvZz48L3N2Zz4=" />
                    &nbsp;
                    Reported steps</li>
                <ul ref={stepsContainer} className="nav nav-list">
                    {renderSteps()}
                </ul>

            </div>
            <div className="steps-history">
                <li className="nav-header">
                    <img width="25px" height="25px" src="data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjUxMnB0IiB2aWV3Qm94PSItMTEyIDAgNTEyIDUxMi4wMDEiIHdpZHRoPSI1MTJwdCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cGF0aCBkPSJtMTAgNjEuOTcyNjU2di0xNi42Mjg5MDZjMC0xOS41MTk1MzEgMTUuODI0MjE5LTM1LjM0Mzc1IDM1LjM0Mzc1LTM1LjM0Mzc1aDE5Ni42OTE0MDZjMTkuNTE5NTMyIDAgMzUuMzQzNzUgMTUuODI0MjE5IDM1LjM0Mzc1IDM1LjM0Mzc1djE2LjYyODkwNiIgZmlsbD0iI2I5YjliZCIvPjxwYXRoIGQ9Im0xMzYuMzU5Mzc1IDYxLjk3MjY1NmgxNDEuMDE5NTMxdjM3OC4wMzkwNjNoLTI2Ny4zNzg5MDZ2LTM3OC4wMzkwNjNoMzYuMzU5Mzc1IiBmaWxsPSIjZmZmIi8+PHBhdGggZD0ibTg4LjUzOTA2MiA3MS45Njg3NWMtLjY0ODQzNyAwLTEuMzA4NTkzLS4wNTg1OTQtMS45NDkyMTgtLjE4NzUtLjY0MDYyNS0uMTMyODEyLTEuMjY5NTMyLS4zMjAzMTItMS44Nzg5MDYtLjU3MDMxMi0uNjAxNTYzLS4yNS0xLjE3OTY4OC0uNTYyNS0xLjcyMjY1Ny0uOTIxODc2LS41NDY4NzUtLjM2NzE4Ny0xLjA1ODU5My0uNzc3MzQzLTEuNTE5NTMxLTEuMjM4MjgxLS40NjA5MzgtLjQ3MjY1Ni0uODc4OTA2LS45ODA0NjktMS4yMzgyODEtMS41MTk1MzEtLjM1OTM3NS0uNTUwNzgxLS42NzE4NzUtMS4xMzI4MTItLjkyMTg3NS0xLjczMDQ2OS0uMjUtLjYwMTU2Mi0uNDQ5MjE5LTEuMjMwNDY5LS41NzAzMTMtMS44NzEwOTMtLjEyODkwNi0uNjQ4NDM4LS4xOTkyMTktMS4zMDA3ODItLjE5OTIxOS0xLjk0OTIxOSAwLS42NjAxNTcuMDcwMzEzLTEuMzA4NTk0LjE5OTIxOS0xLjk2MDkzOC4xMjEwOTQtLjY0MDYyNS4zMjAzMTMtMS4yNjk1MzEuNTcwMzEzLTEuODcxMDkzLjI1LS41OTc2NTcuNTYyNS0xLjE3OTY4OC45MjE4NzUtMS43MjY1NjMuMzU5Mzc1LS41NDI5NjkuNzc3MzQzLTEuMDYyNSAxLjIzODI4MS0xLjUyMzQzNy40NjA5MzgtLjQ1NzAzMi45NzI2NTYtLjg3ODkwNyAxLjUxOTUzMS0xLjIzODI4Mi41NDI5NjktLjM1OTM3NSAxLjEyMTA5NC0uNjcxODc1IDEuNzIyNjU3LS45MjE4NzUuNjA5Mzc0LS4yNSAxLjIzODI4MS0uNDM3NSAxLjg3ODkwNi0uNTY2NDA2IDMuMjUtLjY1MjM0NCA2LjY5OTIxOC40MDYyNSA5LjAxOTUzMSAyLjcyNjU2My40NjA5MzcuNDYwOTM3Ljg3ODkwNi45ODA0NjggMS4yMzgyODEgMS41MjM0MzcuMzYzMjgyLjU0Njg3NS42NzE4NzUgMS4xMjg5MDYuOTIxODc1IDEuNzI2NTYzLjI1LjYwMTU2Mi40NDE0MDcgMS4yMzA0NjguNTcwMzEzIDEuODcxMDkzLjEyODkwNi42NTIzNDQuMTk5MjE4IDEuMzAwNzgxLjE5OTIxOCAxLjk2MDkzOCAwIC42NDg0MzctLjA3MDMxMiAxLjMwMDc4MS0uMTk5MjE4IDEuOTQ5MjE5LS4xMjg5MDYuNjQwNjI0LS4zMjAzMTMgMS4yNjk1MzEtLjU3MDMxMyAxLjg3MTA5My0uMjUuNTk3NjU3LS41NTg1OTMgMS4xNzk2ODgtLjkyMTg3NSAxLjczMDQ2OS0uMzU5Mzc1LjUzOTA2Mi0uNzc3MzQ0IDEuMDQ2ODc1LTEuMjM4MjgxIDEuNTE5NTMxLTEuODU5Mzc1IDEuODU5Mzc1LTQuNDQxNDA2IDIuOTE3OTY5LTcuMDcwMzEzIDIuOTE3OTY5em0wIDAiLz48cGF0aCBkPSJtMjc3LjM3ODkwNiA0NDAuMDExNzE5djI2LjY0NDUzMWMwIDE5LjUxOTUzMS0xNS44MjQyMTggMzUuMzQzNzUtMzUuMzQzNzUgMzUuMzQzNzVoLTE5Ni42OTE0MDZjLTE5LjUxOTUzMSAwLTM1LjM0Mzc1LTE1LjgyNDIxOS0zNS4zNDM3NS0zNS4zNDM3NXYtMjYuNjQ0NTMxeiIgZmlsbD0iIzhhOGFhMCIvPjxwYXRoIGQ9Im0yNDIuMDM1MTU2IDBoLTE5Ni42OTE0MDZjLTI1IDAtNDUuMzQzNzUgMjAuMzM5ODQ0LTQ1LjM0Mzc1IDQ1LjM0Mzc1djQyMS4zMTI1YzAgMjUuMDAzOTA2IDIwLjM0Mzc1IDQ1LjM0Mzc1IDQ1LjM0Mzc1IDQ1LjM0Mzc1aDE5Ni42OTE0MDZjMjUgMCA0NS4zNDM3NS0yMC4zMzk4NDQgNDUuMzQzNzUtNDUuMzQzNzV2LTQyMS4zMTI1YzAtMjUuMDAzOTA2LTIwLjM0Mzc1LTQ1LjM0Mzc1LTQ1LjM0Mzc1LTQ1LjM0Mzc1em0tMTk2LjY5MTQwNiAyMGgxOTYuNjkxNDA2YzEzLjk3MjY1NiAwIDI1LjM0Mzc1IDExLjM3MTA5NCAyNS4zNDM3NSAyNS4zNDM3NXY2LjYzMjgxMmgtMTMxLjAxOTUzMWMtNS41MjM0MzcgMC0xMCA0LjQ3NjU2My0xMCAxMCAwIDUuNTE5NTMyIDQuNDc2NTYzIDEwIDEwIDEwaDEzMS4wMTk1MzF2MzU4LjAzNTE1N2gtMjQ3LjM3ODkwNnYtMzU4LjAzNTE1N2gyNi4zNTkzNzVjNS41MjM0MzcgMCAxMC00LjQ4MDQ2OCAxMC0xMCAwLTUuNTIzNDM3LTQuNDc2NTYzLTEwLTEwLTEwaC0yNi4zNTkzNzV2LTYuNjMyODEyYzAtMTMuOTcyNjU2IDExLjM3MTA5NC0yNS4zNDM3NSAyNS4zNDM3NS0yNS4zNDM3NXptMjIyLjAzNTE1NiA0NDYuNjU2MjVjMCAxMy45NzI2NTYtMTEuMzcxMDk0IDI1LjM0Mzc1LTI1LjM0Mzc1IDI1LjM0Mzc1aC0xOTYuNjkxNDA2Yy0xMy45NzI2NTYgMC0yNS4zNDM3NS0xMS4zNzEwOTQtMjUuMzQzNzUtMjUuMzQzNzV2LTE2LjY0NDUzMWgyNDcuMzc4OTA2em0wIDAiLz48cGF0aCBkPSJtNTIuMjUgMTQ1LjA1MDc4MWg3MC41NzAzMTJ2NzAuNTY2NDA3aC03MC41NzAzMTJ6bTAgMCIgZmlsbD0iI2ZmZTc4NyIvPjxwYXRoIGQ9Im0xMjIuODIwMzEyIDIyNS42MTcxODhoLTcwLjU3MDMxMmMtNS41MjM0MzggMC0xMC00LjQ3NjU2My0xMC0xMHYtNzAuNTY2NDA3YzAtNS41MjM0MzcgNC40NzY1NjItMTAgMTAtMTBoNzAuNTcwMzEyYzUuNTIzNDM4IDAgMTAgNC40NzY1NjMgMTAgMTB2NzAuNTY2NDA3YzAgNS41MjM0MzctNC40NzY1NjIgMTAtMTAgMTB6bS02MC41NzAzMTItMjBoNTAuNTcwMzEydi01MC41NjY0MDdoLTUwLjU3MDMxMnptMCAwIi8+PHBhdGggZD0ibTE2NC41NTg1OTQgMTQ1LjA1MDc4MWg3MC41NzAzMTJ2NzAuNTY2NDA3aC03MC41NzAzMTJ6bTAgMCIgZmlsbD0iI2JhZjBmZiIvPjxwYXRoIGQ9Im0yMzUuMTI4OTA2IDIyNS42MTcxODhoLTcwLjU3MDMxMmMtNS41MjM0MzggMC0xMC00LjQ3NjU2My0xMC0xMHYtNzAuNTY2NDA3YzAtNS41MjM0MzcgNC40NzY1NjItMTAgMTAtMTBoNzAuNTcwMzEyYzUuNTIzNDM4IDAgMTAgNC40NzY1NjMgMTAgMTB2NzAuNTY2NDA3YzAgNS41MjM0MzctNC40NzY1NjIgMTAtMTAgMTB6bS02MC41NzAzMTItMjBoNTAuNTcwMzEydi01MC41NjY0MDdoLTUwLjU3MDMxMnptMCAwIi8+PHBhdGggZD0ibTUyLjI1IDI1Ni4zNTU0NjloNzAuNTcwMzEydjcwLjU3MDMxMmgtNzAuNTcwMzEyem0wIDAiIGZpbGw9IiNmNDg1OGEiLz48cGF0aCBkPSJtMTIyLjgyMDMxMiAzMzYuOTI1NzgxaC03MC41NzAzMTJjLTUuNTIzNDM4IDAtMTAtNC40NzY1NjItMTAtMTB2LTcwLjU3MDMxMmMwLTUuNTE5NTMxIDQuNDc2NTYyLTEwIDEwLTEwaDcwLjU3MDMxMmM1LjUyMzQzOCAwIDEwIDQuNDgwNDY5IDEwIDEwdjcwLjU3MDMxMmMwIDUuNTIzNDM4LTQuNDc2NTYyIDEwLTEwIDEwem0tNjAuNTcwMzEyLTIwaDUwLjU3MDMxMnYtNTAuNTcwMzEyaC01MC41NzAzMTJ6bTAgMCIvPjxwYXRoIGQ9Im0xNjQuNTU4NTk0IDI1Ni4zNTU0NjloNzAuNTcwMzEydjcwLjU3MDMxMmgtNzAuNTcwMzEyem0wIDAiIGZpbGw9IiNmZmU3ODciLz48cGF0aCBkPSJtMjM1LjEyODkwNiAzMzYuOTI1NzgxaC03MC41NzAzMTJjLTUuNTIzNDM4IDAtMTAtNC40NzY1NjItMTAtMTB2LTcwLjU3MDMxMmMwLTUuNTE5NTMxIDQuNDc2NTYyLTEwIDEwLTEwaDcwLjU3MDMxMmM1LjUyMzQzOCAwIDEwIDQuNDgwNDY5IDEwIDEwdjcwLjU3MDMxMmMwIDUuNTIzNDM4LTQuNDc2NTYyIDEwLTEwIDEwem0tNjAuNTcwMzEyLTIwaDUwLjU3MDMxMnYtNTAuNTcwMzEyaC01MC41NzAzMTJ6bTAgMCIvPjxwYXRoIGQ9Im0xNTYuMDIzNDM4IDQ4MS4wMDM5MDZoLTI0LjY2Nzk2OWMtNS41MjM0MzggMC0xMC00LjQ3NjU2Mi0xMC0xMCAwLTUuNTIzNDM3IDQuNDc2NTYyLTEwIDEwLTEwaDI0LjY2Nzk2OWM1LjUyMzQzNyAwIDEwIDQuNDc2NTYzIDEwIDEwIDAgNS41MjM0MzgtNC40NzY1NjMgMTAtMTAgMTB6bTAgMCIvPjxwYXRoIGQ9Im0xNzYuNjkxNDA2IDM4MWgtNjZjLTUuNTIzNDM3IDAtMTAtNC40NzY1NjItMTAtMTBzNC40NzY1NjMtMTAgMTAtMTBoNjZjNS41MTk1MzIgMCAxMCA0LjQ3NjU2MiAxMCAxMHMtNC40ODA0NjggMTAtMTAgMTB6bTAgMCIvPjwvc3ZnPg==" />
                    &nbsp;
                    Last three reported steps</li>
                <ul className="screenshots">
                    {renderScreenshots()}
                </ul>
            </div>
        </div>
    )
}

/*function deleteSomeStep(index, endPoint, sessionId, actionProvider){
    // ask the server to remove the step in the REPORT_S2R in the server
    // return the updated REPORT_S2R
    if (index > 0) {
        const data = {
            sessionId: sessionId,
            messages: [index]
        }
        console.log(index)
        const responsePromise = axios.post(endPoint, data);
        responsePromise.then(response => {

            let conversationResponse = response.data;
            let chatbotMsgs = conversationResponse.messages;
            let chatbotMsg = chatbotMsgs[0];

            if (conversationResponse.code === 0) {
                let stepsHistory = chatbotMsg.values;
                if (stepsHistory != null)
                    actionProvider.updateAllStepHistory(stepsHistory);
            } else if (conversationResponse.code === -1) {
                window.alert(chatbotMsg.messageObj.message);
            } else {
                window.alert("There was an unexpected error");
            }
        }).catch(error => {
            console.error(`There was an unexpected error: ${error}`);
        })
    }

}*/



export default StepsPanel;
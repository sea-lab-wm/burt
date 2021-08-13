import React, {useEffect, useRef} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Steps.css';
import Modal from 'react-modal';

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
                        sessionId
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
            let ind = index + 1
            let fullStepDescription = getFullDescription(ind, stepDescription);
            let croppedStepDescription = getCroppedDescription(ind, stepDescription);

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

            function deleteStep() {
                let endPoint = config.serverEndpoint + config.processDeleteSomeStep;
                deleteSomeStep(index, endPoint, sessionId, actionProvider);
            }



            return <li key={ind} className="list-group-item">
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
                    <a href="#" className="label label-danger" onClick={deleteStep} title="Delete this step">
                        <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                             className="bi bi-x-circle" viewBox="0 0 16 16">
                            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                            <path
                                d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                        </svg>
                    </a>
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
            return <div className="screenshot-display">
                <img className="screenshot" src={stepImage} alt=""/>
                <div className="screen-index">{stepIndex}</div>
            </div>
        })
    }


    return (
        <div className="span-steps App screen-center">
            <div className="steps-history" id="stepsHistoryPanel">
                <li className="nav-header">Reported steps</li>
                <ul ref={stepsContainer} className="nav nav-list">
                    {renderSteps()}
                </ul>

            </div>
            <div className="steps-history">
                <li className="nav-header">Last three reported steps</li>
                <ul className="screenshots">
                    {renderScreenshots()}
                </ul>
            </div>
        </div>
    )
}

function deleteSomeStep(index, endPoint, sessionId, actionProvider){
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

}



export default StepsPanel;
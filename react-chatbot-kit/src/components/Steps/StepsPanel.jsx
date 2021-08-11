import React from "react";
import ReactDOM from 'react-dom'
import './Steps.css';

import Modal from 'react-modal';
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
};
Modal.setAppElement('#root');


const StepsPanel = ({
                        stepsState
                    }) => {
    const [modalIsOpens, setIsOpens] = React.useState(Array( stepsState.steps.size).fill(false));
    const setIsOpen = (i, v) => {
        setIsOpens(Object.assign([...modalIsOpens], { [i]: v }));
    };
    function renderSteps() {
        return stepsState.steps.map((step, index) => {

                let stepDescription = step.value1;
                let stepImage = "http://localhost:8081" + step.value2;
                let ind = index +  1
                let desc = ind + ". " + stepDescription + ". ";
            let subtitle;
            function openModal(e) {
                window.onbeforeunload = null;
                e.preventDefault();
                setIsOpen(index,true);
            }

            function afterOpenModal() {
                // references are now sync'd and can be accessed.
                subtitle.style.color = '#f00';
            }

            function closeModal(e) {
                e.stopPropagation();
                setIsOpen(index,false);
            }

                return <li key={ind} className="list-group-item">
                    <small>
                        {desc}
                        <a href={stepImage}  title={"See a screenshot of this step"} onClick={openModal}>
                            <Modal
                                isOpen={modalIsOpens[index]}
                                onAfterOpen={afterOpenModal}
                                onRequestClose={closeModal}
                                style={customStyles}
                                contentLabel="Example Modal"
                                onHide={closeModal}
                                backdrop="static"
                                keyboard={false}
                            >
                                <div className="popupDisplay">
                                    <div className="popupTitle" title={desc}>{desc}</div>
                                    <img height="533px" width="300px" src={stepImage} />
                                    <button onClick={closeModal}>close</button>
                                </div>
                            </Modal>
                        <span className="label label-info">
                          <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                               className="bi bi-file-earmark-image" viewBox="0 0 16 16" >
                            <path d="M6.502 7a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3z"/>
                            <path
                              d="M14 14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5V14zM4 1a1 1 0 0 0-1 1v10l2.224-2.224a.5.5 0 0 1 .61-.075L8 11l2.157-3.02a.5.5 0 0 1 .76-.063L13 10V4.5h-2A1.5 1.5 0 0 1 9.5 3V1H4z"/>
                            </svg>
                            <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor"
                                 className="bi bi-zoom-in" viewBox="0 0 16 16">
                              <path fill-rule="evenodd"
                                    d="M6.5 12a5.5 5.5 0 1 0 0-11 5.5 5.5 0 0 0 0 11zM13 6.5a6.5 6.5 0 1 1-13 0 6.5 6.5 0 0 1 13 0z"/>
                              <path
                                  d="M10.344 11.742c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1 6.538 6.538 0 0 1-1.398 1.4z"/>
                              <path fill-rule="evenodd"
                                    d="M6.5 3a.5.5 0 0 1 .5.5V6h2.5a.5.5 0 0 1 0 1H7v2.5a.5.5 0 0 1-1 0V7H3.5a.5.5 0 0 1 0-1H6V3.5a.5.5 0 0 1 .5-.5z"/>
                            </svg>
                         </span>
                        </a>
                        <a href="#" className="label label-danger" onClick="deleteStep();" title="Delete this step">
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


    return (
        <div className="span8">
            <div className="steps-history sidebar-nav" id="stepsHistoryPanel">
                <li className="nav-header">Reported steps</li>
                <ul className="nav nav-list">
                    {renderSteps()}
                </ul>

            </div>
            <div className="steps-history sidebar-nav">
                <li className="nav-header">Last three reported steps</li>
                <ul className="screenshots">
                </ul>
            </div>

        </div>


    )

}



export default StepsPanel;
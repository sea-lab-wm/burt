import React, {useEffect, useRef, useState} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './NewAppPanel.css'
import {ReactComponent as LastStepsIcon} from "../../assets/icons/phone.svg";
import {func} from "prop-types";
import Modal from "react-modal";
import axios from "axios";
import {isOpera} from "sockjs-client/lib/utils/browser";

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
    modalButton: {
        margin: '.2em',
    },
}

class NewAppPanel extends React.Component {

    constructor(props) {
        super(props)
        console.log(this.props.showPanel)
        // console.log("Constructor")
    }

    state = {
        password: "",
        appName: "",
        appVersion: "",
        isOpen: false,
        selectedIcon: 'none',
        selectedCrashscopeFile: 'none',
        selectedTracereplayerFile: 'none'
    }

    showHidePasswordField() {
        let div = document.getElementById("name");
        div.style.display = (div.style.display !== "none") ? "none" : "block";
    }

    handleChange = event => {
        this.setState({
            password: event.target.value
        })
    };

    handleAppNameChange = event => {
        this.setState({
            appName: event.target.value
        })
    };

    handleAppVersionChange = event => {
        this.setState({
            appVersion: event.target.value
        })
    };


    toggleModal = () => {
        this.setState({
            isOpen: !this.state.isOpen
        })
    }

    validatePassword = event => {

        this.setState({
            password: event.target.value
        })

        // TODO : default password change this functionality to backend
        var defaultPassword = "test"
        if (this.state.password === defaultPassword) {
            this.toggleModal()
        } else {
            alert("Password NON Matched")
        }
    };


    submitNewApp = () => {
        // Get new App Add endpoint
        console.log(this.props.config)
        const endPoint = this.props.config.serverEndpoint + this.props.config.addAppService;

        // Prepare data
        const sessionId = this.props.sessionId;
        const data = {
            sessionId: sessionId,
            messages: [{
                message: this.state.fullStepDescription,
                selectedValues: [this.props.index]
            }]
        }

        const formData = new FormData();
        formData.append("req", new Blob([JSON.stringify(data)], {
            type: "application/json"
        }));
        formData.append("appName", this.state.appName);
        formData.append("appVersion", this.state.appVersion);
        formData.append("image", this.state.selectedIcon);
        formData.append("crashScopeZip", this.state.selectedCrashscopeFile);
        formData.append("traceReplayerZip", this.state.selectedTracereplayerFile);

        // Send and process post request
        const responsePromise = axios.post(endPoint, formData);
        responsePromise.then(response => {

            let result = response.data;
            if (!result) {
                console.error(`The New App Data was not updated: ` + this.props.index);
            }
        }).catch(error => {
            console.error(`There was an unexpected error: ${error}`);
        })

    }


    changeIconHandler = event => {
        this.setState({
            selectedIcon: event.target.files[0]
        })

        console.log(this.state.selectedIcon)
    }

    changeCrashscopeHandler = event => {
        this.setState({
            selectedCrashscopeFile: event.target.files[0]
        })
    }

    changeTracereplayerHandler = event => {
        this.setState({
            selectedTracereplayerFile: event.target.files[0]
        })
    }

    onInputchange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    close(){
        this.setState({ isOpen: false });
    }

    open(){
        console.log(this.state.isOpen)
        this.setState({ isOpen: true });
    }

    render() {
        return (
            <div className="last-steps">
                <div className="subpanel-header">
                    Developer Panel
                </div>
                <div className="addNewApp" style={{paddingBottom: '5px'}}>
                    <button className={"btn btn-primary btn-sm action-btn"}
                            onClick={e => this.showHidePasswordField(e)}>Add New App
                    </button>
                    <div id={"name"} style={{display: 'none', paddingBottom: '5px'}}>
                        <input type={"password"} placeholder={"Enter password"} value={this.state.password}
                               onChange={this.handleChange}/>
                        <button className={"btn-primary"}  onClick={this.validatePassword}>Submit</button>

                    </div>
                    <Modal
                        isOpen={this.state.isOpen}
                        onRequestClose={this.toggleModal}
                        contentLabel="My dialog"
                        style={customStyles}
                        backdrop="static"
                        keyboard={false}
                        closeTimeoutMS={10}>
                        <div className={"popup-display"}>
                            <div>
                                <div className="form-label"><h3>App Details</h3></div>
                                <div className="container">
                                    <div className={"row"}>
                                        <div className={"col-5"}>
                                            <label htmlFor="appName">App Name</label>
                                        </div>
                                        <div className={"col-7"}>
                                            <input type="text" className="form-control" id="appName"
                                                   placeholder="Enter App Name"
                                                   value={this.state.appName}
                                                   onChange={this.handleAppNameChange}/>
                                        </div>
                                    </div>

                                    <div className={"row"}>
                                        <div className={"col-5"}>
                                            <label htmlFor="appVersion">App Version</label>
                                        </div>
                                        <div className={"col-7"}>
                                            <input type="text" className="form-control" id="appVersion"
                                                   placeholder="Enter App Version"
                                                   value={this.state.appVersion}
                                                   onChange={this.handleAppVersionChange}/>
                                        </div>
                                    </div>

                                    <div className={"row"}>
                                        <div className={"col-5"}>
                                            <label htmlFor="appIcon">App icon</label>
                                        </div>
                                        <div className={"col-7"}>
                                            <input type="file" className="form-control-file"
                                                   onChange={this.changeIconHandler} accept="image/*" id="appIcon"/>
                                        </div>
                                    </div>

                                    <div className={"row"}>
                                        <div className={"col-5"}>
                                            <label htmlFor="crashScopeFile">CrashScope Zip File</label>
                                        </div>
                                        <div className={"col-7"}>
                                            <input type="file" className="form-control-file"
                                                   onChange={this.changeCrashscopeHandler} accept=".zip"
                                                   id="crashScopeFile"/>
                                        </div>
                                    </div>
                                    <div className={"row"}>
                                        <div className={"col-5"}>
                                            <label htmlFor="traceReplayerFile">TraceReplayer Zip File</label>
                                        </div>
                                        <div className={"col-7"}>
                                            <input type="file" className="form-control-file"
                                                   onChange={this.changeTracereplayerHandler} accept=".zip"
                                                   id="traceReplayerFile"/>
                                        </div>
                                    </div>
                                    <button className={"btn btn-primary mb-2"} onClick={this.submitNewApp}>Submit
                                    </button>
                                    <button className={"btn btn-danger mb-2"} onClick={this.toggleModal}>Cancel
                                    </button>
                                </div>


                            </div>
                        </div>
                    </Modal>
                </div>
            </div>
        )
    }
}

export default NewAppPanel;
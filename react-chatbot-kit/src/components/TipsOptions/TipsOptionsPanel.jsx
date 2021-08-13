import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './TipsOptions.css';
import Modal from 'react-modal';
import StepsPanel from "../Steps/StepsPanel";
const axios = require('axios')
const CODE = {
    ERROR_CODE : -1,
    SUCCESS_CODE : 0,
    END_CONVERSATION_CODE : 100,
    REPORT_NO_INFO_CODE : -2,
};

function sendRequestSync(url, data) {
    let request = new XMLHttpRequest();
    request.open('POST', url, false);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

    request.send(data !== null && data !== undefined ? JSON.stringify(data) : data);

    if (request.status === 200) {
        const messagesFromServer = request.responseText;

        if (messagesFromServer !== "") {
            try {
                return JSON.parse(messagesFromServer)
            } catch (e) {
                return messagesFromServer
            }
        }
    } else {
        throw `There was an error: ${request.status} - ${request.statusText}`
    }
    return null;
}

function restartNewConversation(url, sessionId, actionProvider, SessionManager){
        const data = {
            sessionId: sessionId,
        }
        try {
            const conversationResponse = sendRequestSync(url, data)

            if (conversationResponse === CODE.SUCCESS_CODE) {
                SessionManager.endSession();
                window.location.reload(false);
            } else if (conversationResponse === CODE.ERROR_CODE) {
                window.alert("I am sorry, I couldn't restart the conversation." +
                    "Please try again in a few moments.");
            } else {
                window.alert("There was an unexpected error");
            }
        } catch (e) {
            console.error(`There was an unexpected error: ${e}`);
        }

}


function previewBugReport(config, url, sessionId) {
    const data = {
        sessionId: sessionId,
    }
    const responsePromise=  axios.post(url, data);
    responsePromise.then(response => {

        let conversationResponse = response.data;
        let chatbotMsgs = conversationResponse.messages;
        let chatbotMsg = chatbotMsgs[0];

        if (conversationResponse.code === CODE.SUCCESS_CODE) {
            let link = chatbotMsg.generatedReport;
            console.log(link);
            window.open(config.serverEndpoint + "/" + link, "_blank");
        } else if (conversationResponse.code === CODE.REPORT_NO_INFO_CODE) {
            window.alert(chatbotMsg.messageObj.message);
        } else if (conversationResponse.code === CODE.ERROR_CODE) {
            window.alert(chatbotMsg.messageObj.message);
        } else {
            window.alert("There was an unexpected error");
        }
    }).catch(error => {
        console.error(`There was an unexpected error: ${error}`);
    })



}
const TipsOptionsPanel = ({
            config,
            SessionManager,
            actionProvider,
            processResponse,
            sessionId, messageParser}) => {

    function restartConversation() {
        let url = config.serverEndpoint + config.endService;
        restartNewConversation(url, sessionId, actionProvider, SessionManager);
    }

    function finishS2R() {
        const msg = actionProvider.createUserMsg("This is the last step")
        actionProvider.updateChatbotState(msg)
        messageParser.parse(msg);
    }
    function previewReport() {
        let url = config.serverEndpoint + config.getBugReportPreview;
        previewBugReport(config, url, sessionId);
    }
//onClick={finishS2R}
    return(
        <div className="span-tips-options App screen-center">
        <div className="tips-quick-answers">
            <div className="quick-answers">
                <div className="subpanel-header">
                    <img height="25px" width="25px" src="data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjUxMiIgdmlld0JveD0iMCAwIDY0IDY0IiB3aWR0aD0iNTEyIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxnIGlkPSJBcHBsaWNhdGlvbi10b3VjaC1oYW5kLXNvZnR3YXJlLWFwcCI+PHJlY3QgZmlsbD0iIzQ2ZjhmZiIgaGVpZ2h0PSI0NiIgcng9IjIiIHdpZHRoPSIyNiIgeD0iMyIgeT0iMyIvPjxwYXRoIGQ9Im0yOSA1djQyYTIuMDA2IDIuMDA2IDAgMCAxIC0yIDJoLTIyYTIuMDA2IDIuMDA2IDAgMCAxIC0yLTJ2LTJoMjBhMi4wMDYgMi4wMDYgMCAwIDAgMi0ydi00MGgyYTIuMDA2IDIuMDA2IDAgMCAxIDIgMnoiIGZpbGw9IiMwMGQ3ZGYiLz48cmVjdCBmaWxsPSIjZmZkZTU1IiBoZWlnaHQ9IjE2IiByeD0iMiIgd2lkdGg9IjI2IiB4PSIzNSIgeT0iMyIvPjxwYXRoIGQ9Im02MSA1djEyYTIuMDA2IDIuMDA2IDAgMCAxIC0yIDJoLTIyYTIuMDA2IDIuMDA2IDAgMCAxIC0yLTJ2LTJoMjBhMi4wMDYgMi4wMDYgMCAwIDAgMi0ydi0xMGgyYTIuMDA2IDIuMDA2IDAgMCAxIDIgMnoiIGZpbGw9IiNlYmJmMDAiLz48cGF0aCBkPSJtMjQgM3YyYTIuMDA2IDIuMDA2IDAgMCAxIC0yIDJoLTEyYTIuMDA2IDIuMDA2IDAgMCAxIC0yLTJ2LTJ6IiBmaWxsPSIjZTBlMGUyIi8+PHBhdGggZD0ibTI0IDN2MmEyLjAwNiAyLjAwNiAwIDAgMSAtMiAyaC00YTIuMDA2IDIuMDA2IDAgMCAwIDItMnYtMnoiIGZpbGw9IiNjNmM1Y2EiLz48cmVjdCBmaWxsPSIjNTdhNGZmIiBoZWlnaHQ9IjgiIHJ4PSI0IiB3aWR0aD0iMTgiIHg9IjciIHk9IjExIi8+PHBhdGggZD0ibTI1IDE1YTMuOTk1IDMuOTk1IDAgMCAxIC00IDRoLTEwYTMuOTkyIDMuOTkyIDAgMCAxIC0zLjg3LTNoOS44N2EzLjk5NSAzLjk5NSAwIDAgMCA0LTQgMy42NTcgMy42NTcgMCAwIDAgLS4xMy0xaC4xM2E0IDQgMCAwIDEgNCA0eiIgZmlsbD0iIzAwNmRmMCIvPjxyZWN0IGZpbGw9IiM1N2E0ZmYiIGhlaWdodD0iOCIgcng9IjQiIHdpZHRoPSIxOCIgeD0iNyIgeT0iMjMiLz48cGF0aCBkPSJtMjUgMjdhMy45OTUgMy45OTUgMCAwIDEgLTQgNGgtMTBhMy45OTIgMy45OTIgMCAwIDEgLTMuODctM2g5Ljg3YTMuOTk1IDMuOTk1IDAgMCAwIDQtNCAzLjY1NyAzLjY1NyAwIDAgMCAtLjEzLTFoLjEzYTQgNCAwIDAgMSA0IDR6IiBmaWxsPSIjMDA2ZGYwIi8+PHJlY3QgZmlsbD0iIzU3YTRmZiIgaGVpZ2h0PSI4IiByeD0iNCIgd2lkdGg9IjE4IiB4PSI3IiB5PSIzNSIvPjxwYXRoIGQ9Im0yNSAzOWEzLjk5NSAzLjk5NSAwIDAgMSAtNCA0aC0xMGEzLjk5MiAzLjk5MiAwIDAgMSAtMy44Ny0zaDkuODdhMy45OTUgMy45OTUgMCAwIDAgNC00IDMuNjU3IDMuNjU3IDAgMCAwIC0uMTMtMWguMTNhNCA0IDAgMCAxIDQgNHoiIGZpbGw9IiMwMDZkZjAiLz48cGF0aCBkPSJtNDEgNDJ2My4zN2ExMiAxMiAwIDAgMSAtMi4wMSA2LjY1bC01Ljk4IDguOThoLTE3LjAybC0zLjY5LTYuMzRhMTIuMDQ3IDEyLjA0NyAwIDAgMSAtLjMyLTExLjVsLjEzLS4xNiA0Ljg5LTZ2LTE3YTIuNzcyIDIuNzcyIDAgMCAxIC4xOC0xIDIuOTkgMi45OSAwIDAgMSA0Ljk0LTEuMTIgMy4xNTQgMy4xNTQgMCAwIDEgLjU1Ljc1IDMuMDExIDMuMDExIDAgMCAxIC4zMyAxLjM3djE4YTMgMyAwIDAgMSA2IDB2MmEzIDMgMCAwIDEgNiAwdjJhMyAzIDAgMCAxIDYgMHoiIGZpbGw9IiNmZmRhYWEiLz48cGF0aCBkPSJtNDEgNDIuMTY5djMuMmExMiAxMiAwIDAgMSAtMi4wMSA2LjY1bC01Ljk4IDguOTgxaC0xNy4wMmwtMi4zMy00aDIuNTYxYTIwLjE0NyAyMC4xNDcgMCAwIDAgMTYuNzY5LTguOTggMTIgMTIgMCAwIDAgMi4wMS02LjY1di42M2EzIDMgMCAwIDEgMy4yMi0yLjk5MiAzLjExNiAzLjExNiAwIDAgMSAyLjc4IDMuMTYxeiIgZmlsbD0iI2ZmYjY1NSIvPjxwYXRoIGQ9Im0zOCAzOGEzLjkzMiAzLjkzMiAwIDAgMCAtMi4yNC42OSA0IDQgMCAwIDAgLTMuNzYtMi42OSAzLjkwNiAzLjkwNiAwIDAgMCAtMiAuNTZ2LTMxLjU2YTMuMDA5IDMuMDA5IDAgMCAwIC0zLTNoLTIyYTMuMDA5IDMuMDA5IDAgMCAwIC0zIDN2NDJhMy4wMDkgMy4wMDkgMCAwIDAgMyAzaDQuNzZhMTMgMTMgMCAwIDAgMS42OCA1LjE3bDMuNjkgNi4zM2ExIDEgMCAwIDAgLjg2LjVoMTcuMDJhMS4wMDggMS4wMDggMCAwIDAgLjgzLS40NWw1Ljk4LTguOTdhMTIuOTY5IDEyLjk2OSAwIDAgMCAyLjE4LTcuMjF2LTMuMzdhNCA0IDAgMCAwIC00LTR6bS0yOS0zNGgxNHYxYTEgMSAwIDAgMSAtMSAxaC0xMmExIDEgMCAwIDEgLTEtMXptLjcgNDRoLTQuN2ExIDEgMCAwIDEgLTEtMXYtNDJhMSAxIDAgMCAxIDEtMWgydjFhMy4wMDkgMy4wMDkgMCAwIDAgMyAzaDEyYTMuMDA5IDMuMDA5IDAgMCAwIDMtM3YtMWgyYTEgMSAwIDAgMSAxIDF2MjkuNTZhMy44NTEgMy44NTEgMCAwIDAgLTQgMHYtMy41N2E0LjQwOSA0LjQwOSAwIDAgMCAuNTQtLjQ2IDQuOTg4IDQuOTg4IDAgMCAwIC0uNTQtNy41MnYtMy4wMWEzLjkyOCAzLjkyOCAwIDAgMCAtLjEyLS45MiA1LjY1OSA1LjY1OSAwIDAgMCAuNjYtLjU1IDUgNSAwIDAgMCAtMy41NC04LjUzaC0xMGE1IDUgMCAwIDAgMCAxMGg1djJoLTVhNSA1IDAgMCAwIDAgMTBoNXYyaC01YTQuOTg0IDQuOTg0IDAgMCAwIC0uNDUgOS45NSAxMy4wMjkgMTMuMDI5IDAgMCAwIC0uODUgNC4wNXptMTMuNDMtMzAuODlhMS44MTEgMS44MTEgMCAwIDEgLS4yMS4xOCAzLjk1NSAzLjk1NSAwIDAgMCAtNi4zNi43MWgtNS41NmEzIDMgMCAxIDEgMC02aDEwYTMuMDA5IDMuMDA5IDAgMCAxIDMgMyAyLjk2NCAyLjk2NCAwIDAgMSAtLjg3IDIuMTF6bS03LjEzIDYuODl2NmgtNWEzIDMgMCAxIDEgMC02em0tNSAxOGEzIDMgMCAxIDEgMC02aDV2LjY0bC00LjM2IDUuMzZ6bTI5IDMuMzdhMTEuMDEyIDExLjAxMiAwIDAgMSAtMS44NCA2LjFsLTUuNjkgOC41M2gtMTUuOWwtMy40MS01Ljg0YTEwLjk1MiAxMC45NTIgMCAwIDEgLS4zMy0xMC40NWwzLjE3LTMuOXY1LjE5aDJ2LTI1YTIgMiAwIDAgMSA0IDB2MjVoMnYtN2EyIDIgMCAwIDEgNCAwdjhoMnYtNmEyIDIgMCAwIDEgNCAwdjhoMnYtNmEyIDIgMCAwIDEgNCAweiIvPjxwYXRoIGQ9Im01OSAyaC0yMmEzIDMgMCAwIDAgLTMgM3YxMmEzIDMgMCAwIDAgMyAzaDIyYTMgMyAwIDAgMCAzLTN2LTEyYTMgMyAwIDAgMCAtMy0zem0xIDE1YTEgMSAwIDAgMSAtMSAxaC0yMmExIDEgMCAwIDEgLTEtMXYtMTJhMSAxIDAgMCAxIDEtMWgyMmExIDEgMCAwIDEgMSAxeiIvPjxwYXRoIGQ9Im00MiA2aDE2djJoLTE2eiIvPjxwYXRoIGQ9Im00MiAxMGgxNnYyaC0xNnoiLz48cGF0aCBkPSJtNDIgMTRoMTZ2MmgtMTZ6Ii8+PHBhdGggZD0ibTM4IDZoMnYyaC0yeiIvPjxwYXRoIGQ9Im0zOCAxMGgydjJoLTJ6Ii8+PHBhdGggZD0ibTM4IDE0aDJ2MmgtMnoiLz48cGF0aCBkPSJtNDcgMjhhMyAzIDAgMCAxIC0zIDNoLTEydjJoMTJhNS4wMDYgNS4wMDYgMCAwIDAgNS01di0yaC0yeiIvPjxwYXRoIGQ9Im00NyAyMmgydjJoLTJ6Ii8+PC9nPjwvc3ZnPg==" />
                    Quick actions</div>
                <div className="button-lists">
                    <button type="button" className="btn btn-success btn-sm action-btn" onClick={finishS2R} >Finish reporting the bug</button>
                    <button type="button" className="btn btn-primary btn-sm action-btn" onClick={restartConversation} >Restart the conversation</button>
                    <button type="button" className="btn btn-warning btn-sm action-btn" onClick={previewReport} >View the bug report</button>
                </div>
            </div>
            <div className="tips">
                <div className="subpanel-header">
                    <img height="25px" width="25px" src="data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjUxMiIgdmlld0JveD0iMCAwIDUyIDYwIiB3aWR0aD0iNTEyIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxnIGlkPSJQYWdlLTEiIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0iZXZlbm9kZCI+PGcgaWQ9IjA2MS0tLXRpcC1hbGVydCIgZmlsbC1ydWxlPSJub256ZXJvIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwIC0xKSI+PGcgaWQ9ImNvbG9yIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxIDEpIj48cGF0aCBpZD0iU2hhcGUiIGQ9Im01MCAxNnYyN2MwIDIuMjA5MTM5LTEuNzkwODYxIDQtNCA0aC0xMGMtLjYyOTUxNDYgMC0xLjIyMjI5MTIuMjk2Mzg4My0xLjYuOGwtNy44NiAxMC40OGMtLjM3OTk1OTguNDU4NTQwNi0uOTQ0NDkyNy43MjM5MTIyLTEuNTQuNzIzOTEyMnMtMS4xNjAwNDAyLS4yNjUzNzE2LTEuNTQtLjcyMzkxMjJsLTcuODYtMTAuNDhjLS4zNzc3MDg4LS41MDM2MTE3LS45NzA0ODU0LS44LTEuNi0uOGgtMTBjLTIuMjA5MTM5IDAtNC0xLjc5MDg2MS00LTR2LTI3YzAtMi4yMDkxMzkgMS43OTA4NjEtNCA0LTRoNy4zMmMtMS4wMzEwODMzIDQuNzkxMzU3OC41MDcyNDY4IDkuNzczODggNC4wNiAxMy4xNSAyLjM1OTI1NzEgMi4zMTA3ODUxIDMuNjY3NzEzNCA1LjQ4Nzk0ODMgMy42MiA4Ljc5djYuMDZjMCAxLjY1Njg1NDIgMS4zNDMxNDU4IDMgMyAzaDZjMS42NTY4NTQyIDAgMy0xLjM0MzE0NTggMy0zdi02LjIzYy0uMDQ4NjIzMy0zLjIyMjk1ODMgMS4yNDAyNDAzLTYuMzIyMDIzNCAzLjU2LTguNTYgMi44MzQ4NTI3LTIuNjM4MjIxNiA0LjQ0MzUzMDgtNi4zMzc0NTY4IDQuNDQtMTAuMjEuMDAwNjI3Ny0xLjAwODU1MDUtLjEwNjY0OTUtMi4wMTQyNzM4LS4zMi0zaDcuMzJjMi4yMDkxMzkgMCA0IDEuNzkwODYxIDQgNHoiIGZpbGw9IiNlOTFlNjMiLz48ZyBmaWxsPSIjYWQxNDU3Ij48cGF0aCBpZD0iU2hhcGUiIGQ9Im0xMi4zOCAyNS4xNWMyLjM1OTI1NzEgMi4zMTA3ODUxIDMuNjY3NzEzNCA1LjQ4Nzk0ODMgMy42MiA4Ljc5djYuMDZjMCAxLjY1Njg1NDIgMS4zNDMxNDU4IDMgMyAzaDNjLTEuNjU2ODU0MiAwLTMtMS4zNDMxNDU4LTMtM3YtNi4wNmMuMDQ3NzEzNC0zLjMwMjA1MTctMS4yNjA3NDI5LTYuNDc5MjE0OS0zLjYyLTguNzktMy41NTI3NTMyLTMuMzc2MTItNS4wOTEwODMzLTguMzU4NjQyMi00LjA2LTEzLjE1aC0zYy0xLjAzMTA4MzI5IDQuNzkxMzU3OC41MDcyNDY3NyA5Ljc3Mzg4IDQuMDYgMTMuMTV6Ii8+PHBhdGggaWQ9IlNoYXBlIiBkPSJtNDYgMTJoLTNjMi4yMDkxMzkgMCA0IDEuNzkwODYxIDQgNHYyN2MwIDIuMjA5MTM5LTEuNzkwODYxIDQtNCA0aDNjMi4yMDkxMzkgMCA0LTEuNzkwODYxIDQtNHYtMjdjMC0yLjIwOTEzOS0xLjc5MDg2MS00LTQtNHoiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im0zMS40IDQ3LjgtNy44NiAxMC40OGMuMzc5NjY2MS40MzA1MDA5LjkyNTk5ODguNjc3MTI0MyAxLjUuNjc3MTI0M3MxLjEyMDMzMzktLjI0NjYyMzQgMS41LS42NzcxMjQzbDcuODYtMTAuNDhjLjM3NzcwODgtLjUwMzYxMTcuOTcwNDg1NC0uOCAxLjYtLjhoLTNjLS42Mjk1MTQ2IDAtMS4yMjIyOTEyLjI5NjM4ODMtMS42Ljh6Ii8+PC9nPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTM5IDE1Yy4wMDM1MzA4IDMuODcyNTQzMi0xLjYwNTE0NzMgNy41NzE3Nzg0LTQuNDQgMTAuMjEtMi4zMTk3NTk3IDIuMjM3OTc2Ni0zLjYwODYyMzMgNS4zMzcwNDE3LTMuNTYgOC41NnYyLjIzaC0xMnYtMi4wNmMuMDQ3NzEzNC0zLjMwMjA1MTctMS4yNjA3NDI5LTYuNDc5MjE0OS0zLjYyLTguNzktMy41NTI3NTMyLTMuMzc2MTItNS4wOTEwODMzLTguMzU4NjQyMi00LjA2LTEzLjE1IDEuNTIxNDY5NC02Ljk5MzY3ODIyIDguMDcyOTM0OS0xMS43MTM2MTIwNSAxNS4xODg1Mjc1LTEwLjk0MjQxMzQ0IDcuMTE1NTkyNS43NzExOTg2MSAxMi41MDM3MDIyIDYuNzg1MTYxNTUgMTIuNDkxNDcyNSAxMy45NDI0MTM0NHoiIGZpbGw9IiNmZmViM2EiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im0zOC42OCAxMmMtMS40NTczOTI5LTYuNjgxOTIxNzMtNy41MjkwOTg0LTExLjMzMjk0OTYtMTQuMzYtMTEtLjI4IDAtLjU2IDAtLjg0LjA3IDUuNDM1MTg1Ni41ODg4ODQzOSAxMC4wMzAzNjgyIDQuMjg3Njg0OCAxMS43NjY5MTg4IDkuNDcxNTQ2M3MuMjk2NzQ1OSAxMC45MDQzMzI1LTMuNjg2OTE4OCAxNC42NDg0NTM3Yy0yLjMyNDg4MjYgMi4yNDI3NTA4LTMuNjE0MTkwNiA1LjM1MDEyNzktMy41NiA4LjU4djIuMjNoM3YtMi4yM2MtLjA0ODYyMzMtMy4yMjI5NTgzIDEuMjQwMjQwMy02LjMyMjAyMzQgMy41Ni04LjU2IDIuODM0ODUyNy0yLjYzODIyMTYgNC40NDM1MzA4LTYuMzM3NDU2OCA0LjQ0LTEwLjIxLjAwMDYyNzctMS4wMDg1NTA1LS4xMDY2NDk1LTIuMDE0MjczOC0uMzItM3oiIGZpbGw9IiNmZGQ4MzQiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im0zMSAzNnY0YzAgMS42NTY4NTQyLTEuMzQzMTQ1OCAzLTMgM2gtNmMtMS42NTY4NTQyIDAtMy0xLjM0MzE0NTgtMy0zdi00eiIgZmlsbD0iI2Y1ZjVmNSIvPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTI4IDM2djRjMCAxLjY1Njg1NDItMS4zNDMxNDU4IDMtMyAzaDNjMS42NTY4NTQyIDAgMy0xLjM0MzE0NTggMy0zdi00eiIgZmlsbD0iI2NmZDhkYyIvPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTE2IDEzaC0uMTljLS4yNjc1NzgyLS4wNDU4NTEyLS41MDUwMTUzLS4xOTg1MjE4LS42NTc3NzM0LS40MjI5NDQxLS4xNTI3NTgxLS4yMjQ0MjI0LS4yMDc3MDc1LS41MDEzMDczLS4xNTIyMjY2LS43NjcwNTU5LjcwMjI0MDYtMy4wMDAyNTU2NCAyLjc3NzM2NjgtNS40OTQxMTI2NCA1LjYtNi43My4zMzU5MDMyLS4xNzgyMDMyOC43NDM4MDgzLS4xNTA0ODE5MiAxLjA1MjUxMjkuMDcxNTI5MDkuMzA4NzA0NS4yMjIwMTEwMS40NjQ3ODU2LjU5OTg5MTYzLjQwMjczNDMuOTc1MDQwOTQtLjA2MjA1MTQuMzc1MTQ5My0uMzMxNDk2OS42ODI2NDU5NC0uNjk1MjQ3Mi43OTM0Mjk5NyAwIDAtMy42NiAxLjU5LTQuMzYgNS4yNy0uMDkyMjQ5MS40NzcxMDgyLS41MTQxMzU4LjgxODgzNjQtMSAuODF6IiBmaWxsPSIjZjVmNWY1Ii8+PC9nPjxnIGlkPSJJY29ucyIgZmlsbD0iIzAwMCI+PHBhdGggaWQ9IlNoYXBlIiBkPSJtMCAxN3YyN2MwIDEuMzI2MDgyNC41MjY3ODQyIDIuNTk3ODUyIDEuNDY0NDY2MDkgMy41MzU1MzM5LjkzNzY4MTkuOTM3NjgxOSAyLjIwOTQ1MTQ2IDEuNDY0NDY2MSAzLjUzNTUzMzkxIDEuNDY0NDY2MWgxMGMuMzE0NzU3MyAwIC42MTExNDU2LjE0ODE5NDIuOC40bDcuODkgMTAuNTJjLjU2OTczMjguNjk4Njg2NyAxLjQyMzQ2ODggMS4xMDQxMTYzIDIuMzI1IDEuMTA0MTE2M3MxLjc1NTI2NzItLjQwNTQyOTYgMi4zMjUtMS4xMDQxMTYzbDcuODYtMTAuNTJjLjE4ODg1NDQtLjI1MTgwNTguNDg1MjQyNy0uNC44LS40aDEwYzEuMzI2MDgyNCAwIDIuNTk3ODUyLS41MjY3ODQyIDMuNTM1NTMzOS0xLjQ2NDQ2NjFzMS40NjQ0NjYxLTIuMjA5NDUxNSAxLjQ2NDQ2NjEtMy41MzU1MzM5di0yN2MwLTIuNzYxNDIzNy0yLjIzODU3NjMtNS01LTVoLTYuNTVjLTEuODYyNjkzMy02Ljc2MjYyMTgzLTguMTYzMzY3Ny0xMS4zMzEzMzc2NC0xNS4xNy0xMS02LjQ3NTk2ODQuMzExOTA5ODktMTIuMDE4NTgwMyA0Ljc0OTIyNjU2LTEzLjc0IDExaC02LjU0Yy0yLjc2MTQyMzc1IDAtNSAyLjIzODU3NjMtNSA1em0yNS4zNy0xNGM1LjQ0MTcyMTYtLjI1OTY1OTYyIDEwLjQ2ODE0MDggMi45MDEyNjkzMyAxMi41OTE1MjEyIDcuOTE4MzQxNSAyLjEyMzM4MDMgNS4wMTcwNzIyLjg5MzI2NTUgMTAuODI1OTYwMS0zLjA4MTUyMTIgMTQuNTUxNjU4NS0yLjUyMzUwMTYgMi40MzAyMzM4LTMuOTI4MDg0NyA1Ljc5Njg4OTMtMy44OCA5LjN2MS4yM2gtMTB2LTEuMDZjLjA0NTYwNS0zLjU3ODk1NTktMS4zNzg1MzM0LTcuMDIwMDIxMi0zLjk0LTkuNTItMy43Nzg2OTk2LTMuNTgxNDA1NS01LjA1NjI2MzYtOS4wNzE3OTgxLTMuMjQ2ODUyOC0xMy45NTM1MDU3IDEuODA5NDEwOC00Ljg4MTcwNzUzIDYuMzU2Nzc0LTguMjEzMDgzNzggMTEuNTU2ODUyOC04LjQ2NjQ5NDN6bTMuNjMgNDBoLTZjLTEuMTA0NTY5NSAwLTItLjg5NTQzMDUtMi0ydi0zaDEwdjNjMCAxLjEwNDU2OTUtLjg5NTQzMDUgMi0yIDJ6bS0yNy0yNmMwLTEuNjU2ODU0MiAxLjM0MzE0NTc1LTMgMy0zaDYuMTVjMCAuMjEtLjA3LjQyLS4wOS42My0uNDE0NzU4NiA0LjU3NzAwMTcgMS4yOTE1NTgyIDkuMDkxNTU0OCA0LjYzIDEyLjI1IDIuMTU3NzQ1OSAyLjEyMTg5OTEgMy4zNTM2NzU5IDUuMDM0MDQzIDMuMzEgOC4wNnY2LjA2Yy4wMDU5MDcyIDEuODE5MTU3IDEuMjM4NTk3MSAzLjQwNTIxNzkgMyAzLjg2djEuMTRjMCAuNTUyMjg0Ny40NDc3MTUzIDEgMSAxczEtLjQ0NzcxNTMgMS0xdi0xaDR2MWMwIC41NTIyODQ3LjQ0NzcxNTMgMSAxIDFzMS0uNDQ3NzE1MyAxLTF2LTEuMTRjMS43NjE0MDI5LS40NTQ3ODIxIDIuOTk0MDkyOC0yLjA0MDg0MyAzLTMuODZ2LTYuMjNjLS4wNDQ1MjM5LTIuOTQ2ODM2IDEuMTMxNzkxLTUuNzgwODUgMy4yNS03LjgzIDMuNTcxNzg1MS0zLjI5MTkxMzcgNS4yOTI0NDYyLTguMTMyMjA4MSA0LjYtMTIuOTRoNi4xNWMxLjY1Njg1NDIgMCAzIDEuMzQzMTQ1OCAzIDN2MjdjMCAxLjY1Njg1NDItMS4zNDMxNDU4IDMtMyAzaC0xMGMtLjk0NDI3MTkgMC0xLjgzMzQzNjkuNDQ0NTgyNS0yLjQgMS4ybC03LjgzIDEwLjQ0Yy0uMTk5MjE2MS4yMDIxNTQ0LS40NzExODA2LjMxNTk3MzgtLjc1NS4zMTU5NzM4cy0uNTU1NzgzOS0uMTEzODE5NC0uNzU1LS4zMTU5NzM4bC03Ljg2LTEwLjQ0Yy0uNTY2NTYzMS0uNzU1NDE3NS0xLjQ1NTcyODEtMS4yLTIuNC0xLjJoLTEwYy0xLjY1Njg1NDI1IDAtMy0xLjM0MzE0NTgtMy0zeiIvPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTI0IDI5Yy0uNTUyMjg0NyAwLTEgLjQ0NzcxNTMtMSAxdjNjMCAuNTUyMjg0Ny40NDc3MTUzIDEgMSAxczEtLjQ0NzcxNTMgMS0xdi0zYzAtLjU1MjI4NDctLjQ0NzcxNTMtMS0xLTF6Ii8+PHBhdGggaWQ9IlNoYXBlIiBkPSJtMjMgMjVjMCAuNTUyMjg0Ny40NDc3MTUzIDEgMSAxczEtLjQ0NzcxNTMgMS0xdi0xYzAtLjU1MjI4NDctLjQ0NzcxNTMtMS0xLTFzLTEgLjQ0NzcxNTMtMSAxeiIvPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTI4IDM0Yy41NTIyODQ3IDAgMS0uNDQ3NzE1MyAxLTF2LTNjMC0uNTUyMjg0Ny0uNDQ3NzE1My0xLTEtMXMtMSAuNDQ3NzE1My0xIDF2M2MwIC41NTIyODQ3LjQ0NzcxNTMgMSAxIDF6Ii8+PHBhdGggaWQ9IlNoYXBlIiBkPSJtMjggMjZjLjU1MjI4NDcgMCAxLS40NDc3MTUzIDEtMXYtMWMwLS41NTIyODQ3LS40NDc3MTUzLTEtMS0xcy0xIC40NDc3MTUzLTEgMXYxYzAgLjU1MjI4NDcuNDQ3NzE1MyAxIDEgMXoiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im0xMCA4Yy4zMDI1ODg0LjAwMTAyODg2LjU4OTM2OTktLjEzNTAwODUzLjc4LS4zNy4xNjY2NDc3LS4yMDc4NDQ2My4yNDM0NzM0LS40NzM1OTI5Ny4yMTM0MzYtLjczODI5Nzcxcy0uMTY0NDU0Ny0uNTA2NDgyMjMtLjM3MzQzNi0uNjcxNzAyMjlsLTUtNGMtLjQzMTM2OTkzLS4zNDAzNTY4OC0xLjA1NjU0NjA4LS4yNjg5MDgxOC0xLjQuMTYtLjM0MDM1Njg4LjQzMTM2OTkzLS4yNjg5MDgxOCAxLjA1NjU0NjA4LjE2IDEuNGw1IDRjLjE3NTkzOTI3LjE0MTE2MDU5LjM5NDQzNTc2LjIxODY5MTYuNjIuMjJ6Ii8+PHBhdGggaWQ9IlNoYXBlIiBkPSJtNDIuNjIgNy43OCA1LTRjLjQyODkwODItLjM0MzQ1MzkyLjUwMDM1NjktLjk2ODYzMDA3LjE2LTEuNC0uMzQzNDUzOS0uNDI4OTA4MTgtLjk2ODYzMDEtLjUwMDM1Njg4LTEuNC0uMTZsLTUgNGMtLjIwODk4MTMuMTY1MjIwMDYtLjM0MzM5ODYuNDA2OTk3NTUtLjM3MzQzNi42NzE3MDIyOXMuMDQ2Nzg4My41MzA0NTMwOC4yMTM0MzYuNzM4Mjk3NzFjLjE5MDYzMDEuMjM0OTkxNDcuNDc3NDExNi4zNzEwMjg4Ni43OC4zNy4yMjU1NjQyLS4wMDEzMDg0LjQ0NDA2MDctLjA3ODgzOTQxLjYyLS4yMnoiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im00LjIyIDM2LjYzYy4xOTA2MzAwNS4yMzQ5OTE1LjQ3NzQxMTU5LjM3MTAyODkuNzguMzcuMjI1NTY0MjQtLjAwMTMwODQuNDQ0MDYwNzMtLjA3ODgzOTQuNjItLjIybDUtNGMuMzE0NDQ2OS0uMjA5MDE4LjQ4NDM1NDctLjU3NzMyNTYuNDM5MjcwNi0uOTUyMjAyNy0uMDQ1MDg0Mi0uMzc0ODc3LS4yOTc0NzI0LS42OTIzOTc2LS42NTI1MTA5LS44MjA5MDA3cy0uNzUyMTg0NDItLjA0NjA3NTctMS4wMjY3NTk3LjIxMzEwMzRsLTUgNGMtLjIwODk4MTI5LjE2NTIyMDEtLjM0MzM5ODYyLjQwNjk5NzYtLjM3MzQzNjAzLjY3MTcwMjMtLjAzMDAzNzQyLjI2NDcwNDcuMDQ2Nzg4MzMuNTMwNDUzMS4yMTM0MzYwMy43MzgyOTc3eiIvPjxwYXRoIGlkPSJTaGFwZSIgZD0ibTQ2LjM4IDM2Ljc4Yy4xNzU5MzkzLjE0MTE2MDYuMzk0NDM1OC4yMTg2OTE2LjYyLjIyLjMwMjU4ODQuMDAxMDI4OS41ODkzNjk5LS4xMzUwMDg1Ljc4LS4zNy4xNjY2NDc3LS4yMDc4NDQ2LjI0MzQ3MzQtLjQ3MzU5My4yMTM0MzYtLjczODI5NzdzLS4xNjQ0NTQ3LS41MDY0ODIyLS4zNzM0MzYtLjY3MTcwMjNsLTUtNGMtLjQzMjU0OTQtLjI4NzUyMjctMS4wMTMyMDk4LS4xOTYwMzcyLTEuMzM2Mzk4NS4yMTA1NTUtLjMyMzE4ODcuNDA2NTkyMy0uMjgxMzAzOS45OTI5MjEzLjA5NjM5ODUgMS4zNDk0NDV6Ii8+PHBhdGggaWQ9IlNoYXBlIiBkPSJtNSAxOWgzYy41NTIyODQ3NSAwIDEtLjQ0NzcxNTMgMS0xcy0uNDQ3NzE1MjUtMS0xLTFoLTNjLS41NTIyODQ3NSAwLTEgLjQ0NzcxNTMtMSAxcy40NDc3MTUyNSAxIDEgMXoiLz48cGF0aCBpZD0iU2hhcGUiIGQ9Im00NCAxOWgzYy41NTIyODQ3IDAgMS0uNDQ3NzE1MyAxLTFzLS40NDc3MTUzLTEtMS0xaC0zYy0uNTUyMjg0NyAwLTEgLjQ0NzcxNTMtMSAxcy40NDc3MTUzIDEgMSAxeiIvPjwvZz48L2c+PC9nPjwvc3ZnPg==" />
                    Useful Tips
                </div>
                <div className="subpanel-list">
                    <p className="tip_style">
                        For describing the <span>incorrect and expected app behavior</span>, use <span>vocabulary that you observed</span> in the app
                    </p>
                    <p className="tip_style">
                        To <span>express a step</span>, you can use the format "I [action] [UI component or complement]". <span>Examples</span>:
                        I clicked the save button, I entered "test" in the comments, etc.
                    </p>
                    <p className="tip_style">
                        You can use "<span>This is/was the last step</span>" to finish reporting the bug or <span>click the button</span>
                        "Finish reporting the bug"
                    </p>
                    <p className="tip_style">
                        Some of the screenshots displayed by BURT are <span>for reference only</span>. <span>Input values</span>
                        and <span>UI components</span> may be different from what you actually observed in the app
                    </p>
                </div>
            </div>

        </div>
        </div>
    )

}
export default TipsOptionsPanel;
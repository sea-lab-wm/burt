import React , { useState, useEffect }  from "react";
import config from "../config";
import "./ReportGenerator.css"
import ApiClient from "../ApiClient";
const ReportGenerator = (props) => {
    // const generatedReport = props.generatedReport;
    // console.log("Generated report: " +generatedReport);
    // const link = config.serverEndpoint +"/" + generatedReport

    const generateBugReport =() => {
        const responsePromise = ApiClient.processReportPreview();
        responsePromise.then(response => {
            let conversationResponse = response.data;
            if (conversationResponse.code === 0) {
                let chatbotMsgs = conversationResponse.messages;
                let chatbotMsg = chatbotMsgs[0];
                let link = chatbotMsg.generatedReport;
                console.log(link);
                window.open(config.serverEndpoint + "/" + link, "_blank");
            } else if (conversationResponse.code === -1) {
                window.alert("Oops, there is something wrong.");
            } else {
                window.alert("There was an unexpected error");
            }
        })
    }

    return (
       // <a target="_blank" href={link}>
    <button className="bn633-hover bn28" onClick={generateBugReport} >View the bug report</button>
// </a>


    )

}


export default ReportGenerator;
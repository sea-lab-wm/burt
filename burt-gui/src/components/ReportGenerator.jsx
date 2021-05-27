import React , { useState, useEffect }  from "react";
import config from "../config";

// let bugreports = require.context('../../../data/generated_bug_reports', true);

const ReportGenerator = (props) => {
    const generatedReport = props.generatedReport;
    console.log("Generated report: " +generatedReport);
    // props.actionProvider.updateChatbotState(message)
    const link = config.serverEndpoint +"/" + generatedReport

    return (
<div>   <a target="_blank" href={link}>Bug report</a></div>
    )

}


export default ReportGenerator;
import React , { useState, useEffect }  from "react";

let bugreports = require.context('../../../data/generated_bug_reports', true);

const ReportGenerator = (props) => {
    const generatedReport = props.generatedReport;
    console.log("Generated report: " +generatedReport);
    // props.actionProvider.updateChatbotState(message)
    const link = bugreports("./" + generatedReport).default;

    return (
<div>   <a target="_blank" href={link}> the link of summary</a></div>
    )

}


export default ReportGenerator;
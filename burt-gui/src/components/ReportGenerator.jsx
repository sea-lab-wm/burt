import React , { useState, useEffect }  from "react";
import config from "../config";

const ReportGenerator = (props) => {
    const generatedReport = props.generatedReport;
    console.log("Generated report: " +generatedReport);
    const link = config.serverEndpoint +"/" + generatedReport

    return (
<div>   <a target="_blank" href={link}>Bug report</a></div>
    )

}


export default ReportGenerator;
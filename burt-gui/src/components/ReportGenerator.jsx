import React , { useState, useEffect }  from "react";
import config from "../config";
import "./ReportGenerator.css"
const ReportGenerator = (props) => {
    const generatedReport = props.generatedReport;
    console.log("Generated report: " +generatedReport);
    const link = config.serverEndpoint +"/" + generatedReport

    return (
       <a target="_blank" href={link}>
    <button className="bn633-hover bn28">bug report</button> </a>
    )

}


export default ReportGenerator;
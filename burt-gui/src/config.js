import React from "react";
import { createChatBotMessage, createClientMessage } from "react-chatbot-kit";
import AppSelector from "./components/ScreenOptions/AppSelector";
import LinkList from "./components/LinkList/LinkList";
import './config.css';
import OBScreenSelector from "./components/ScreenOptions/OBScreenSelector";
import OneScreenNoButtons from "./components/ScreenOptions/OneScreenNoButtons";
import S2RScreenSelector from "./components/ScreenOptions/S2RScreenSelector";
import ReportGenerator from "./components/ReportGenerator";
import UpdateStepsHistory from "./UpdateStepsHistory";

const config = {
    botName: "BURT",
    serverEndpoint: "http://localhost:8081",
//     serverEndpoint: "http://rocco.cs.wm.edu:21203",
    logosPath: "/app_logos/",
    saveMessagesService: "/saveMessages",
    loadMessagesService: "/loadMessages",
    getBugReportPreview: "/reportPreview",
    getStepsHistory:"/stepsHistory",
    startService: "/start",
    endService: "/end",
    processMessageService: "/processMessage",
    initialMessages: [
        createChatBotMessage("Hi there, this is BURT"),
        createChatBotMessage("I will assist you in reporting any problem with your app"),
        createChatBotMessage("Let me remind you that some of the <b>screenshots</b> that I will display are" +
            " <b>for reference only</b>"),
        createChatBotMessage("<b>Input values</b> and <b>UI components</b> may be different from what you " +
            "actually observed in the app"),
        createChatBotMessage("To start, please provide the <b>Participant ID</b> that was assigned to " +
            "you (e.g., P3)"),
    ],
    widgets: [
        {
            widgetName: "AppSelector",
            widgetFunc: (props) => <AppSelector {...props} />,
            // mapStateToProps: ["app_list", "app_values"]
        },
        {
            widgetName: "OBScreenSelector",
            widgetFunc: (props) => <OBScreenSelector {...props} />,
            mapStateToProps: []
        },
        {
            widgetName: "OneScreenNoButtons",
            widgetFunc: (props) => <OneScreenNoButtons {...props} />,
            mapStateToProps: []
        },
        {
            widgetName: "S2RScreenSelector",
            widgetFunc: (props) => <S2RScreenSelector {...props} />,
            mapStateToProps: []
        },
        {
            widgetName: "ReportGenerator",
            widgetFunc: (props) => <ReportGenerator {...props} />,
            mapStateToProps: []
        },
        {
            widgetName: " UpdateStepsHistory",
            widgetFunc: (props) => < UpdateStepsHistory {...props} />,
            mapStateToProps: []
        },

        // {
        //     widgetName: "MultipleScreensOptions",
        //     widgetFunc: (props) => <SelectMultipleScreens {...props} />,
        // },
        {
            widgetName: "javascriptLinks",
            widgetFunc: (props) => <LinkList {...props} />,
            props: {
                options: [
                    {
                        text: "Introduction to JS",
                        url:
                            "https://www.freecodecamp.org/learn/javascript-algorithms-and-data-structures/basic-javascript/",
                        id: 1,
                    },
                    {
                        text: "Mozilla JS Guide",
                        url:
                            "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide",
                        id: 2,
                    },
                    {
                        text: "Frontend Masters",
                        url: "https://frontendmasters.com",
                        id: 3,
                    },
                ],
            },
        },
    ],
};
export default config

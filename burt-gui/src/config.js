import React from "react";
import { createChatBotMessage, createClientMessage } from "react-chatbot-kit";
import AppSelector from "./components/ScreenOptions/AppSelector";
import LinkList from "./components/LinkList/LinkList";
import './config.css';
import OBScreenSelector from "./components/ScreenOptions/OBScreenSelector";
import OneScreenYesNoButtons from "./components/ScreenOptions/OneScreenYesNoButtons";
import S2RScreenSelector from "./components/ScreenOptions/S2RScreenSelector";
import ReportGenerator from "./components/ReportGenerator";
import UpdateStepsHistory from "./UpdateStepsHistory";
import YesNoButtons from "./components/ScreenOptions/YesNoButtons";

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
    processDeleteSomeStep:"/deleteSomeStep",
    initialMessages: [
        createChatBotMessage("Hi there, this is BURT"),
        createChatBotMessage("I'll assist you in reporting any problem with your app"),
        createChatBotMessage("Please <b>start a stopwatch from scratch</b> to time yourself during" +
            " this bug reporting session"),
        createChatBotMessage("To start, please provide your <b>Participant ID</b>"),
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
            widgetName: "OneScreenYesNoButtons",
            widgetFunc: (props) => <OneScreenYesNoButtons {...props} />,
            mapStateToProps: []
        },
        {
            widgetName: "YesNoButtons",
            widgetFunc: (props) => <YesNoButtons {...props} />,
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

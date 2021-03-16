import React from "react";
// Config starter code
import { createChatBotMessage } from "react-chatbot-kit";
import SelectOneScreen from "./components/ScreenOptions/SelectOneScreenOption";
import SelectMultipleScreens from "./components/ScreenOptions/SelectMultipleScreensOption";
import LinkList from "./components/LinkList/LinkList";
import './config.css';
const config = {
    serverEndpoint: "http://localhost:8081",
    saveMessagesService: "/saveMessages",
    loadMessagesService: "/loadMessages",
    startService: "/start",
    processMessageService: "/processMessage",
    initialMessages: [
        createChatBotMessage("Hi there"),
        createChatBotMessage("This is BURT. I am going to assist you in reporting the problem with your app."),
    ],
    widgets: [
        {
            widgetName: "OneScreenOption",
            widgetFunc: (props) => <SelectOneScreen {...props} />,
            mapStateToProps: ["app_list", "app_values"]
        },
        {
            widgetName: "MultipleScreensOptions",
            widgetFunc: (props) => <SelectMultipleScreens {...props} />,
        },
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
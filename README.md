<!-- <p align="center"> <img src="/code/burt-gui/src/assets/icons/chatbot-2.svg" width="50"></p> -->
<h2 align="center"> <img src="https://dl.dropboxusercontent.com/s/ymqb1cjvd7fmdca/chatbot-2.svg?dl=0" width="35"> BURT: A task-oriented dialogue system for bug reporting </h2>

## Overview

This repository contains the replication package of our ESEC/FSE'22 paper:


> Y. Song, J. Mahmud, Y. Zhou, O. Chaparro, K. Moran, A. Marcus, and D. Poshyvanyk, “**Toward Interactive Bug Reporting for (Android App) End-Users**,” in Proceedings of the 30th ACM Joint Meeting on the Foundations of Software Engineering (ESEC/FSE'22), 2022, to appear


This README will walk you through installing and using BURT. 

This README also describes the content of our ESEC/FSE'22 replication package.

## What is BURT?

BURT is a web-based task-oriented chatbot for **BU**g **R**epor**T**ing (BURT), which offers a variety of features for interactive bug reporting such as the ability to:
* Guide the user in reporting essential bug report elements (i.e., the observed behavior or OB, expected behavior or EB, and steps to reproduce the bug or S2Rs)
* Check the quality of these elements at the moment they are provided
* Offer instant feedback about issues with these elements
* Provide graphical suggestions of these elements 

You can watch a teaser video demonstration of BURT <a href="https://tinyurl.com/bcbto">here</a>.


BURT is implemented as a web application with two major software components: **the backend server** and **the frontend (GUI)**. The backend server is implemented via SpringBoot (Java) and the frontend is implemented via React (Javascript, Node.js). The frontend comunicates with the server via REST web services.

The following figure shows an overview of BURT's workflow.
<p align="center"> <img src="https://dl.dropboxusercontent.com/s/gg84imhpleb38cv/Burt-Overview.png?dl=0" width="600"></p>

## Building and deploying BURT

To build and run BURT, you need to (1) set up the development environment, (2) build/run BURT's backend server, and (3) build/run BURT's frontend.

We provide instructions for installing BURT on Windows 10 and Mac OS Big Sur. Similar steps can be performed for other operating systems and other versions of Windows and Mac.

### 1. Environment set-up

To set up the environment, complete the following steps:

**NOTE**: make sure to install the version of the tools/frameworks as specified. Also make sure these can be executed in the terminal.
1. Install the **Java Development Kit (JDK) v12**. You can downlod the JDK installer from [this website](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html){:target="_blank"}. Run the installer on your machine and follow the instructions on the screen. If you have multiple versions of the JDK installed on your machine, make sure you use JDK 12 by default (e.g., on Windows you may need to modify the `PATH` environment variable to point to the path where the 12 version is installed).
2. Install the **Maven 3.6.3** build tool. One way to do so is by downloading  Maven from [this website](https://dlcdn.apache.org/maven/maven-3/3.6.3/binaries/), decompressing the package, and adding the `bin` directory to the `PATH` environment variable. These tutorials explain other ways to install Maven: [windows](https://javabydeveloper.com/how-to-install-maven-on-windows/) or [macOS](https://mkyong.com/maven/install-maven-on-mac-osx/).
3. Install **Node.js 15.8.0 and npm 7.5.1**. You can use nvm to install node.js and npm easily, refer to these tutorials to install nvm on your [windows](https://docs.microsoft.com/en-us/windows/dev-environment/javascript/nodejs-on-windows#alternative-version-managers) or [macOS](https://github.com/nvm-sh/nvm#install--update-script).
4. Install the **yarn 1.22.5** package manager. You can run this command on the terminal to do so: `sudo npm i --global yarn@1.22.5`. You can check that Yarn is installed correctly by running: `yarn --version`.
5. Install **Git** (any version should work).
6. By default the backend server will run at the 8081 port and the frontend endpoint will be accessible via http://localhost:3000. Make sure the ports 8081 and 3000 are available on your machine.

### 2. Building/running BURT's backend server

Complete the following steps:

1. Create an empty directory on your machine (e.g., `fse-burt-project`, we recommend **not** to use `burt` as the folder name).
2. Download the installation script and save it in the directory you just created (e.g., `fse-burt-project`):
   [Windows script](https://drive.google.com/drive/folders/1JHbEMk9km2CbK4Df_Iy7T-vFS9LU6vFV?usp=sharing)
   or [Mac OS script](https://drive.google.com/drive/folders/1hYaZsm4deZza3c9mZKRKqaNV05SHYnfK?usp=sharing)
3. Open a terminal, go to the directory you created, and run the script:
   1. Windows: run `run_server.bat`
   2. Mac OS: give permissions to the script by running `chmod +x run_server.sh` and run the script `./run_server.sh`
   
   This step might take a while because the script will download all the dependencies needed to build BURT.
4. Once the script finishes, you should see on the terminal multiple messages from SpringBoot. The last message should look like this: `"... Started ConversationController in 14.845 seconds..."`

### 3. Building/running BURT's frontend

Complete the following steps:

1. Open a new terminal, go to the `fse-burt-project` directory, and go to the `burt/burt-gui` folder.
2. Run the script that executes BURT's GUI:
   1. Windows: run the `run_app.bat` script
   2. Mac Os: give permissions to the script (`chmod +x run_app.sh`) and then run it  (`./run_app.sh`)
   
   This step may take a while because the script will download all the dependencies required by BURT's GUI.
3. At this point the script should have executed successfully and your web browser should have opened showing BURT's GUI. If the browser was not opened, open it and go to http://localhost:3000 

## Reporting a bug using BURT 

After you build/deploy BURT successfully, you can now report one or more bugs with BURT.

**NOTE**: We recommend you to watch a short instructional video that explains how to use BURT via an example, the video can be found at [https://tinyurl.com/bcbto](https://tinyurl.com/bcbto). Feel free to read our [user manual](https://github.com/sea-lab-wm/burt/blob/master/data/BURT_User_Guide.pdf) to learn how to use BURT.

To do so, you can enter **P20** as participant ID, then select an app, and then report a bug on BURT.
We provide 12 bugs from six Android apps (see more details in our original paper), the bug videos can be found [here](https://github.com/sea-lab-wm/burt/tree/master/data/bug%20videos%20for%20evaluation). Feel free to watch one bug video and report it using BURT.


## ESEC/FSE'22 Replication Package

Our ESEC/FSE'22 replication package contains three main parts: BURT's source code, BURT's app execution data, and BURT's evaluation artifacts. These parts are composed of multiple directories/folders that we describe next:

### BURT's source code

BURT's source code is found in multiple sub-folders (i.e., Maven packages):

* `burt-gui`: this folder mainly contains the frontend code that implements BURT's chatbot interface via the React Chatbot Kit and the Bootstrap framework. The chat-related folders (`Chat`, `Chatbot`, `ChatbotMessage`, etc.) contain the code about the chat box,
and the `StepsPanel` folder has the code that displays the S2Rs that end-users have reported. The `TipsOptions` code implements 
the dynamic display of recommendations to end-users on how to use BURT. 

* `burt-server`: this folder implements the BURT's backend BURT via SpringBoot. It is made up of several Java packages: `actions`, `conversation`, `msgparsing`, `output`, 
`statecheckers`. In the `action` package, you will find different actions that the chatbot will perform, such as asking users to select an app or provide the OB, EB, or S2R. In the `conversation` package, you will find different kinds of message objects and states, necessary for BURT to keep track of the conversation flow. The `msgparsing` folder contains all defined intents (i.e., types of messages) and the code that obtains the intent in each round of bug reporting dialogue. The `output` folder contains the code that generates the web-based bug reports. The `statecheckers` folder contains the code
that will check the end-user's message to decide the next action that chatbot will perform. 

* `burt-quality-checker`: this folder contains the app execution model, which is a graph that stores the sequential GUI-level app interactions, and it also contains 
the code that checks the quality of the end-user's textual bug descriptions (i.e., the individual OB, EB, or S2Rs) and matches them to screens or transitions of the execution model. 

* `burt-nlparser`: this folder contains the code that parses the textual bug descriptions (i.e., the invididual OB, EB, and S2Rs) provided by end-users using dependency parsing via Stanford CoreNLP toolkit.

* `crashscope`: this folder contains the CrashScope's code, which generates app execution data in the form of sequential interactions (e.g., taps or taps) by utilizing a set of systematic exploration strategies on Android apps (e.g. top-down and bottom-up). The code will also generate the screenshot of each interaction, XML files with GUI hierarchies, and app execution information for each screen of the app (e.g., which component was interacted with). 

* `traceReplayer`: this folder contains the code that record and processes the app interactions made by humans (e.g., end users or developers) to execute app features. The code generates the screenshots, XML GUI hierarchy files, and app execution information to complement/augment the execution model (i.e., the graph). 

### BURT's app execution data

The `data` directory contains all the data that is used to build BURT's app execution graph. We describe the three types of data stored in this directory:
    
* CrashScope data: The app exploration data that is collected by CrashScope, following multiple systematic app exploration strategies. We stored the screenshot, XML GUI hierarchy, and app execution information for each screen. This data is stored in `data\CrashScope-Data`.

* Collected traces: We collected crowdsourced (i.e., human-based) usage traces from our studied apps. These traces correspond to usages of the main features of the apps in our dataset. All the collected traces are stored in `data\Collected_traces_fixed`.

* TraceReplayer data: The screenshot, XML GUI hierarchy, and app execution information are extracted from each of the screens of the collected human-based traces. We stored the trace replayer data in `data\TraceReplayer-Data`.

### BURT's evaluation data

This directory contains all the artifacts and data related to BURT's evaluation:

* BURT evaluation data: in the `evaluation\code\qualtrics_data_analyzing` folder, you can find the code that we used to process the results from the paricipant responses to the Qualtrics survey and generated the stacked bar charts that display the usefulness/ease of use of BURT's features. You can also find the code (`evaluation\code\bug_assignment.py`) that generated the random bug assignments to all participants, and the code (`evaluation\code\create_analysis_spreadsheets.py`) that processed the generated bug
reports for analysis. We also provided the original answers to our survey from 18 participants in the `evaluation\code\BURT Evaluation Survey_August-Anonymous.xlsx`, and the Excel file `evaluation\burt_result_analysis.xlsx` that stores the complete analysis of results including both the answers to the survey and the generated BURT bug reports.

* ITRAC evaluation data: in the `evaluation\code` folder, you can find the code `generate_analysis_itrac.py` and `generate_itrac_bug_reports.py` that we used to process the collected results from the participants responses to the ITRAC Qualtrics survey and generate a spreadsheet that contains the ITRAC bug reports. We also provide the original answers to our survey from 18 participants in the `evaluation\code\ITRAC-study_March.xlsx`, and the Excel file `evaluation\itrac_result_analysis.xlsx` that stores the complete analysis of the collected ITRAC bug reports.

* Bug reports: the folder `evaluation\data\generated_bug_reports` stores all the bug reports generated by participants via BURT. Another folder `evaluation\data\itrac_bug_reports` stores all the bug reports generated by participants via ITRAC.

* BURT conversations: the folder `evaluation\data\conversation_dumps` stores the conversations that each particiant had with BURT.



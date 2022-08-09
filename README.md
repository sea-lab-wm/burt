<!-- <p align="center"> <img src="/code/burt-gui/src/assets/icons/chatbot-2.svg" width="50"></p> -->
<h2 align="center"> <img src="https://dl.dropboxusercontent.com/s/ymqb1cjvd7fmdca/chatbot-2.svg?dl=0" width="35"> BURT: A task-oriented dialogue system for bug reporting </h2>

[![DOI](https://zenodo.org/badge/342777003.svg)](https://zenodo.org/badge/latestdoi/342777003) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Overview

This repository contains the replication package of our ESEC/FSE'22 paper:


> **Y. Song, J. Mahmud, Y. Zhou, O. Chaparro, K. Moran, A. Marcus, and D. Poshyvanyk, “Toward Interactive Bug Reporting for (Android App) End-Users,” in Proceedings of the 30th ACM Joint Meeting on the Foundations of Software Engineering (ESEC/FSE'22), 2022, to appear**


This README will walk you through installing and using BURT. 

This README also describes the content of our ESEC/FSE'22 replication package.

## What is BURT?

BURT is a web-based task-oriented chatbot for interactive **BU**g **R**epor**T**ing (BURT), which offers a variety of features such as the ability to:
* Guide the user in reporting essential bug report elements (i.e., the observed behavior or OB, expected behavior or EB, and steps to reproduce the bug or S2Rs);
* Check the quality of these elements at the moment they are provided;
* Offer instant feedback about any issues with these elements; and
* Provide graphical suggestions

You can watch a video demonstration of BURT <a href="https://tinyurl.com/bcbto">here</a>.


BURT is implemented as a web application with two major software components: **the backend server** and **the frontend (GUI)**. The backend server is implemented via SpringBoot (Java) and the frontend is implemented via React (Javascript, Node.js). The frontend comunicates with the backend server via REST web services.

This figure shows an overview of BURT's workflow:
<p align="center"> <img src="https://dl.dropboxusercontent.com/s/gg84imhpleb38cv/Burt-Overview.png?dl=0" width="600"></p>

## Building and deploying BURT

To build and run BURT, you need to:
1. set up the development environment, 
2. build/run BURT's backend server, and
3. build/run BURT's frontend

**NOTE**: We provide instructions for installing BURT on Windows 10 and Mac OS Big Sur. Similar steps can be performed for other operating systems and other versions of Windows and Mac.

### 1. Environment set-up

To set up BURT's build/runtime environment, complete the following steps:

**NOTE**: make sure to install the version of the tools/frameworks as specified. Also make sure these can be executed in the terminal.
1. Install the **Java Development Kit (JDK) 12**:
   1. You can download the OpenJDK 12.0.2 (build 12.0.2+10) from [this website](https://jdk.java.net/archive/).
   2. For **Windows** users, download `Windows 64-bit zip`, Unzip this and put the folder `jdk-12.0.2` in the appropriate place, e.g. `C:\Program Files\Java\jdk-12.0.2`. Then add `C:\Program Files\Java\jdk-12.0.2\bin` to system path, and set environment variable JAVA_HOME as `C:\Program Files\Java\jdk-12.0.2`, refer to these [instructions](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html) to set the variable.
   3. For **macOS** users, download `Mac 64-bit	tar.gz`, extracts the downloaded jdk-12.jdk folder to `/Library/Java/JavaVirtualMachines`, and run ``export JAVA_HOME=`/usr/libexec/java_home -v 12.0.2` ``
   4. If you have multiple JDK versions installed on your machine, make sure you use JDK 12 by default.
   5. Verify the JDK installation by running `java -version` on the terminal. You should see the specific JDK 12 version.
2. Install the **Maven 3.6.3** build tool. One way to do so is by downloading  Maven from [this website](https://dlcdn.apache.org/maven/maven-3/3.6.3/binaries/), decompressing the package, and adding the `bin` directory to the `PATH` environment variable. These tutorials explain other ways to install Maven: [Windows](https://javabydeveloper.com/how-to-install-maven-on-windows/) or [Mac OS](https://mkyong.com/maven/install-maven-on-mac-osx/).
3. Install the **Node.js 15.8.0** runtime environment and its package manager **npm 7.5.1**:
   1. **Windows**: install the *node version manager (nvm)* by downloading its installer from [here](https://github.com/coreybutler/nvm-windows/releases/download/1.1.9/nvm-setup.exe) and then running the installer on your machine. Next, open a [terminal with administrative rights](https://grok.lsu.edu/article.aspx?articleid=18026&printable=y), and run the following commands: `nvm install 15.8.0` and  `nvm use 15.8.0`. This [website](https://javascript.plainenglish.io/the-best-way-to-install-node-js-on-a-windows-pc-4481156bf63e) provides extra information.
   2. **Mac OS**: download node's .pkg installer (`node-v15.8.0.pkg`) from [here](https://nodejs.org/download/release/v15.8.0/). Open the installer and follow the instructions on the screen.
   
    Verify the node/npm installation by running the commands: `node -v` and `npm -v`.
4. Install the **Yarn 1.22.5** package manager:
   1. **Windows**: on a terminal with administrative rights, run: `npm i --global yarn@1.22.5`
   2. **Mac OS**: run `sudo npm i --global yarn@1.22.5` on the terminal.
   
   You can check Yarn is installed correctly by running: `yarn --version`.
5. Install **Git** (any version should work). A tutorial to install Git is found [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
6. By default BURT's backend server will run at the 8081 port and BURT's frontend will be accessible at http://localhost:3000. Make sure the ports 8081 and 3000 are available on your machine.

### 2. Building/running BURT's backend server

Complete the following steps:

1. Create an empty directory called `fse-burt-project` on your personal directory.
2. Download the installation script and save it in the `fse-burt-project` directory:
   [Windows](https://drive.google.com/drive/folders/1JHbEMk9km2CbK4Df_Iy7T-vFS9LU6vFV?usp=sharing)
   or [Mac OS](https://drive.google.com/drive/folders/1hYaZsm4deZza3c9mZKRKqaNV05SHYnfK?usp=sharing).
3. Open a terminal, go to the `fse-burt-project`, and run the build script:
   1. **Windows**: run `run_server.bat`.
   2. **Mac OS**: give permissions to the script (`chmod +x run_server.sh`) and then run the script (`./run_server.sh`).
   
   This step might take a while because the script will download all the dependencies needed to build BURT.
4. Once the script finishes, you should see on the terminal multiple messages from SpringBoot. The last message should look like this: `"... Started ConversationController in 14.845 seconds..."`.

### 3. Building/running BURT's frontend

Complete the following steps:

1. Open a new terminal, go to the `fse-burt-project/burt/burt-gui` directory.
2. Run the script that builds/executes BURT's frontend:
   1. **Windows**: run the `run_app.bat` script.
   2. **Mac Os**: give permissions to the script (`chmod +x run_app.sh`) and then run it  (`./run_app.sh`).
   
   This step may take a while because the script will download all the dependencies required by BURT's frontend.
3. At this point the script should have executed successfully and your web browser should have opened showing BURT's GUI. If the browser was not opened, open it and go to http://localhost:3000.

## Reporting a bug using BURT 

After you build/deploy BURT successfully, you can now report one or more bugs with BURT.

**NOTE**: We recommend you to watch a short instructional video that explains how to use BURT via an example; the video can be found [here](https://tinyurl.com/bcbto). Feel free to read our [user manual](https://github.com/sea-lab-wm/burt/blob/master/data/BURT_User_Guide.pdf) to learn how to use BURT.

To do so, you can enter **P20** as participant ID, then select an app, and then report a bug on BURT.
We provide 12 bugs from six Android apps (see more details in our original paper), the bug videos can be found [here](https://github.com/sea-lab-wm/burt/tree/master/data/bug%20videos%20for%20evaluation). Feel free to watch one bug video and report it using BURT.


## ESEC/FSE'22 Replication Package

Our ESEC/FSE'22 replication package contains three main parts: BURT's source code, BURT's app execution data, and BURT's evaluation artifacts. These parts are composed of multiple directories/folders that we describe next.

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

We use a tool called `AVT` which is a custom screen-recording desktop app that allows humans to collect screen recordings and getevent traces from a mobile device or emulator by clicking a record button. The tool internally uses the Android screen recorder that is executed by Android’s ADB tool.

### BURT's evaluation data

This directory contains all the artifacts and data related to BURT's evaluation:

* BURT evaluation data: in the `evaluation\code\qualtrics_data_analyzing` folder, you can find the code that we used to process the results from the paricipant responses to the Qualtrics survey and generated the stacked bar charts that display the usefulness/ease of use of BURT's features. You can also find the code (`evaluation\code\bug_assignment.py`) that generated the random bug assignments to all participants, and the code (`evaluation\code\create_analysis_spreadsheets.py`) that processed the generated bug
reports for analysis. We also provided the original answers to our survey from 18 participants in the `evaluation\code\BURT Evaluation Survey_August-Anonymous.xlsx`, and the Excel file `evaluation\burt_result_analysis.xlsx` that stores the complete analysis of results including both the answers to the survey and the generated BURT bug reports.

* ITRAC evaluation data: in the `evaluation\code` folder, you can find the code `generate_analysis_itrac.py` and `generate_itrac_bug_reports.py` that we used to process the collected results from the participants responses to the ITRAC Qualtrics survey and generate a spreadsheet that contains the ITRAC bug reports. We also provide the original answers to our survey from 18 participants in the `evaluation\code\ITRAC-study_March.xlsx`, and the Excel file `evaluation\itrac_result_analysis.xlsx` that stores the complete analysis of the collected ITRAC bug reports.

* Bug reports: the folder `evaluation\data\generated_bug_reports` stores all the bug reports generated by participants via BURT. Another folder `evaluation\data\itrac_bug_reports` stores all the bug reports generated by participants via ITRAC.

* BURT conversations: the folder `evaluation\data\conversation_dumps` stores the conversations that each particiant had with BURT.

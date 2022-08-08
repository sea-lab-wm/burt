<!-- <p align="center"> <img src="/code/burt-gui/src/assets/icons/chatbot-2.svg" width="50"></p> -->
<h2 align="center"> <img src="https://dl.dropboxusercontent.com/s/ymqb1cjvd7fmdca/chatbot-2.svg?dl=0" width="35"> BURT: A task-oriented dialogue system for bug reporting </h2>


## What is BURT?

BURT is a web-based task-oriented chatbot for **BU**g **R**epor**T**ing (BURT), which offers a variety of features for interactive bug reporting such as the ability to:
* Guide the user in reporting essential bug report elements (i.e. the observed behavior or OB, expected behavior or EB, and steps to reproduce the bug or S2Rs)
* Check the quality of these elements
* Offer instant feedback about issues
* Provide graphical suggestions. 

The following figure is the overview of workflow of BURT.
<p align="center"> <img src="https://dl.dropboxusercontent.com/s/gg84imhpleb38cv/Burt-Overview.png?dl=0" width="600"></p>

you can watch a teaser video demonstration of BURT <a href="https://tinyurl.com/bcbto">here</a>.

## Deploy BURT on your machine

Burt is web application built with Springboot and React, so you need to run both the backend and frontend, respectively.  

First, set up the following environment on our machine (please make sure to install the version as specified):
* install **JDK 12**, download at [here](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html)
* install **Maven 3.6.3**, download at [here](https://dlcdn.apache.org/maven/maven-3/), refer to these tutorials to install maven on your [win10](https://javabydeveloper.com/how-to-install-maven-on-windows/) or [macOS](https://mkyong.com/maven/install-maven-on-mac-osx/) 
* install **node.js 15.8.0 and npm 7.5.1**, you can use nvm to install node.js and npm easily, refer to these tutorials to install nvm on your [win10](https://docs.microsoft.com/en-us/windows/dev-environment/javascript/nodejs-on-windows#alternative-version-managers) or [macOS](https://github.com/nvm-sh/nvm#install--update-script).
* install **yarn 1.22.5**, refer to this [tutorial](https://classic.yarnpkg.com/en/docs/install#windows-stable) or [this](https://www.npmjs.com/package/yarn/v/1.22.5)
* By default the server endpoint is http://localhost:8081, so please make sure the port 8081 is free

### For Windows users:
Second, to run the server, you need to do the following steps:

1. create an empty folder on your machine (e.g. `fse-burt-project`, it is better **not** to use burt as the folder name).
2. create a script file in the `fse-burt-project` folder, there are two ways to do this:
    *  create a script file named by `run_server.bat` in this folder, and copy and paste the following commands to this script file.
    * download the script from Google Drive directly, the gdrive link is [here](https://drive.google.com/drive/folders/1JHbEMk9km2CbK4Df_Iy7T-vFS9LU6vFV?usp=sharing),        and then put this script file in this folder.
```
set CUR_DIR=%CD%
call git clone -b fse-version https://github.com/sea-lab-wm/burt.git
call git clone https://github.com/ojcchar/appcore.git
call git clone https://github.com/ojcchar/text-analyzer.git
call git clone https://github.com/ojcchar/bug_report_completion.git


rem repo update
set APPCORE_REPO_PATH=%CUR_DIR%\appcore
set TXT_ANALYZER_REPO_PATH=%CUR_DIR%\text-analyzer
set BUG_REPORT_COMPLETION_REPO_PATH=%CUR_DIR%\bug_report_completion

REM project building
cd "%APPCORE_REPO_PATH%\appcore" && call gradlew clean testClasses install && @echo on
cd "%TXT_ANALYZER_REPO_PATH%\text-analyzer" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_coding" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_patterns" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_classifier" && call gradlew clean testClasses install && @echo on
cd "%BUG_REPORT_COMPLETION_REPO_PATH%\code\bug_report_parser\bugparser" && call gradlew clean testClasses install && @echo on
cd "%CUR_DIR%"

cd burt\burt-nlparser && call mvn clean install -DskipTests && @echo on
cd burt\trace-replayer\lib && 0_install-maven-deps.bat && @echo on
cd ..\..\trace-replayer && mvn clean install -DskipTests && @echo on
cd ..\crashscope && call mvn clean install -DskipTests && @echo on
cd ..\burt-quality-checker && call mvn clean install -DskipTests && @echo on

cd "%CUR_DIR%"

cd burt\burt-server
call mvnw spring-boot:run
```
4. open a new terminal, and go to the folder with this script file. 
5. run this command: `run_server.bat`. This step might take a while because it needs to download dependencies and compile all needed packages.

Third, to run the burt gui, you need to do the following steps:

6. open a new terminal, go to the `fse-burt-project` folder, then go to the `burt\burt-gui` folder.
7. run this command: `run_app.bat`
8. open your browser, you can see BURT! (the url is http://localhost:8081) 

### For Mac users:
Second, to run the server, you need to do the following steps:
1. create an empty folder named `fse-burt-project` on your machine (e.g. `/Users/yourname/fse-burt-project`, it is better **not** to use burt as the folder name).
2. create a script file in the `fse-burt-project` folder, there are two ways to do this:
    *  create a script file named by `run_server.sh` in this folder, and copy and paste the following commands to this script file.
    * download the script from Google Drive directly, the gdrive link is [here](https://drive.google.com/drive/folders/1hYaZsm4deZza3c9mZKRKqaNV05SHYnfK?usp=sharing):        and then put this script file in this folder.
    
```
#!/bin/bash
set -x #echo on

export CUR_DIR=`pwd`
git clone -b fse-version https://github.com/sea-lab-wm/burt.git
git clone https://github.com/ojcchar/appcore.git
git clone https://github.com/ojcchar/text-analyzer.git
git clone https://github.com/ojcchar/bug_report_completion.git


export APPCORE_REPO_PATH=$CUR_DIR/appcore
export TXT_ANALYZER_REPO_PATH=$CUR_DIR/text-analyzer
export BUG_REPORT_COMPLETION_REPO_PATH=$CUR_DIR/bug_report_completion


#repo update
cd $APPCORE_REPO_PATH && git pull
cd $TXT_ANALYZER_REPO_PATH && git pull
cd $BUG_REPORT_COMPLETION_REPO_PATH && git pull


# project building
cd $APPCORE_REPO_PATH/appcore && ./gradlew clean testClasses install
cd $TXT_ANALYZER_REPO_PATH/text-analyzer && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_coding && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_patterns && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_classifier && ./gradlew clean testClasses install
cd $BUG_REPORT_COMPLETION_REPO_PATH/code/bug_report_parser/bugparser && ./gradlew clean testClasses install


cd $CUR_DIR

cd burt/burt-nlparser && mvn clean install -DskipTests
cd burt/trace-replayer/lib && ./0_install-maven-deps.sh
cd ../../trace-replayer && mvn clean install -DskipTests
cd ../crashscope && mvn clean install -DskipTests
cd ../burt-quality-checker && mvn clean install -DskipTests
cd $CUR_DIR

cd burt/burt-server
./mvnw spring-boot:run
```
4. open a new terminal, and go to the folder with this script file. 
5. run this command：`chmod +x run_server.sh`
6. run this command: `./run_server.sh`. This step might take a while because it needs to download dependencies and compile all needed packages.

Third, to run the burt gui, you need to do the following steps:

6. open a new terminal, go to the `fse-burt-project` folder, then go to the `burt/burt-gui` folder.
7. run this command: `chmod +x run_app.sh`
8. run this command: `./run_app.sh`
9. open your browser, you can see BURT! (the url is http://localhost:8081)

**NOTE** :
if you have error when you git clone some repo like "Support for password authentication was removed. Please use a personal access token instead", please go to Settings => Developer Settings => Personal Access Token => Generate New Token => Copy the generated Token, then use this token as the password.

## Test BURT 

After you deploy BURT on your machine successfully, you can try to report some bugs using BURT. 

First, we recommend you to watch a short instructional video that explained how to use BURT via an example, the video can be found at [https://tinyurl.com/bcbto](https://tinyurl.com/bcbto). You can also read our user manual to learn how ot use BURT, the user manual can be found at [here](https://github.com/sea-lab-wm/burt/blob/master/data/BURT_User_Guide.pdf)

Then, you can select a app, and report a bug on BURT. We provide 12 bugs from six Android apps (see more details in our original paper), the bug videos can he found at [here](https://github.com/sea-lab-wm/burt/tree/master/data/bug%20videos%20for%20evaluation). Feel free to watch one bug video and report it using BURT!









## Replication Package
In this package, there are three main folders: code, app_execution_data and evaluation.

### 1. Code

* `burt-gui`: this folder mainly contains the front-end code that implements a chatbot interface with other visual components 
through one React Chatbot Kit and Bootstrap. The chat-related folders (`Chat`, `Chatbot`, `ChatbotMessage`, etc.) contain the code about the chatbot interface,
and the `StepsPanel` folder has the code that displays the S2Rs that end-users have reported. The `TipsOptions` code implements 
the dynamic display of recommendations to end-users on how to use BURT. 

* `burt-server`: this folder implements the backend of BURT through Spring Boot. It is made up of several Java packages: `actions`, `conversation`, `msgparsing`, `output`, 
`statecheckers`. In the `action` package, you will find different actions that chatbot will perform, such as asking users to select app and provide OB, EB, or S2R, etc. In the `conversation` package, you will find different kinds of message objects and states. The `msgparsing` folder contains all defined intents and the code that obtains the intent in each round of dialogue. The `output` folder contains the code that generates the web-based bug reports. The `statecheckers` folder contains the code
that will check the end-user's message to decide the next action that chatbot will perform. 

* `burt-quality-checker`: this folder contains the app execution model, which is a graph that stores the sequential GUI-level app interactions, and it also contains 
the code that checks the quality of the end-user's textual bug description (OB, EB, or S2Rs) and matches them to screens or transitions of the execution model. 

* `burt-nlparser`: this folder contains the code that parses the textual bug description (OB, EB, and S2Rs) provided by end-users using dependency parsing via Stanford CoreNLP toolkit.

* `crashscope`: this folder contains the code that generates app execution data in the form of sequential interactions by utilizing a set of systematic exploration strategies on Android apps (e.g. top-down and bottom-up), the code will generate the screenshot of each interaction, XML file and app execution information for each screen of the app. 

* `traceReplayer`: this folder contains the code that processes the collected recorded traces (from humans) that execute app features and generates the screenshots, XML files, and app execution information to complement/augment the execution model (i.e., the graph). 

### 2. App execution data
    
* CrashScope data: The app exploration data is captured following multiple systematic input generation strategies. We stored the screenshot, XML and app execution information for each screen. The data of CrashScope is stored in `data\CrashScope-Data`.

* Collected traces: We collected crowdsourced (i.e., human-based) usage traces from our studied apps to have the key app features in our dataset. All the collected traces are stored in `data\Collected_traces_fixed`.

* TraceReplayer data: The screenshot, XML, and app execution information are extracted from each of the screens of the collected traces. We stored the trace-replayer data in `data\TraceReplayer-Data`.

### 3. Evaluation

* BURT evaluation: in the `evaluation\code\qualtrics_data_analyzing` folder, you can find the code that we used to process the results from the paricipant responsed to the Qualtrics survey and generated the stacked bar charts that display the usefulness/ease of use of BURT's features. You can also find the code (`evaluation\code\bug_assignment.py`) that generated the random bug assignments to all participants, and the code (`evaluation\code\create_analysis_spreadsheets.py`) that processed the generated bug
reports for analysis. We also provided the original answers to our survey from 18 participants in the `evaluation\code\BURT Evaluation Survey_August-Anonymous.xlsx`. And the Excel file `evaluation\burt_result_analysis.xlsx` that stores the whole analysis of results including both the answers to survey and generated bug reports

* ITRAC evaluation: in the `evaluation\code` folder, you can find the code `generate_analysis_itrac.py` and `generate_itrac_bug_reports.py` that we used to process the collected results from the participants responsed to the ITRAC Qualtrics survey and generate a spreadsheet that contains the ITRAC bug reports. We also provided the original answers to our survey from 18 participants in the `evaluation\code\ITRAC-study_March.xlsx`, and the Excel file `evaluation\itrac_result_analysis.xlsx` that stores the whole analysis of the collected ITRAC bug reports.

* Bug reports: the folder `evaluation\data\generated_bug_reports` stores all the bug reports generated by participants in the BURT bug reporting study. Another folder `evaluation\data\itrac_bug_reports` stores all the bug reports generated by participants in the ITRAC bug reporting study.

* Conversations: the folder `evaluation\data\conversation_dumps` stores the conversations that each particiant had with BURT.



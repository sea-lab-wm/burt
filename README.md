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
### For Windows users:

First, create a new folder on your machine (e.g. `D:\Projects\burt-project`), and open this folder `burt-project` in the terminal, then follow the next steps:
1. `git clone https://github.com/WM-SEMERU/CSCI435-21FALL-BURT-PROJECT.git`
2. `git clone https://github.com/ojcchar/appcore.git`
3. `git clone https://github.com/ojcchar/text-analyzer.git`
4. `git clone https://github.com/ojcchar/bug_report_completion.git`
5. Open the file `\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-server\update_deps_and_run_server.bat`, then change the REPOSITORIES_PATH to the directory that contains burt repo on your machine, such as `D:\Projects\burt-project`
6. make sure your java version is 12 or 11
7. make sure your maven version is 3.6.3
8. check if you have installed yarn, if not, please install yarn

Second, to run the burt server, open a new terminal, then input the next commands:
1. `cd D:\Projects\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-server` (feel free to change to your own path)
2. `update_deps_and_run_server.bat`

Third, to run the burt gui, open a new terminal, then input the next commands:
1. `cd D:\Projects\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-gui-new` (feel free to change to your own path)
2. `run_app.bat`

### For Mac users:

First, create a new folder on your machine (e.g. `/Users/yourname/burt-project`), and open this folder `burt-project` in the terminal, then follow the next steps:
1. `git clone https://github.com/WM-SEMERU/CSCI435-21FALL-BURT-PROJECT.git`
2. `git clone https://github.com/ojcchar/appcore.git`
3. `git clone https://github.com/ojcchar/text-analyzer.git`
4. `git clone https://github.com/ojcchar/bug_report_completion.git`
5. Open the file `CSCI435-21FALL-BURT-PROJECT/burt-server/update_deps_and_run_server.sh`, then change the REPOSITORIES_PATH to the directory that contains burt repo on your machine, such as `/Users/yourname/burt-project/`
6. make sure your java version is 12 or 11
7. make sure your maven version is 3.6.3
8. check if you have installed yarn, if not, please install yarn

Second, to run the burt server, open a new terminal, then input the next commands:
1. `cd /Users/yourname/burt-project/CSCI435-21FALL-BURT-PROJECT/burt-server` (feel free to change to your own path)
2. `chmod +x update_deps_and_run_server.sh`
3. `./update_deps_and_run_server.sh`

Third, to run the burt gui, open a new terminal, then input the next commands:
1. `cd Users/yourname/burt-project/CSCI435-21FALL-BURT-PROJECT/burt-gui-new` (feel free to change to your own path)
2. `chmod +x run_app.sh`
3. `./run_app.sh`

**NOTE** :
if you have error when you git clone some repo like "Support for password authentication was removed. Please use a personal access token instead", please go to Settings => Developer Settings => Personal Access Token => Generate New Token => Copy the generated Token, then use this token as the password.

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

* `result\burt`: in this folder, you can find the code (`code\generate_stacked_bar_chart.py`) that we used to process the results from the paricipant responsed to the Qualtrics survey and generated the stacked bar charts that display the usefulness/ease of use of BURT's features. You can also find the code (`code\bug_assignment.py`) that generated the random bug assignments to all participants, and the code (`code\create_analysis_spreadsheets.py`) that processed the generated bug
reports for analysis. We also provided the original answers to our survey from 18 participants in the `BURT Evaluation Survey_users.xlsx`, and the Excel file `Burt_result_analysis.xlsx` that stores the whole analysis of results including both the answers to survey and generated bug reports. 

* `result\itrac`: in this folder, you can find the code in `code\generate_analysis_itrac.py` and `code\generate_itrac_bug_reports.py` that we used to process the collected results from the participants responsed to the ITRAC Qualtrics survey and generate a spreadsheet that contains the ITRAC bug reports. We also provided the original answers to our survey from 18 participants in the `ITRAC Study_users.xlsx`, and the Excel file `Itrac_result_analysis.xlsx` that stores the whole analysis of the collected ITRAC bug reports.

* `bug_reports`: the folder `burt_bug_reports` stores all the bug reports generated by participants in the BURT bug reporting study. Another folder `itrac_bug_reports` stores all the bug reports generated by participants in the ITRAC bug reporting study.

* `conversations`: this folder stores the conversations that each particiant had with BURT



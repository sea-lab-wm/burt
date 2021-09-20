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
## For Windows users:

First, create a new folder on your machine (e.g. `D:\Projects\burt-project`), and open this folder `burt-project` in the terminal, then follow the next steps:
1. `git clone https://github.com/WM-SEMERU/CSCI435-21FALL-BURT-PROJECT.git`
2. `git clone https://github.com/ojcchar/appcore.git`
3. `git clone https://github.com/ojcchar/text-analyzer.git`
4. `git clone https://github.com/ojcchar/bug_report_completion.git`
5. Open the file `\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-server\update_deps_and_run_server.bat`, then change the REPOSITORIES_PATH to the directory that contains burt repo on your machine, such as `D:\Projects\burt-project`
6. make sure your java version is 12 
7. check if you have installed yarn, if not, please install yarn

Second, to run the burt server, open a new terminal, then input the next commands:
1. `cd D:\Projects\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-server` (feel free to change to your own path)
2. `update_deps_and_run_server.bat`

Third, to run the burt gui, open a new terminal, then input the next commands:
1. `cd D:\Projects\burt-project\CSCI435-21FALL-BURT-PROJECT\burt-gui-new` (feel free to change to your own path)
2. `run_app.bat`

## For Mac users:

First, create a new folder on your machine (e.g. `/Users/yourname/burt-project`), and open this folder `burt-project` in the terminal, then follow the next steps:
1. `git clone https://github.com/WM-SEMERU/CSCI435-21FALL-BURT-PROJECT.git`
2. `git clone https://github.com/ojcchar/appcore.git`
3. `git clone https://github.com/ojcchar/text-analyzer.git`
4. `git clone https://github.com/ojcchar/bug_report_completion.git`
5. make sure your java version is 12
6. check if you have installed yarn, if not, please install yarn

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




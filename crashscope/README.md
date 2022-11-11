# Crashscope
This directory contains the source code of Crashscope. Crashscope takes input from a set of apk files that are mentioned in [apps.txt](https://github.com/sea-lab-wm/burt/tree/tool-demo/CrashScope/apps.txt), and for each of the apps, the tool explores GUIs using a top-down or bottom-up approach performing different interactions. This tool generates screenshots of app screens and interacted GUI components. In addition, Crashscope produces GUI hierarchies in the form of XMLs and information on executions for all explorations. 

To run this tool, we will create a configuration file ```config.yaml``` and update the path including the following information.
- ```apkFile```: The absolute path of the ```apps.txt``` file. The ```apps.txt``` file contains the absolute paths of the apks for which we generate the data.
- ```aaptPath```: The absolute path of the of the build tools version.
- ```outputFolder```: The absolute path of the output directory where the data will be saved.
- ```scriptsPath```: The absolute path of the [scripts](https://github.com/sea-lab-wm/burt/tree/tool-demo/CrashScope/lib/scripts) that are required to run crashscope.
- ```androidSDKPath```: The absolute path of the android SDK.
- ```GUITraversal```: This field is a list containing GUI traversal strategy. The strategy can be either ***top-down*** or ***bottom-up*** or both.
- ```textEntry```: This filed can be ***expected-text*** or ***unexpected-text*** or ***no-text***.
- ```avdPort```: Port number on emulator
- ```adbPort```: Port number of adb server
- ```executionNum```: Execution number

An example of the ```config.yaml``` file is shown [here](https://github.com/sea-lab-wm/burt/tree/tool-demo/CrashScope/config.yaml). Run the following command to run [```crashscope.jar```](https://github.com/sea-lab-wm/burt/tree/tool-demo/CrashScope/crashscope.jar):
> java -jar crashscope.jar --config <path-to-config.yaml>
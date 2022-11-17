# TraceReplayer
This directory contains the source code of TraceReplayer. 

```AVT```: We can capture the app execution information from the app interactions made by humans. We can capture app recordings and getevent traces using the AVT tool. The detailed instructions for using this tool are [here](https://github.com/sea-lab-wm/burt/tree/tool-demo/trace-replayer/AVT-instructions.pdf). 

From an apk file and getevent traces, trace-replayer produces screenshots of apps, the components a user interacts with, and actions. In addition, the trace-replayer generates XML files containing GUI hierarchies and app execution information.

Create a configuration file ```config.yaml``` and update the path, including the following information.
- ```androidSDKPath```: The absolute path of the android SDK.
- ```pythonScriptsPath```: The absolute path of the [python scripts](https://github.com/sea-lab-wm/burt/tree/tool-demo/trace-replayer/lib/python-scripts) that are required to run trace-replayer.
- ```aaptPath```: The absolute path of the build tools version.
- ```apkPath```: The absolute path of the apk file.
- ```getEventFile```: The android getevent traces generated using the AVT tool. The instructions for using VCT tool are here (***provide download link***) .
- ```outputFolder```: The absolute path of the output directory where the data will be saved.
- ```avdPort```: Port number on emulator
- ```adbPort```: Port number of adb server
- ```executionNum```: Execution number

An example of ```config.yaml``` file is shown [here](https://github.com/sea-lab-wm/burt/tree/tool-demo/trace-replayer/config.yaml). Run the following command to run [```trace-replayer.jar```](https://github.com/sea-lab-wm/burt/tree/tool-demo/trace-replayer/trace-replayer.jar):
> java -jar trace-replayer.jar --config <path-to-config.yaml>
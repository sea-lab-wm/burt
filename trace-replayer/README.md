## Android Trace Replayer

This repository includes the code for the Android Trace Replayer. This tool takes as input a `getevent` trace and converts this trace into a tokenized representation of the actions performed during a trace by replaying the trace on a target device.

### Getting Started

To get started with this project, import the Java project into Eclipse or your favorite Java IDE. The eclipse configuration files are included with this repo. 

In order to run the code, you need to have at least a single Android emulator running. For the Android test case reuse project, we are using the Android Nexus 5X. Please see the user study instructions [here](https://www.dropbox.com/s/nwzeyphph7fiy88/Data-Collection-Study-Instructions.pdf?dl=0) for instructions on setting up this emulator.

### Running the Code

The main method that controls the trace replayer is the `src/edu/semeru/android/testing/stepextraction/TraceReplayer.java` class, and the main method of this class should be run in order to initiate the conversion. There are several different variables that need to be initialized in order for the code to run effectively. I briefly detail these variables below. 

* `androidSDKPath`: This is the path to the root of your AndroidSDK folder. This can be downloaded [here](https://developer.android.com/studio) from the Android developers website, and it is also installed automatically if you install Android Studio.
* `pythonScriptsPath`: This is the path to a series of python scripts that perform the conversion from the cryptic getevent events into coordinate based touches. This is included in this code and is located at `/lib/python-scripts`. This is the path that should be passed here.
* `device`: This is only necessary if using more than one emulator.
* `appName`: This is the name of the current application for which traces are being converted. This can be set to any user defined string.
* `appPackage`: This is the application package for the current app under analysis. This needs to be exact, as it is used to install/launch the application during the replay process.
* `mainActivity`: This is the main Activity of the app under analysis. This also must be exact as it is used to launch the application.
* `apkPath`: The is the path to the .apk file of the app that is currently under analysis. Full path should be provided.
* `geteventFile`: This is the path to the getevent `.log` file that should be replayed for the current app under analysis.
* `outputFolder`: This where you want the tokenized version of the actions to be saved. Full path name to a folder. The generated event sequences will be timestamped with the app name as they are generated.

### Modifying the output

Currently the `EventsFormatter.java` class is responsible for controlling what gets printed in the output. Basically, it is capable of printing any information that is stored in the `GUIEventVO` object, which should be anything that we need for the project. But this is where we can start to customize the token/event-based representation.

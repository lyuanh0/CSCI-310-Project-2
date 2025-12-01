# CSCI-310-Project-2: ChatPet
## Environment Setup
### Install the latest version of Android Studio (2025):
* https://developer.android.com/studio
  
**Set up the AVD.**
* If you have not set up any AVD yet, do so by selecting the menu, Tools > Device Manager.
* Choose Pixel 2.

### Setup the LLM Model:
**Check if you have ADB installed.**
* Go to Tools > SDK Manager.
* In the SDK Manager window, select SDK Tools.
* Look for Android SDK Platform-Tools. If it's not installed, check the box and click Apply to install it.
* Note the Android SDK Location displayed at the top of the SDK Manager window. This is your SDK directory.
* The adb executable is located in the platform-tools subdirectory within your SDK directory.  
&emsp;For example, if your SDK location is /Users/yourusername/Library/Android/sdk, then adb will be at /Users/yourusername/Library/Android/sdk/platform-tools/adb.
* Add the SDK directory to your PATH environment variable, e.g., by typing the following command in a terminal: `export PATH=$PATH:/Users/yourusername/Library/Android/sdk/platform-tools`

**Verify `adb` is working.**
* Open a terminal and type: `adb devices`
* This should show all the list of connected devices or emulators. Make sure you have at least one emulator or device running.

**Download the LLM model.**
* Download the file `gemma3-1b-it-int4.task` from [HuggingFace](https://huggingface.co/litert-community/Gemma3-1B-IT)
* Now push the model to your device.
* For example, if you have the model in Downloads folder: `adb push ~/Downloads/gemma3-1b-it-int4.task /data/local/tmp/llm/gemma3-1b-it-int4.task`

## Running the App
* Open Project: Open the ChatPet project in Android Studio and let Gradle sync.
* Launch the emulator and click the Run App (▶️) button to run the app

## 2.5 Improvements
* Updated the profile fragment to update greeting message when username is updated
* Udated birthday input field to expect a date
* Added additional error checking for registration
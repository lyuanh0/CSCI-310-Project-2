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
* Updated the profile fragment to update the greeting message when the username is updated
* Updated birthday input field to expect a date
* Added additional error checking for registration
* New feature: Journal entries can be favorited and the bookmark button can toggle to display all entries or only favorited entries
* Journal entries can now consider the first time the player creates the pet (meeting the owner for the first time) and the interaction that the user chatted with the pet
* Improved the search query to be able to search for dates in different formats such as mm/dd/yyyy and month names
* Improved the prompt for journal entry generation (content for journal entries are better)
* Fixed the Level up button to be only available when the XP reached the max for the level, and the level was not already the max level.
* Updated the profile fragment and updated the greeting message as soon as the username is updated.
* Sends a verification email upon account registration, which, when clicked, will see the verification status reflected in the profile tab.
* Updated the happiness meter increase by 5% upon chatting with pet.
* Fixed the implementation so tuck-in is disabled when energy is close to 100%
* Updated so that feeding increases hunger and happiness meter only.
* fixed about app crashes when the pet wakes up while the user is on another tab.

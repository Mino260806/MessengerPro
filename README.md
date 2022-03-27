
# MessengerPro
An Xposed module with extends Facebook Messenger with a bunch of exciting features

## Warning
This module is still in alpha phase, so you will most likely encounter bugs. Please open a github issue if you do.  
It will work if and only if use **Messenger version 350.0.0.7.89 Android 9.0+ release** (download from [here](https://www.apkmirror.com/apk/facebook-2/messenger/messenger-350-0-0-7-89-release/facebook-messenger-350-0-0-7-89-android-apk-download/)). This is mainly because Messenger code is obfuscated. It *may* support more versions in the future, but as of now, you should use this version.

## Overview

#### Features: (Suggestions are welcome)
- [Message formatting](https://user-images.githubusercontent.com/53614199/160291863-1684be51-e737-458e-92bd-c6af907ddf17.png). Note that it doesn't currently support nested items. Supported tokens:
  - \*bold\* &#8594; <strong>bold</strong>
  - !italic! &#8594; <em>italic</em>
  - \_underline\_ &#8594; <ins>underline</ins>
  - \-cross out\- &#8594; ~~cross out~~
- [Ability to attach any file](https://user-images.githubusercontent.com/53614199/160292179-7bc66da7-b374-46b2-8474-f360fc95a688.png). You can even attach images and their quality won't be downgraded (but no preview) !
- [Automatically watermark any image](https://user-images.githubusercontent.com/53614199/160291865-6da21f0f-eab1-4d6c-b4a6-9bf7d4d92bb4.png)  you send.

- [Show confirmation dialog](https://user-images.githubusercontent.com/53614199/160291862-4ef5ad4e-59f4-4cce-be99-59e53da91cb4.png) before calling someone to prevent accidental calls. (Inspired from [X Messenger Privacy](https://forum.xda-developers.com/t/mod-xposed-x-messenger-privacy-enable-essential-privacy-features-in-messenger.3451579/))
- [A bunch of feature\-rich commands](https://user-images.githubusercontent.com/53614199/160291859-b861c406-c41b-4138-84f5-5347139a3c89.png). Currently supported commands
  - /wikipedia \[ISO 2 letter language code\] \[article title\]
  - /reddit \[subreddit\] \[sort(optional)\]
  - /word pronounce \[word\] (/word define will be added in a future release)
  - I am open for more suggestions
- [Access to Messenger Pro settings](https://user-images.githubusercontent.com/53614199/160292411-d07eca6e-fd3c-4a92-b12a-8e94d4aa4b5e.png)
  from the top right corner. You can disable/enable the previous features to match your preferences.

## Compatibility
This module should support Android versions starting from Android 9.0. However it has only been tested on Android 12.0

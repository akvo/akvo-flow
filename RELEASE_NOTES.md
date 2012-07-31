Last changed: 10 July 2012 co

Akvo FLOW (Field Level Operations Watch) is a system to collect, manage, analyse and display geographically-referenced monitoring and evaluation data.

Read more about [Akvo FLOW](http://www.akvo.org/blog/?p=4836).
Read more about the [Akvo Platform](http://www.akvo.org/blog/?p=4822).

Akvo FLOW Field Survey app ver 1.9.36 release notes
----
8 July 2012, co

Overview
----
This release contains improvements to the FLOW Field Survey application related to the storage of files on the device, a new keystore for app generation for easier installation of updates, and a new feature to allow users to scan barcodes from within a survey on the application.

New features & enhancements
----
### New feature: Barcode question type in surveys
A new question type that allows users to scan a barcode from inside their FLOW survey while collecting data in the field. The user selects the question type from the Survey Manager on the Dashboard when creating a survey, and then on the device this question prompts the user to "Scan Barcode," which calls an external barcode scanning app, and then populates the text field with the barcode number. REQUIRES an external barcode scanning app to be installed on the device. Barcode scanning apps that have been tested for this feature are: ZXing and QuickMark.

GitHub issues:
[17](https://github.com/akvo/akvo-flow/issues/17), [59](https://github.com/akvo/akvo-flow/issues/59)

### Enhancement: Enable users to store large numbers of surveys and photos on device SD card
Users can now store very large numbers of survey zip files and photos on their device's SD card. This is necessary when data collectors are going for long periods offline, where surveys must be stored on the devices for later upload. Since there is a limit for the number of files that can be stored in each directory independent of SD card storage capacity, this is achieved using a new file structure that distributes the files into multiple directories, instead of just storing them in the SD card root directory as before.

GitHub issue:
[16](https://https://github.com/akvo/akvo-flow/issues/16)

### Enhancement: New keystore for apk generation to eliminate signature conflicts
Created and distributed a release keystore along with instructions for use so that there is a single signature for the Field Survey application. This solved the problem from previous versions where, when multiple keystores were used to generate the app, users would face a signature conflict when trying to update the app on the device that forced them to uninstall the existing app before installing an update.

GitHub issue:
[21](https://https://github.com/akvo/akvo-flow/issues/21)


Bug fixes
----
### Email address of user not displayed correctly in user management

GitHub issue:
[51](https://https://github.com/akvo/akvo-flow/issues/51)


Known Issues
----
### Compatibility issues with Android OS version 2.1

Barcode Scanning
External barcode scanning apps (QuickMark, ZXing) crash or freeze in Android OS 2.1. As a result, we have not made this feature available for the apk running on 2.1. You can still enter barcodes manually into surveys when a barcode question type is present.

GPS Status App
Cannot reliably launch GPS Status app from inside the Field Survey application running on Android OS 2.1 (Settings > GPS Status).

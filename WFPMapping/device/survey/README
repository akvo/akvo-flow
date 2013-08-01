This is the root directory of the Flow Field Survey application.

The project depends on Android version 2.0.0

Before the project will build, the survey.properties file must be placed in the following location:
res/raw/
This file contains the secret keys needed to communicate with the Flow Server instance set up for a given organization.

This file is slated to be replaced via a central configuration service in an upcoming version.




Summary of new features and changes for released versions
---------------------------------------------------------

1.10.6	Always include serverBase from survey.properties in server selection. Makes instance builds independent of res/values/arrays.xml

1.10.5	Prevent display rotation from forgetting a taken photo. Fix survey status partially-successful icon bug.

1.10.3	Report OS version to server in beacon call.

1.10.2	Shrink photos, warn about large media files, report IMEI to server.


Command line build
------------------

Create build.properties file (just copy build.properties.template) and edit it:

+ sdk.dir: this is the path your android-sdk directory.
+ key.store: path to the keystore
+ key.alias: your alias
+ key.store.password: your keystore password
+ key.alias.password: your alias password

Call ant target **flow-release, with the parameter survey.properties, which specifies the path to the survey.properties file you want to use**:

    ant flow-release -Dsurvey.properties=/path/to/your/survey.properties


The app will be named fieldSurvey-[version].apk (i.e. fieldsurvey-1.10.7.apk). 
The version number will be retrieved from AndroidManifest.xml (android:versionName) and by default, it will be located in the bin folder. 
You can override the default output directory with the property **out.dir**, setting it on the build.properties file:

    out.dir=/path/to/dir

or just passing it as a parameter to the build script (which will override the build.properties one):
    
    ant flow-release -Dout.dir=/path/to/dir

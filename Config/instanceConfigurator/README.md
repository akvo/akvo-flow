## InstanceConfigurator

## Building the JAR file

Just execute `ant` under the `instanceConfigurator` folder.
The resulting _self-contained_ jar will be located in the `dist` folder. 

Example of the output

````

$ ant

Buildfile: Config/instanceConfigurator/build.xml

makedir:

makejar:
    [javac] Compiling 1 source file to Config/instanceConfigurator/bin
     [copy] Copying 5 files to Config/instanceConfigurator/bin
     [copy] Copying 13 files to Config/instanceConfigurator/dist
      [jar] Building jar: Config/instanceConfigurator/dist/instanceConfigurator.jar

BUILD SUCCESSFUL
Total time: 1 second

````

## Running the jar

````
java -jar dist/instanceConfigurator.jar
Missing required options: on, ak, as, bn, gae
usage: org.akvo.flow.InstanceConfigurator
 -ak,--awsKey <arg>       AWS Access Key
 -as,--awsSecret <arg>    AWS Access Secret
 -bn,--bucketName <arg>   AWS S3 bucket name
 -ef,--emailFrom <arg>    Sender email - NOTE: Must be developer in GAE
                          instance
 -et,--emailTo <arg>      Recipient email of error notifications
 -gae,--gaeServer <arg>   GAE base server - https://x.appspot.com
 -on,--orgName <arg>      Organzation name
 (...)
``` 
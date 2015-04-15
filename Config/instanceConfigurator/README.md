## InstanceConfigurator

## Building the JAR file

### Requirements

* JDK 1.7+
* Apache Ant 1.7+
* Git

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
      [jar] Building jar: Config/instanceConfigurator/dist/instanceConfigurator-version.jar

BUILD SUCCESSFUL
Total time: 1 second

````
The `version` in the resulting file is the output of [`git describe`](http://git-scm.com/docs/git-describe) command.

## Running the jar

````
java -jar dist/instanceConfigurator-version.jar
Missing required options: on, ak, as, bn, gae, et, o, fs, a
usage: org.akvo.flow.InstanceConfigurator
 -a,--alias <arg>           Instance alias, e.g. instance.akvoflow.org
 -ak,--awsKey <arg>         AWS Access Key
 -as,--awsSecret <arg>      AWS Access Secret
 -bn,--bucketName <arg>     AWS S3 bucket name
 -ef,--emailFrom <arg>      Sender email - NOTE: Must be developer in GAE
                            instance
 -et,--emailTo <arg>        Recipient email of error notifications
 -fs,--flowServices <arg>   FLOW Services url, e.g.
                            http://services.akvoflow.org
 -en,--eventNotification <arg>
                            FLOW services event notification endpoint, e.g.
                            http://services.akvoflow.org:3030/event_notification
 -ce,--enableChangeEvents true/false
                            `true` if the instance should store change event data
                            and notify FLOW services event notification endpoint
                            of new events
 -gae,--gaeId <arg>         GAE instance id - The `x` in
                            https://x.appspot.com
 -o,--outFolder <arg>       Output folder for configuration files
 -on,--orgName <arg>        Organzation name
```

## NOTES

* The credentials (`--awsKey` `--awsSecret`) are from a [IAM user](https://aws.amazon.com/iam/)
  that is allowed to perform IAM and [S3](https://aws.amazon.com/s3/) operations:
  * IAM operations: create user, create group, associate user to a group, and associate security policies to a group
  * S3 operations: check if a S3 bucket exists, create bucket

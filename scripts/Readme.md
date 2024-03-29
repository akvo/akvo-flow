# Scripts to be called using RemoteAPI

Ant buil.xml script included in `/data` folder lets you call any service that implements 
`org.akvo.gae.remoteapi.RemoteAPI` included in this folder `data/src/org/akvo/gae/remoteapi`
 
``` shell
  cd data/src/org/akvo/gae/remoteapi
  ls
```

```
 | AddPermission.java                      |
 | AddUsers.java                           |
 | CheckDataPointLocation.java             |
 | CheckImages.java                        |
 | CheckMissingFiles.java                  |
 | CheckOptions.java                       |
 | CheckParentPath.java                    |
 | CheckSurveyInstance.java                |
 | CheckSurveyStructure.java               |
 | CorrectFolderSurveyPath.java            |
 | CorrectSurveyedLocale.java              |
 | DataPoints.java                         |
 | DataStoreWithUnilog.java                |
 | DataUtils.java                          |
 | DefaultUserRoles.java                   |
 | DeleteData.java                         |
 | DeleteFormCompletely.java               |
 | DeleteQuestionWithoutQuestionGroup.java |
 | DeleteSurveyIfEmpty.java                |
 | DeleteSurveyInstances.java              |
 | DeleteUsers.java                        |
 | DeviceList.java                         |
 | ExportAuthDataToEventLog.java           |
 | ExportDataToEventLog.java               |
 | ExportDataToPG.java                     |
 | ExtractImageGeotag.java                 |
 | FixDeviceFileJobQueue.java              |
 | FixSurveyedLocale.java                  |
 | InstanceStats.java                      |
 | LetsEncryptAppUrl.java                  |
 | NameEqualsCodeCleanup.java              |
 | PathToIdMigration.java                  |
 | PilotDataFix.java                       |
 | PrintTreeStructure.java                 |
 | Process.java                            |
 | ProjectMigration.java                   |
 | PublicPrivateInstanceCount.java         |
 | RemoteAPI.java                          |
 | SatStatUpdater.java                     |
 | SplitAssignments.java                   |
 | SurveyedLocaleFix.java                  |
 | UnAssignUnPublishedForms.java           |
 | UserList.java                           |
```

## calling a RemoteAPIService

``` shell
ant remoteAPI -DappId=akvoflow-uat2 -Dservice=UnifyDataPointAssignment
```

By default akvo-server-config/*/*.p12 auth files are taken from this relative path `../../../akvo-flow-server-config` but if you have another one, just specify it in the call

``` shell
ant remoteAPI -DappId=akvoflow-uat2 -Dservice=UnifyDataPointAssignment -DakvoFlowServerConfigPath=/your-path-to-akvo-flow-server-config
```

## calling a RemoteAPIService against local env

``` shell
ant devRemoteAPI -Dservice=AddUsers -Dargs=./dev_environment_users.csv
```


# FAQ

## why is better to use ANT instead of *.sh file?
Thus ANT build.xml includes java path configuration and takes care of always `compile` before calling the RemoteAPIService

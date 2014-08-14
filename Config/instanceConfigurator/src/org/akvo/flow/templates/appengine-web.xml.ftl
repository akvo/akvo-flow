<?xml version="1.0" encoding="utf-8"?>
<appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
    <application>${instanceId}</application>
    <version>1</version>
    <sessions-enabled>true</sessions-enabled>
    <threadsafe>true</threadsafe>
    <auto-id-policy>legacy</auto-id-policy>
    <static-files>
        <include path="/**.png" expiration="1000d 5h" />
        <include path="/**.jpg" expiration="1000d 5h" />
    </static-files>
    <!-- Configure java.util.logging -->
    <system-properties>
        <property name="apkS3Path" value="https://akvoflow.s3.amazonaws.com/apk/" />
        <property name="autoUpdateApk" value="true" />
        <property name="java.util.logging.config.file" value="WEB-INF/logging.properties" />
        <property name="aws_secret_key" value="${awsSecretAccessKey}" />
        <property name="aws_identifier" value="${awsAccessKeyId}" />
        <property name="photo_url_root" value="${s3url}/images/" />
        <property name="alias" value="${alias}" />
        <property name="flowServices" value="${flowServices}" />
        <property name="s3bucket" value="${awsBucket}" />
        <property name="surveyuploadurl" value="${s3url}/" />
        <property name="surveyuploaddir" value="surveys" />
        <property name="deviceZipPath" value="${s3url}/devicezip/" />
        <property name="emailFromAddress" value="${emailFrom}" />
        <property name="recipientListString" value="${emailTo};FLOW Errors recipient" />
        <property name="defaultPhotoCaption" value="${organization}" />
        <property name="attachreport" value="true" />
        <property name="bootstrapdir" value="bootstrap"/>
        <property name="imageroot" value="https://${instanceId}.appspot.com"/>
        <property name="mapiconimageroot" value="${s3url}/images/mapicons"/>
        <property name="scoreAPFlag" value="true"/>
        <property name="organization" value="${organization}"/>
        <property name="allowUnsignedData" value="true" />
        <property name="defaultOrg" value="${organization}" />
        <property name="domainType" value="locale" />
        <property name="exportedProperties" value="defaultOrg,domainType,pointTypes" />
        <property name="pointTypes" value="WaterPoint,SanitationPoint,PublicInstitution"/>
        <property name="optionRenderMode" value="radio"/>
        <property name="backendpublish" value="false"/>
        <property name="cacheExpirySeconds" value="3600"/>
        <property name="useLongDates" value="true"/>
        <property name="statusQuestionText" value="water available on the day;Yes=FUNCTIONING_HIGH;No=BROKEN_DOWN;DEFAULT=BROKEN_DOWN"/>
        <property name="mergeNearbyLocales" value="false"/>
        <property name="scoreAPDynamicFlag" value="false"/>
        <property name="asyncTaskTimeout" value="16384"/>
        <property name="enableRestSecurity" value="true"/>
        <property name="restPrivateKey" value="${apiKey}"/>
        <property name="useTabRDRFlag" value="false"/>
        <property name="showStatisticsFeature" value="false"/>
        <property name="showMonitoringFeature" value="false"/>
    </system-properties>
</appengine-web-app>

@Echo OFF
rem * JavaService installation script for Radon Server

set JAVA_HOME=C:/java/jdk5_22
set JAVASERVICE=JavaService.exe
rem set current Dir at ARADON_HOME
rem set ARADON_HOME=%cd%
set ARADON_HOME=%cd%

rem check java
if not exist "%JAVA_HOME%\jre" goto no_java

rem check parameter
if "%1" == "" goto error_exit


SET JVMDLL=%JAVA_HOME%\jre\bin\server\jvm.dll
rem if not exist "%JVMDLL%" SET JVMDLL=%JAVA_HOME%\jre\bin\hotspot\jvm.dll
rem if not exist "%JVMDLL%" SET JVMDLL=%JAVA_HOME%\jre\bin\client\jvm.dll
if not exist "%JVMDLL%" goto no_java

SET toolsjar=%JAVA_HOME%\lib\tools.jar
if not exist "%toolsjar%" goto no_java

set JARS=%ARADON_HOME%\lib\ant.jar;%ARADON_HOME%\lib\aradon_0.8.jar;%ARADON_HOME%\lib\apache_extend_fat.jar;%ARADON_HOME%\lib\iframework_2.3.jar;%ARADON_HOME%\lib\jci_fat.jar;%ARADON_HOME%\lib\org.simpleframework.jar;%ARADON_HOME%\lib\rest_fat.jar;%ARADON_HOME%\lib\netty-3.4.6.Final.jar;

rem select mode : auto, manual
set MODE=-auto


:: run command
@echo. JAVA_HOME %JAVA_HOME%
@echo. JVMDLL %JVMDLL%
@echo. ARADON_HOME %ARADON_HOME%
@echo. %1

if /i "%1" == "register" (
	@echo . Install RadonServer service
	%JAVASERVICE% -install RadonServer %JVMDLL% -Djava.class.path=%JARS%;%CLASS_PATH%; -Daradon.home.dir=%ARADON_HOME% -Xms256M -Xmx512M -start net.ion.nradon.ServerRunner -params -config:%ARADON_HOME%/resource/config/aradon-config.xml -action:restart -out %ARADON_HOME%/resource/log/server_out.log -err %ARADON_HOME%/resource/log/server_err.log %MODE%
) else if /i "%1" == "start" (
	@echo . Start RadonServer service
	net start RadonServer
) else if /i "%1" == "stop" (
	@echo . Stop RadonServer service
	net stop RadonServer
) else if /i "%1%" == "unregister" (
	@echo . UnInstall RadonServer service
	%JAVASERVICE% -uninstall RadonServer
) else (
	goto no_target_cmd
)

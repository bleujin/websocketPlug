
@Echo OFF
rem * JavaService installation script for WebSocket Server

rem set JAVA_HOME=c:\jdk
set JAVASERVICE=JavaService.exe
rem set current Dir at BASE_HOME
set BASE_HOME=%cd%

rem check java
if not exist "%JAVA_HOME%\jre" goto no_java

rem check parameter
if "%1" == "" goto error_exit


SET JVMDLL=%JAVA_HOME%\jre\bin\server\jvm.dll
if not exist "%JVMDLL%" SET JVMDLL=%JAVA_HOME%\jre\bin\hotspot\jvm.dll
if not exist "%JVMDLL%" SET JVMDLL=%JAVA_HOME%\jre\bin\client\jvm.dll
if not exist "%JVMDLL%" goto no_java

SET toolsjar=%JAVA_HOME%\lib\tools.jar
if not exist "%toolsjar%" goto no_java

set JARS=%BASE_HOME%/websocket.jar;%BASE_HOME%/lib/apache_fat.jar;%BASE_HOME%/lib/aradon_fat_0.5.jar;%BASE_HOME%/lib/netty-3.2.5.Final.jar;

rem select mode : auto, manual
set MODE=-auto


:: run command
@echo. %1
if /i "%1" == "register" (
	@echo . Install Websocket service
	%JAVASERVICE% -install Websocket %JVMDLL% -Djava.class.path=%JARS%;%CLASS_PATH%; -Mms64M -Xmx64M -start net.ion.websocket.server.ServerNodeRunner -params -config:%BASE_HOME%/resource/config/server-config.xml -basedir:%BASE_HOME% -out %BASE_HOME%/logs/server_node_out.log -err %BASE_HOME%/logs/server_node_err.log %MODE%
) else if /i "%1" == "start" (
	@echo . Start Websocket service
	net start Websocket
) else if /i "%1" == "stop" (
	@echo . Stop frontNode service
	net stop Websocket
) else if /i "%1%" == "unregister" (
	@echo . UnInstall Websocket service
	%JAVASERVICE% -uninstall Websocket
) else (
	goto no_target_cmd
)

goto end

:no_target_cmd
goto end

:no_java
@echo . This install script requires the parameter to specify Java location
@echo . The Java run-time files tools.jar and jvm.dll must exist under that location
goto error_exit

:error_exit
@echo .
@echo . Failed to install WebSocket as a system service
@echo . Command format:
@echo . %~n0.bat [register/start/stop/unregister]
@echo . 
@echo . Example:
@echo . %~n0.bat register

:end
@echo .
@pause
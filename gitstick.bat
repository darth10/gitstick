@echo off
set SCRIPT_DIR=%~dp0
java -Xmx256m -jar "%SCRIPT_DIR%\target\gitstick.jar" %*

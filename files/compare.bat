@echo on

setlocal

REM In order that required bpm libraries appear in this bat file as classpath entries, ensure that
REM library files are added to the eclipse classpath for the bpm project. Any required libraries used
REM by projects other than bpm must be registered within the "bpm/build/build.properties" file in
REM order to get automatically included as registered libraries within this batch file.

set CLASSPATH=.\lib\ojdbc8.jar;.\lib\postgresql-42.2.5.jar
set CLASSPATH=%CLASSPATH%;.\dataCompareExe.jar;

java -cp %CLASSPATH% -Xms512m -Xmx1536m com.smartstream.db.compare.DataCompare %1 %2 %3

endlocal

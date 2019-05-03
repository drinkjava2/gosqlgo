copy .\poms\pom_undertow_embedded.xml pom.xml /y 
copy .\poms\MainApp_UndertowEmbedded.txt src\main\java\com\demo\MainApp.java /y 
rd /s/q /q .\tomcat.80
call mvn clean -DskipTests package
xcopy ".\src\main\webapp\*.*" ".\target\classes\src\main\webapp\"  /S /D /Y /Q >nul
for /r .\target\repo\ %%i in (*.jar) do copy %%i .\target\classes\*.jar /y
copy .\target\*.jar .\target\classes\
cd target\classes
java -classpath ".;*" com.demo.MainApp
@pause
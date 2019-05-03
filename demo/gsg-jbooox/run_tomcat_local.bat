copy .\poms\pom_tomcat_local.xml pom.xml /y 
del src\main\java\com\demo\MainApp.java

rd /s/q /q .\tomcat.80
call mvn clean -DskipTests package 

set TomcatFolder=c:\tomcat8
rd /s/q /q %TomcatFolder%\logs
md %TomcatFolder%\logs
rd /s/q /q %TomcatFolder%\work
md %TomcatFolder%\work
rd /s/q /q %TomcatFolder%\webapps
md %TomcatFolder%\webapps 
cd target
del ROOT.war
ren *.war ROOT.war 
copy ROOT.war %TomcatFolder%\webapps\ /y 
call %TomcatFolder%\bin\startup.bat
start http://127.0.0.1 

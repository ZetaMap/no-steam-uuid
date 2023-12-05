@echo off
cmd /c ".\gradlew :build"
move /y .\build\libs\*.jar .\
rd /s /q .\build
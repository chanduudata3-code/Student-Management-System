@echo off
cd /d "%~dp0"
javac BookStoreApp.java
if errorlevel 1 (
    echo BookNest could not compile. Check that JDK 17 or newer is installed.
    pause
    exit /b 1
)
java BookStoreApp

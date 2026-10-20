@echo off
REM Compiles and runs the ATM simulator (requires JDK 17+).
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\com\atm\*.java || exit /b 1
java -cp out com.atm.ATMApplication

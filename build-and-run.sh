#!/usr/bin/env bash
# Compiles and runs the ATM simulator (requires JDK 17+).
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/com/atm/*.java
java -cp out com.atm.ATMApplication

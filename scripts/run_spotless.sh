#!/bin/bash

# Uruchom Gradle Spotless
python3 spring-boot-service/scripts/run_gradle.py spotlessApply

# Dodaj zmiany do git
git add -A

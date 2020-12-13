#!/bin/bash
docker ps | grep -v CONT | awk '{ print $1 }' | xargs docker stop
docker compose up --build

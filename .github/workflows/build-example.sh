#!/bin/bash

ACTION=$1

if [ "$ACTION" == "android" ]
then
    cd example-- \
    flutter build apk --debug
  exit
fi


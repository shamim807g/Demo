#!/bin/bash

declare -a appMap=(
    #"en=english"
    #"us=englishUS"
    #"de=german"
    #"cn=Chinese"
    #"it=Italian"
    #"pt=Portuguese"
    #"se=Swedish"
    #"pl=Polish"
    #"th=Thai"
    #"ar=Arabic"
    #"da=Danish"
    #"el=Greek"
    #"fi=Finnish"
    #"fr=French"
    #"ja=Japanese"
    #"nl=Dutch"
    #"ru=Russian"
    #"ua=Ukrainian"
    #"tr=Turkish"
    #"es=Spanish"
    #"no=Norwegian"
    #"ko=Korean"
    #"cz=Czech"
    #"sk=Slovak"
    "ro=Romanian"
    #"bg=Bulgarian"
    #"sr=Serbian"
    "vi=Vietnamese"
    "hu=Hungarian"
)

  for i in "${!appMap[@]}"; do
    echo "${appMap[$i]}" | cut -d'=' -f2
    appFlavorName=$(echo ${appMap[$i]} | cut -d'=' -f2)
    echo ${appMap[$i]} | cut -d'=' -f1
    appFlavourCodeName=$(echo ${appMap[$i]} | cut -d'=' -f1)
    eval "./gradlew uninstall${appFlavorName}Debug assemble${appFlavorName}Debug"

    eval "adb install -r app/build/outputs/apk/${appFlavorName}/debug/app-${appFlavorName}-debug.apk"
    fullFilePath="app/build/outputs/apk/${appFlavorName}/debug/app-${appFlavorName}-debug.apk"

    pkg=$(aapt dump badging $fullFilePath|awk -F" " '/package/ {print $2}'|awk -F"'" '/name=/ {print $2}')
    act=$(aapt dump badging $fullFilePath|awk -F" " '/launchable-activity/ {print $2}'|awk -F"'" '/name=/ {print $2}')
    eval "adb shell am start -n $pkg/$act"

sleep 10

eval "./gradlew assemble${appFlavorName}DebugAndroidTest"

eval "adb install -r app/build/outputs/apk/androidTest/${appFlavorName}/debug/app-${appFlavorName}-debug-androidTest.apk"
eval "adb shell am instrument -w -m    -e debug false -e class 'com.lengo.uni.UITest' com.lengo.uni.${appFlavourCodeName}.test/com.lengo.uni.AppTestRunner"

eval "adb pull /storage/emulated/0/Android/data/com.lengo.uni.${appFlavourCodeName}/files/Pictures"

eval "./gradlew uninstall${appFlavorName}Debug"

done


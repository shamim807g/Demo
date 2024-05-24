#!/bin/bash

declare -a appMap=(
    "en=english"
    "us=englishUS"
    "de=german"
    "cn=Chinese"
    "it=Italian"
    "pt=Portuguese"
    "se=Swedish"
    "pl=Polish"
    "th=Thai"
    "ar=Arabic"
    "da=Danish"
    "el=Greek"
    "fi=Finnish"
    "fr=French"
    "ja=Japanese"
    "nl=Dutch"
    "ru=Russian"
    "ua=Ukrainian"
    "tr=Turkish"
    "es=Spanish"
    "no=Norwegian"
)

  eval "./gradlew uninstallAll"
 #  for i in "${!appMap[@]}"; do
 #     echo ${appMap[$i]} | cut -d'=' -f2
 #     appFlavorName=$(echo ${appMap[$i]} | cut -d'=' -f2)
 #     echo ${appMap[$i]} | cut -d'=' -f1
 #     appFlavourCodeName=$(echo ${appMap[$i]} | cut -d'=' -f1)
 #     eval "./gradlew uninstallDebug ./gradlew uninstall${appFlavorName}Debug"
 #
 #    done
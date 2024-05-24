#!/bin/bash

for path in `adb shell ls /sdcard/Pictures/`; do
  adb pull $path
done


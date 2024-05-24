#!/bin/bash
#chmod +x uninstall_test.sh
 eval "adb shell pm list instrumentation | cut -d: -f2 | cut -d/ -f1 | xargs -n1 adb uninstall"






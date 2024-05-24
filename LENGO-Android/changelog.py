import os
import shutil
import sys

source = '/fastlane/'
rootdir = "/fastlane/metadata"
print(source)

sourceDir = os.path.join(source, "changelog")

for subdir, dirs, files in os.walk(sourceDir):
    for file in files:
        full_file_path = os.path.join(sourceDir, file)
        for subdira, dirsa, filesa in os.walk(rootdir):
            for dra in dirsa:
                if dra == "changelogs":
                    shutil.copy2(full_file_path, os.path.join(subdira, dra))
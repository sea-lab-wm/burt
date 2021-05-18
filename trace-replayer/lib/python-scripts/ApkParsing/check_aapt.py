"""
Last Edited by Richard Bonett: 3/25/2017

This program counts the apks from a json file of ApkInfos,
outputting a table of each discovered API level and the 
number of apks having that API level.

Usage:
  python3 check_aapt.py INPUT_JSON_FILE 
"""

import json
import sys
from prettytable import PrettyTable

apks = []
for i in range(1, len(sys.argv)) :
    apks += json.load(open(sys.argv[i]))

print("From %d apks..." % (len(apks)))
targets = {-1: 0}
versions = {-1: 0}
for apk in apks :
    if 'targetSdkVersion' in apk :
        if int(apk['targetSdkVersion']) in targets :
            targets[int(apk['targetSdkVersion'])] += 1
        else :
            targets[int(apk['targetSdkVersion'])] = 1
    if 'sdkVersion' in apk :
        sdk = apk['sdkVersion']
        if int(sdk) in versions :
            versions[int(sdk)] += 1
        else :
            versions[int(sdk)] = 1

table = PrettyTable()
table.field_names = ["API Level", "sdkVersion", "targetSdkVersion"]
table.align = "r"
for v in sorted(list(set(list(versions.keys()) + list(targets.keys())))) :
    if v in versions and v in targets :
        table.add_row([v, versions[v], targets[v]])
    elif v in versions :
        table.add_row([v, versions[v], 0])
    else :
        table.add_row([v, 0, targets[v]])
print("Counts of sdkVersion and targetSdkVersion for each API level")
print(table)
"""
print("Sdk Versions:")
for v in sorted(versions.keys()) :
    print(str(v) + " -- " + str(versions[v]))

print("Target Sdk Versions:")
for v in sorted(targets.keys()) :
    print(str(v) + " -- " + str(targets[v]))
"""

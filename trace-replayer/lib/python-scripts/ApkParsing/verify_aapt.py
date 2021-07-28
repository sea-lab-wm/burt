"""
Last Edited by Richard Bonett: 3/25/2017

This program counts the apks in an input json file,
outputting a table of each property count with the 
number of apks having that many properties. It also
prints the paths to any 'faulty' apk, where fault is
defined as not having a 'mainActivity' and a 
'packageName' attribute. 

Usage:
  python3 verify_aapt.py INPUT_JSON_FILE
"""

import json
import sys
from prettytable import PrettyTable

apks = json.load(open(sys.argv[1]))
faulty = []
counts = {}
for apk in apks :
    if 'mainActivity' not in apk and 'packageName' not in apk :
        if 'apkDir' in apk and 'apkName' in apk:
            faulty.append(apk['apkDir'] + '/' + apk['apkName'])
    if (len(apk.keys()) in counts.keys()) :
        counts[len(apk.keys())] += 1
    else :
        counts[len(apk.keys())] = 1

if 0 in counts :
    counts.pop(0)

print("\n" + "-"*25)    
print("Faulty apks:")
print("-"*25)
for apk in faulty :
    print(apk)

print("\n" + "-"*25)
print("Counts of Apks by Property Count:")
table = PrettyTable()
table.field_names = ["Number of Properties", "Number of Apks"]
for c in sorted(counts.keys()): 
    table.add_row([c, counts[c]])
table.align = "r"
print(table)


#!/bin/bash
set -x #echo on

CS_DATA_DIR=../data/CrashScope-Data
CS_DATA_DIR=../data/TraceReplayer-Data

function fix_json_files() {
    
    #list of json files recursively
    find $1 -name "*.json" > json_files.txt

    #for each json file, print its filename and then the contents
    while read -r line; do
        echo $line
        sed -i "" 's/2023,/2023/' $line
    done < json_files.txt
}

fix_json_files $CS_DATA_DIR
fix_json_files $TR_DATA_DIR

import csv
import fnmatch
import json
import ntpath
import os
from itertools import groupby

import pandas as pd


def write_json_line_by_line(data, file_path):
    with open(file_path, 'w') as dest_file:
        for record in data:
            print(json.dumps(record), file=dest_file)


def read_csv_to_dic_list(file_path):
    data = []
    with open(file_path) as csv_file:
        csv_reader = csv.DictReader(csv_file, delimiter=';')
        for item in csv_reader:
            data.append(item)
    return data


def read_json(file_path):
    with open(file_path) as file:
        return json.load(file)


def read_json_line_by_line(file_path):
    data = []
    with open(file_path) as sett_file:
        for item in map(json.loads, sett_file):
            data.append(item)
    return data


def find_file(pattern, path):
    result = []
    for root, dirs, files in os.walk(path):
        for name in files:
            if fnmatch.fnmatch(name, pattern):
                result.append(os.path.join(root, name))
    return result


def write_csv_from_json_list(data, output_path):
    pd.read_json(json.dumps(data)).to_csv(output_path, index=False, sep=";")


def group_dict(data, lambda_expr):
    result = {}
    data.sort(key=lambda_expr)
    for k, v in groupby(data, key=lambda_expr):
        result[k] = list(v)
    return result


def load_settings(path):
    settings_files = find_file("*.json", path)

    all_settings = {}
    for file_path in settings_files:
        setting_name = ntpath.basename(file_path).split(".")[0]
        all_settings[setting_name] = []
        with open(file_path) as sett_file:
            for retrieval_run in map(json.loads, sett_file):
                all_settings[setting_name].append(retrieval_run)

    return all_settings

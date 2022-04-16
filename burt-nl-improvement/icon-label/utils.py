import os
import csv
import json
import re

def camel_case_split(text):
    return re.sub(r'(?<!^)(?=[A-Z])', '_', text).lower()


def update_json():
    json_path = 'data/2/sample_title.json'
    data = json.load(open(json_path, 'r'))
    for item in data:
        text = item["content"].split()
        content = ''
        print('before', text)
        for word in text:
            word = camel_case_split(word)
            # split by '_'
            if '_' in word:
                word = ' '.join(word.split('_'))
                content = content + word
            else:
                content = content + ' ' + word
        content = str.lower(content)
        print('after', content)
        item['content'] = content
    json.dump(data, open('data/2/sample_title_.json', 'w'))


def update_csv():
    csv_path = 'data/2/results-title_/all.csv'
    csv_path_ = 'data/2/results-title_/all_.csv'
    all_table = [row for row in csv.reader(open(csv_path, 'r'))]
    json_path = 'data/2/sample_title_.json'
    data = json.load(open(json_path, 'r'))
    table = [['id', 'gt', 'coala', 'content']]
    content_dict = dict()
    for line_no, row in enumerate(all_table):
        if line_no == 0:
            continue
        content_dict[row[0]] = row
    for item in data:
        if str(item['id']) in content_dict:
            table.append(content_dict[str(item['id'])] + [item['content']])
        else:
            table.append([item['id'], item['content'], '', item['content']])
    csv.writer(open(csv_path_, 'w')).writerows(table)


if __name__ == '__main__':
    update_json()
    # update_csv()
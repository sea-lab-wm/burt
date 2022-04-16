import os
import csv


if __name__ == '__main__':
    img_path = 'data/1/s0'
    # load all
    # csv_path = '/Users/zhouying/git/burt-project/burt/burt-nl-improvement/icon-label/out/results-0415164609/all.csv'
    csv_path = '/data/1/results-0415154832/all.csv'
    all_set = set()
    table = [row for row in csv.reader(open(csv_path, 'r'))]
    for line_no, row in enumerate(table):
        if line_no == 0:
            continue
        all_set.add(row[0])

    for filename in os.listdir(img_path):
        if not filename.endswith('.png'):
            continue
        index = filename.replace('.png', '')
        if index not  in all_set:
            print(index)

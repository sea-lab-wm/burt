# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


# the green button in the gutter to run the script.
import collections
import random
import xlsxwriter


def generateExcel(results):
    workbook = xlsxwriter.Workbook('demo.xlsx')
    worksheet = workbook.add_worksheet()
    row = 0
    column = 0
    header = ["bugID", "system", "order"]
    print(results)
    for key, value in results.items():
        print(key, value)
        # key = p1
        # values = OrderedDict([('itracker', ['bug4', 'bug3', 'bug5', 'bug6']), ('fusion', ['bgu11', 'bug1',
        # 'bug2', 'bug9']), ('burt', ['bug10', 'bug12', 'bug8', 'bug7'])])
        worksheet.write(row, column, key)
        row += 1
        for i in range(3):
            worksheet.write(row, i, header[i])
        row += 1
        for system, bugs in value.items():
            print(system, bugs)
            for j in range(len(bugs)):  # column = 0
                worksheet.write(row, column, bugs[j])
                column += 1
                worksheet.write(row, column, system)
                row += 1
                column -= 1
    workbook.close()


if __name__ == '__main__':
    bugs = ["bug1", "bug2", "bug3", "bug4", "bug5", "bug6", "bug7", "bug8", "bug9", "bug10", "bgu11", "bug12"]
    systems = ["burt", "fusion", "itracker"]

    participants = ["p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12"]
    assignment = {}
    for p in participants:
        assignment[p] = []
        random.shuffle(bugs)
        assignment[p].append(bugs[0:4])
        assignment[p].append(bugs[4:8])
        assignment[p].append(bugs[8:12])

    # assign different bug reporting systems for the bugs
    results = dict()
    for k, v in assignment.items():
        results[k] = collections.OrderedDict()
        random.shuffle(systems)
        for i in range(len(systems)):
            results[k][systems[i]] = v[i]

    # for k, v in results.items():
    #     print(k, v)

    generateExcel(results)

import csv
import os
import pandas as pd
from csv import reader
from lxml import html
import codecs
from pandas import read_excel
import xlrd
from pandas.tests.io.excel.test_xlrd import xlwt
import pdfkit

def read_xlsx(file_name):

    wb = xlrd.open_workbook(file_name)

    sh = wb.sheet_by_name('Sheet0')
    # print(sh.nrows)
    # print(sh.ncols)
    # print(sh.cell(0, 0).value)
    # print(sh.row_values(0))

    # print(dict(zip(sh.row_values(0), sh.row_values(1))))
    # participant: 17
    # bug id: 18
    # bug summary: 19
    # OB: 20
    # EB: 21
    # S2R: 22
    # COMMENTS: 23
    # 1_screen_id: 24
    # 1_screen_name: 25
    # 1_screen_size: 26
    # 1_screen_type: 27

    # 1_screen_id: 28
    # 1_screen_name: 29
    # 1_screen_size: 30
    # 1_screen_type: 31

    # 1_screen_id: 32
    # 1_screen_name: 33
    # 1_screen_size: 34
    # 1_screen_type: 35

    out_path = "new_survey_result.xls"
    data = xlwt.Workbook(encoding='utf-8')
    row = 0
    sheet = data.add_sheet('Sheet1', cell_overwrite_ok=True)
    header = ["participant", "bug_id" ,"text", "text_type"]
    for i in range(len(header)):
        sheet.write(row, i, header[i])
    row += 1

    for i in range(2, sh.nrows):

        participant_id = sh.row_values(i)[17].strip()
        bug_id = sh.row_values(i)[18]
        bug_summary = sh.row_values(i)[19]
        bug_ob = sh.row_values(i)[20]
        bug_eb = sh.row_values(i)[21]
        bug_s2r = sh.row_values(i)[22]
        bug_comments = sh.row_values(i)[23]

        screen_id_1 = sh.row_values(i)[24]
        screen_name_1 = sh.row_values(i)[25]
        screen_size_1 = sh.row_values(i)[26]
        screen_type_1 = sh.row_values(i)[27]

        screen_id_2 = sh.row_values(i)[28]
        screen_name_2 = sh.row_values(i)[29]
        screen_size_2 = sh.row_values(i)[30]
        screen_type_2 = sh.row_values(i)[31]

        bug_summary_line = [participant_id, bug_id, bug_summary, "summary"]
        for i in range(len(bug_summary_line)):
            sheet.write(row, i, bug_summary_line[i])
        row += 1

        bug_ob_line = [participant_id, bug_id, bug_ob, "OB"]
        for i in range(len(bug_ob_line)):
            sheet.write(row, i, bug_ob_line[i])
        row += 1

        bug_eb_line = [participant_id, bug_id, bug_eb, "EB"]
        for i in range(len(bug_eb_line)):
            sheet.write(row, i, bug_eb_line[i])
        row += 1

        s2r_list = bug_s2r.split("\n")

        for j in range(len(s2r_list)):
            step = s2r_list[j]
            bug_step_line = [participant_id, bug_id, step, "S2R"]
            for i in range(len(bug_step_line)):
                sheet.write(row, i, bug_step_line[i])
            row += 1
        if bug_comments:
            bug_comments_line = [participant_id, bug_id, bug_comments, "comments"]
            for i in range(len(bug_comments_line)):
                sheet.write(row, i, bug_comments_line[i])
            row += 1

        if screen_id_1:
            bug_screenshot_line = [participant_id, bug_id, screen_name_1, "screen_option"]
            for i in range(len(bug_screenshot_line)):
                sheet.write(row, i, bug_screenshot_line[i])
            row += 1

        if screen_id_2:
            bug_screenshot_line = [participant_id, bug_id, screen_name_2, "screen_option"]
            for i in range(len(bug_screenshot_line)):
                sheet.write(row, i, bug_screenshot_line[i])
            row += 1


    data.save(out_path)



if __name__ == '__main__':
    file_name = "ITRACKER-study_March 13, 2022_09.53.xlsx"  # File name

    read_xlsx(file_name)
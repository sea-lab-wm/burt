import csv
import os
import pandas as pd
from csv import reader
from lxml import html
import codecs
from pandas import read_excel
import xlrd
from pandas.tests.io.excel.test_xlrd import xlwt
import webbrowser
import pdfkit
from weasyprint import HTML
def generate_bug_reports(file_name):

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

    data = xlwt.Workbook(encoding='utf-8')
    row = 0
    sheet = data.add_sheet('Sheet1', cell_overwrite_ok=True)
    header = ["participant", "bug_id" ,"text", "text_type"]
    for i in range(len(header)):
        sheet.write(row, i, header[i])
    row += 1


    for i in range(2, sh.nrows):
    # for i in range(10, 11):

        participant_id = sh.row_values(i)[17].strip()
        responsible_id = sh.row_values(i)[8].strip()
        bug_id = sh.row_values(i)[18]
        bug_summary = sh.row_values(i)[19]
        bug_ob = sh.row_values(i)[20]
        bug_eb = sh.row_values(i)[21]
        bug_s2r = sh.row_values(i)[22]
        s2r_list = bug_s2r.split("\n")
        html_s2r = ""
        for step in s2r_list:
            new_step = "<p>" + step + "</p>"
            html_s2r += new_step

        bug_comments = sh.row_values(i)[23]

        screen_id_1 = sh.row_values(i)[24]
        screen_name_1 = sh.row_values(i)[25]
        screen_size_1 = sh.row_values(i)[26]
        screen_type_1 = sh.row_values(i)[27]

        screen_id_2 = sh.row_values(i)[28]
        screen_name_2 = sh.row_values(i)[29]
        screen_size_2 = sh.row_values(i)[30]
        screen_type_2 = sh.row_values(i)[31]

        screen_id_3 = sh.row_values(i)[32]
        screen_name_3 = sh.row_values(i)[33]
        screen_size_3 = sh.row_values(i)[34]
        screen_type_3 = sh.row_values(i)[35]

        screenshot_html = ""
        if screen_id_1:
            screenshot_name = "screenshots/" + responsible_id + "_" + screen_name_1
            screenshot_name_html_1 = "<img src=\"" + screenshot_name + "\"  width=\"200\" height=\"400\" >"
            screenshot_html += screenshot_name_html_1
        if screen_id_2:
            screenshot_name = "screenshots/" + responsible_id + "_" + screen_name_2
            screenshot_name_html_2 = "<img src=\"" + screenshot_name + "\" width=\"200\" height=\"400\">"
            screenshot_html += "&nbsp  &nbsp"
            screenshot_html += screenshot_name_html_2
        if screen_id_3:
            screenshot_name = "screenshots/" + responsible_id + "_" + screen_name_3
            screenshot_name_html_3 = "<img src=\"" + screenshot_name + "\" width=\"200\" height=\"400\">"
            screenshot_html += "&nbsp  &nbsp"
            screenshot_html += screenshot_name_html_3



        html_file_name = participant_id + "_" + bug_id + "_" + responsible_id + ".html"
        path = os.path.join("itracker_bug_reports", html_file_name )
        f = open(path,'w')

        message = """
        
        <html>
        <head></head>
        <body>
        <p><b>Bug summary:</b></p>
        <p>%s</p>
        <hr />
        <p><b>Observed Behavior (a description of what happened with the app): </b></p>
        <p>%s</p>
        <hr />
        
        <p><b>Expected Behavior (a description of what you expected to happen): </b></p>
        <p>%s</p>
        <hr />
        
        <p><b>Steps to Reproduce (the steps to reproduce the problem): </b></p>
        <p>%s</p>
        <hr />
        
        <p><b>Additional information: </b></p>
        <p>%s</p>
        <hr />
        
        <p><b>Attachments: </b></p>       
        <p>%s</p>
        <hr />
        </body>
        </html>"""%(bug_summary, bug_ob, bug_eb, html_s2r, bug_comments, screenshot_html)


        f.write(message)

        f.close()
        pdf_file_name = participant_id + "_" + bug_id + "_" + responsible_id + ".pdf"
        pdf_path = os.path.join("itracker_bug_reports_pdf", pdf_file_name )

        HTML(path).write_pdf(pdf_path)

            #
    # bug_summary_line = [participant_id, bug_id, bug_summary, "summary"]
    #         for i in range(len(bug_summary_line)):
    #             sheet.write(row, i, bug_summary_line[i])
    #         row += 1
    #
    #         bug_ob_line = [participant_id, bug_id, bug_ob, "OB"]
    #         for i in range(len(bug_ob_line)):
    #             sheet.write(row, i, bug_ob_line[i])
    #         row += 1
    #
    #         bug_eb_line = [participant_id, bug_id, bug_eb, "EB"]
    #         for i in range(len(bug_eb_line)):
    #             sheet.write(row, i, bug_eb_line[i])
    #         row += 1
    #
    #         s2r_list = bug_s2r.split("\n")
    #
    #         for j in range(len(s2r_list)):
    #             step = s2r_list[j]
    #             bug_step_line = [participant_id, bug_id, step, "S2R"]
    #             for i in range(len(bug_step_line)):
    #                 sheet.write(row, i, bug_step_line[i])
    #             row += 1
    #         if bug_comments:
    #             bug_comments_line = [participant_id, bug_id, bug_comments, "comments"]
    #             for i in range(len(bug_comments_line)):
    #                 sheet.write(row, i, bug_comments_line[i])
    #             row += 1
    #
    #         if screen_id_1:
    #             bug_screenshot_line = [participant_id, bug_id, screen_name_1, "screen_option"]
    #             for i in range(len(bug_screenshot_line)):
    #                 sheet.write(row, i, bug_screenshot_line[i])
    #             row += 1
    #
    #         if screen_id_2:
    #             bug_screenshot_line = [participant_id, bug_id, screen_name_2, "screen_option"]
    #             for i in range(len(bug_screenshot_line)):
    #                 sheet.write(row, i, bug_screenshot_line[i])
    #             row += 1
    #
    #
    #   data.save(out_path)



if __name__ == '__main__':
    file_name = "ITRACKER-study_March.xlsx"  # File name
    if not os.path.exists('itrac_bug_reports'):
        os.makedirs('itrac_bug_reports')
    if not os.path.exists('itrac_bug_reports_pdf'):
        os.makedirs('itrac_bug_reports_pdf')

    generate_bug_reports(file_name)
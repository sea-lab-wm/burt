import csv
import os

from lxml import html
import utils


def read_bug_reports(bug_reports_folder, survey_answers, assignment_list, participant_id):
    print(assignment_list)

    bug_report_ids = [[survey_answers["Q181#2_1_1"], survey_answers["Q181#1_1_1"]],
                      [survey_answers["Q181#2_2_1"], survey_answers["Q181#1_2_1"]],
                      [survey_answers["Q181#2_3_1"], survey_answers["Q181#1_3_1"]]
                      ]
    bug_reports = []

    for bug_report_id in bug_report_ids:
        bug_id = bug_report_id[0]
        html_file_name = bug_report_id[1] + ".html"

        ##---------------------------------------------

        # validate the bug id is correct

        current_assignment = None
        for assignment in assignment_list:
            if assignment["Bug"].replace("TOKEN", "TOK") == bug_id:
                current_assignment = assignment
                break

        if current_assignment is None:
            raise ValueError('Bug id is invalid for participant: ' + bug_id + " - " + participant_id)

        ##---------------------------------------------

        bug_report_file = os.path.join(bug_reports_folder, html_file_name)
        print(bug_report_file)

        with open(bug_report_file, 'r') as file:
            data = file.read()
            tree = html.fromstring(data)
            bug_reports.append({
                "link": "http://rocco.cs.wm.edu:21203/" + html_file_name,
                "html_tree": tree,
                "Bug_ID": current_assignment["Bug"].replace("TOKEN", "TOK") ,
                "App": current_assignment["App"],
                "Reporting_order": current_assignment["Reporting order"]
            })

    return bug_reports


def computes_stats(bug_report):
    print(bug_report)
    xpath_steps = "//div[starts-with(@id,'row')]/div[starts-with(@id,'step')]"
    xpath_s2r_missing_screens = "//div[starts-with(@id,'row')]/div[starts-with(@id,'step')]/img[contains(@src," \
                                "'NO_SCREEN_AVAILABLE')]"
    xpath_obeb_missing_screens = "//div[@id = 'obeb']/div/img[contains(@src,'NO_SCREEN_AVAILABLE')]"
    return {
        "BR_link": bug_report["link"],
        "#_steps": len(bug_report["html_tree"].xpath(xpath_steps)) - 1,
        "#_missing_ob_eb_screens": len(bug_report["html_tree"].xpath(xpath_obeb_missing_screens)),
        "#_missing_s2r_screens": len(bug_report["html_tree"].xpath(xpath_s2r_missing_screens)),
    }


def write_csv_file(statistics_records, file_name):
    column_names = statistics_records[0].keys()
    with open(file_name, 'w', newline='') as output_file:
        dict_writer = csv.DictWriter(output_file, column_names)
        dict_writer.writeheader()
        dict_writer.writerows(statistics_records)


def get_bug_report_info(bug_report, participant_id, brLink):
    bug_report_records = []

    base_record = {
        "Participant": participant_id,
        "Px": get_px(participant_id),
        "Bug_ID": bug_report["Bug_ID"],
        "App": bug_report["App"],
        "Reporting_order": bug_report["Reporting_order"],
        "BR_link": brLink,
        "text": "",
        "text_type": "",
        "missing_screen?": ""
    }

    bug_report_records.append(base_record)

    # ------------------------------

    ob_element = bug_report["html_tree"].xpath("//div[@id = 'obeb']/div[@id='ob']")[0]

    ob_record = dict(base_record)
    ob_record["text"] = ob_element.xpath("p/text()")[0]
    ob_record["text_type"] = "OB"
    ob_record["missing_screen?"] = "x" if len(ob_element.xpath("img[contains(@src,'NO_SCREEN_AVAILABLE')]")) > 0 else ""

    bug_report_records.append(ob_record)

    # ---------------------------------
    eb_element = bug_report["html_tree"].xpath("//div[@id = 'obeb']/div[@id='eb']")[0]

    eb_record = dict(base_record)
    eb_record["text"] = eb_element.xpath("p/text()")[0]
    eb_record["text_type"] = "EB"
    eb_record["missing_screen?"] = "x" if len(eb_element.xpath("img[contains(@src,'NO_SCREEN_AVAILABLE')]")) > 0 else ""

    bug_report_records.append(eb_record)

    # ---------------------------------

    steps_elements = bug_report["html_tree"].xpath("//div[starts-with(@id,'row')]/div[starts-with(@id,'step')]")

    for index, step_element in enumerate(steps_elements):
        if index == 0:
            continue
        s2r_text = step_element.xpath("p/text()")[0]

        s2r_record = dict(base_record)
        s2r_record["text"] = s2r_text
        s2r_record["text_type"] = "S2R"
        s2r_record["missing_screen?"] = "x" if len(
            step_element.xpath("img[contains(@src,'NO_SCREEN_AVAILABLE')]")) > 0 else ""

        bug_report_records.append(s2r_record)

    return bug_report_records


def get_px(participant_id):
    return participant_id.replace("P", "")


if __name__ == '__main__':

    processed_participants = ["P1", "P1", "P3", "P6", "P8", "P9", "P13", "P19" , "P2", "P7", "P17", "P24"]

    survey_answers_file = "BURT ICSE’22 Evaluation Survey_August 23, 2021_08.02.csv"
    assignment_file = "Bug assignment for participant - Bug-assignment.csv"
    bug_reports_folder = os.path.join("../data/generated_bug_reports")
    output_file_name1 = "Statistics.csv"
    output_file_name2 = "Bug_Analysis.csv"

    answers = list(csv.DictReader(open(survey_answers_file)))

    answers_by_participants = utils.group_dict(answers, lambda rec: rec['Q2.1'])

    # print(answers_by_participants)

    bug_assignments = list(csv.DictReader(open(assignment_file)))
    bug_assignments_by_participant = utils.group_dict(bug_assignments, lambda rec: rec['Participant'])

    statistics_records = []
    analysis_records = []

    # print(bug_assignments_by_participant)

    # ------------------------------

    for participant_id_lower, assignment_list in bug_assignments_by_participant.items():

        participant_id = participant_id_lower.upper()

        if participant_id not in answers_by_participants:
            continue

        if participant_id in processed_participants:
            continue

        ##-------------------------------------

        part_answers = answers_by_participants[participant_id][0]
        bug_reports = read_bug_reports(bug_reports_folder, part_answers, assignment_list, participant_id)

        for bug_report in bug_reports:
            stats = computes_stats(bug_report)

            brLink = stats["BR_link"]

            stat_record = {
                "Participant": participant_id,
                "Px": get_px(participant_id),
                "Bug_ID": bug_report["Bug_ID"],
                "App": bug_report["App"],
                "Reporting_order": bug_report["Reporting_order"],
                "BR_link": brLink,
                "#_missing_ob_eb_screens": stats["#_missing_ob_eb_screens"],
                "#_steps": stats["#_steps"],
                "#_missing_s2r_screens": stats["#_missing_s2r_screens"]
            }

            # -----------------

            bug_report_records = get_bug_report_info(bug_report, participant_id, brLink)

            statistics_records.append(stat_record)
            analysis_records.extend(bug_report_records)

    # ------------------------------

    statistics_records = sorted(statistics_records, key=lambda rec: int(rec["Px"]))
    analysis_records = sorted(analysis_records, key=lambda rec: int(rec["Px"]))

    #-------------------------------

    write_csv_file(statistics_records, output_file_name1)
    write_csv_file(analysis_records, output_file_name2)

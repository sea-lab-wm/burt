import random
import xlsxwriter


def generateExcel(bug_assignment):
    workbook = xlsxwriter.Workbook('demo1.xlsx')
    worksheet = workbook.add_worksheet()
    row = 0

    header = ["Participant", "Bug", "Reporting order"]
    for i in range(len(header)):
        worksheet.write(row, i, header[i])
    row += 1
    for participant_id, bugs in bug_assignment.items():
        print(participant_id, bugs)
        # key = p1
        # values = ['TIME-CC4', 'GROW-RC', 'APOD-RB']
        column = 0
        for i in range(len(bugs)):
            worksheet.write(i + row, column, participant_id)  # row = 1 column
            column += 1
            worksheet.write(i + row, column, bugs[i])
            column += 1
            worksheet.write(i + row, column, i + 1)
            column = 0
        row += len(bugs)

    workbook.close()


if __name__ == '__main__':
    participants = []
    dict_number_of_bug = {}
    bugs = ["APOD-CC3", "APOD-RB", "DROID-CC5", "DROID-CC6", "GNU-CC9", "GNU-RC", "GROW-CC5", "GROW-RC", "TIME-CC1",
            "TIME-CC4", "TOKEN-CC2", "TOKEN-CC7"]
    app_bugs_map = {"APOD": ["APOD-CC3", "APOD-RB"], "DROID": ["DROID-CC5", "DROID-CC6"], "GNU": ["GNU-CC9", "GNU-RC"],
                    "GROW": ["GROW-CC5", "GROW-RC"], "TIME": ["TIME-CC1", "TIME-CC4"],
                    "TOKEN": ["TOKEN-CC2", "TOKEN-CC7"]}
    # apps = ["APOD", "DROID", "GNU", "GROW", "TIME", "TOKEN"]

    for bug in bugs:
        dict_number_of_bug[bug] = 6

    for i in range(24):
        participants.append("p" + str(i + 1))
    bug_assignment = {}
    for participant in participants:
        # print(app_bugs_map)
        # select three bugs randomly

        bugs_of_each_participant = []
        app_samples = random.sample(list(app_bugs_map.keys()), min(3, len(app_bugs_map.keys())))
        # print(app_samples)
        for app in app_samples:  # app = "APOD", "DROID","GROW"
            bugs_of_app = app_bugs_map[app]  # bugs_of_app = ["DROID-CC5", "DROID-CC6"]
            bug = random.sample(bugs_of_app, 1)[0]
            bugs_of_each_participant.append(bug)
            if dict_number_of_bug[bug] > 0:
                dict_number_of_bug[bug] -= 1
                if dict_number_of_bug[bug] == 0:
                    app_bugs_map[app].remove(bug)
                    if not app_bugs_map[app]:  # bugs_of_bug is empty
                        app_bugs_map.pop(app, None)

        bug_assignment[participant] = bugs_of_each_participant
        # print(bugs_of_each_participant)
    # generate spreadsheet

    # for key, value in bug_assignment.items():
    #     print(key)
    #     print(value)
    generateExcel(bug_assignment)


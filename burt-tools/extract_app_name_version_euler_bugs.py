import json
import os
import pathlib
import sys

if __name__ == "__main__":
    # tr_data_folder = "../data/TraceReplayer-Data"

    # TR data for EULER
    tr_data_folder = "../../GPT4BugReporting/Data/TR-data/Euler"

    #bug_ids = [22, 45, 54, 76, 91, 92, 101, 106, 110, 158, 160, 162, 168, 178, 192, 198, 199, 200, 228, 248, 1033, 1150,
    #           1153, 1197, 1198, 1201, 1228, 1389, 1425, 1446, 1563, 1568, 1641]

    # BL bug ids
    # bug_ids = [11, 45, 55, 56, 71, 84, 87, 106, 110, 159, 168, 193, 201, 227, 248, 271, 275, 1028, 1089, 1130, 1146, 1147, 1151, 1205, 1213, 1222, 1223, 1402, 1403, 1406, 1428, 1445, 1563, 1640, 1641]
    # EULER bug ids
    bug_ids = [615, 49, 81, 53, 663, 616, 1, 104, 25, 10, 618, 699, 633, 25, 46, 620, 169, 154, 701, 35, 471, 12, 64, 65]

    # list folders in tr_data_folder
    folders = os.listdir(tr_data_folder)
    #print(folders)

    for bug_folder in folders:
        #print(bug_folder)

        # skip if .DS_Store
        if bug_folder == ".DS_Store":
            continue

        # extract bug id from folder name
        # print(bug_folder.split("TR"))

        # bug_id = int(bug_folder.split("TR")[1])
        # Bug ID for EULER bugs
        bug_id = int(bug_folder.split("_")[-1])

        # skip if not in bug_ids
        # if bug_id not in bug_ids:
        #     #print(f"skipping {bug_id}")
        #     continue

        bug_folder_path = os.path.join(tr_data_folder, bug_folder)
        # print(bug_folder_path)

        for json_file_path in pathlib.Path(os.path.join(tr_data_folder, bug_folder)).glob('*.json'):

            #print(json_file_path)
            if not os.path.isfile(json_file_path):
                continue



            # read json file
            with open(json_file_path, 'r') as json_file:
                json_data = json.load(json_file)

                # get app name
                app_name = json_data["app"]['name']

                # get app version
                app_version = json_data["app"]['version']

                package = json_data["app"]['packageName']

                #print(app_name, package)
                print(f'new Triplet<>("{bug_id}", "{app_name}", "{app_version}"),')

            break



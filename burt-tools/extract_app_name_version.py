


import json
import os
import pathlib


if __name__ == "__main__":
    tr_data_folder = "/Users/ojcchar/repositories/Projects/burt/data/TraceReplayer-Data"


    bug_ids = [22, 45, 54, 76, 91, 92, 101, 106, 110, 158, 160, 162, 168, 178, 192, 198, 199, 200, 228, 248, 1033, 1150, 1153, 1197, 1198, 1201, 1228, 1389, 1425, 1446, 1563, 1568, 1641]

    #list folders in tr_data_folder
    folders = os.listdir(tr_data_folder)
    
    for bug_folder in folders:
        #print(bug_folder)

        #skip if .DS_Store
        if bug_folder == ".DS_Store":
            continue

        #extract bug id from folder name
        #print(bug_folder.split("TR"))
        bug_id = int(bug_folder.split("TR")[1])

        #skip if not in bug_ids
        if bug_id not in bug_ids:
            continue

        for app_folder in pathlib.Path(os.path.join(tr_data_folder, bug_folder)).glob('*'):

            for json_file_path in pathlib.Path(os.path.join(tr_data_folder, bug_folder, app_folder)).glob('*.json'):
                
                if not os.path.isfile(json_file_path):
                    continue

                #print(json_file_path)

                #read json file
                with open(json_file_path, 'r') as json_file:
                    json_data = json.load(json_file)

                    #get app name
                    app_name = json_data["app"]['name']

                    #get app version
                    app_version = json_data["app"]['version']

                    package = json_data["app"]['packageName']
                    
                    print(app_name, package)
                    #print(f'new Triplet<>("{bug_id}", "{app_name}", "{app_version}"),')
                    

                break

            break
import os
import pathlib
import shutil


if __name__ == "__main__":
    tr_data_folder = "/Users/ojcchar/repositories/Projects/burt/data/TraceReplayer-Data-new"

    #list folders in tr_data_folder
    folders = os.listdir(tr_data_folder)
    
    for bug_folder in folders:

        print(bug_folder)

        app_folder = None

        #read first xml file in folder
        for xml_file_path in pathlib.Path(os.path.join(tr_data_folder, bug_folder)).glob('*.xml'):
            
            if not os.path.isfile(xml_file_path):
                continue

            print(xml_file_path)

            #get file name with no extension
            file_name = os.path.splitext(os.path.basename(xml_file_path))[0]

            #get prefix of file name before "-User-Trace"
            app_folder = file_name.split("-1-User-Trace")[0]

            app_folder = os.path.join(tr_data_folder, bug_folder, app_folder)
            print(app_folder)

            #mkdir folder based on prefix, if not exists
            if not os.path.exists(app_folder):
                os.mkdir(app_folder)
            
            break

        if app_folder == "" or app_folder == None:
            continue
        
        #move all files in bug_folder to app_folder, except app_folder
        for file_path in pathlib.Path(os.path.join(tr_data_folder, bug_folder)).glob('*'):

            if file_path == app_folder:
                continue

            print(file_path)

            #move file to app_folder
            shutil.move(file_path, app_folder)
            
import os
from os import listdir
import json
import logging
from pathlib import Path
import csv


def write_json_line_by_line(data, file_path):
    with open(file_path, 'w') as dest_file:
        for record in data:
            print(json.dumps(record), file=dest_file)


def parse_dXml(idXml):
    if "NO_ID" in idXml or idXml == "" or "/" not in idXml:
        return None

    else:
        return idXml.split("/")[1]


def parse_type(type):
    if type:
        key_word = type.split(".")[-1]
        print(key_word)
        view_group = ["viewgroup", "layout", "viewpager", "tabhost", "tabwidget", "tablerow", "viewpager"]
        if key_word.lower() == "view" or any(s in key_word.lower() for s in view_group):
            return False
        else:
            return True
    else:
        return False


def read_json(file, data_source):
    result = []

    with open(file) as json_file:
        execution_dict = json.load(json_file)
        steps = execution_dict["steps"]

        for i in range(len(steps)):
            step_contents = {}

            step = steps[i]
            action_code = step["action"]
            if action_code == 99:
                continue

            step_contents["stepID"] = step["sequenceStep"]
            step_contents["dataSource"] = data_source

            if "screenshot" not in step:
                print("the", step["sequenceStep"], "step does not have screenshot")
            else:
                screenshot_path = step["screenshot"]

            step_contents["screenshotPath"] = screenshot_path
            step_contents["action"] = {}

            if "dynGuiComponent" in step:

                dyn_gui_component = step["dynGuiComponent"]

                if "name" in dyn_gui_component:
                    type = dyn_gui_component["name"]
                    if parse_type(type):
                        step_contents["action"]["type"] = type.split(".")[-1]

                        if "text" in dyn_gui_component:
                            if dyn_gui_component["text"]:

                                step_contents["action"]["text"] = dyn_gui_component["text"]

                        if "contentDescription" in dyn_gui_component:
                            if dyn_gui_component["contentDescription"]:
                                step_contents["action"]["content_description"] = dyn_gui_component["contentDescription"]

                        if "idXml" in dyn_gui_component:
                            idXml = dyn_gui_component["idXml"]
                            xml_result = parse_dXml(idXml)
                            if xml_result:
                                step_contents["action"]["idXml"] = xml_result

                            # ----------------------------
            screen = step["screen"]
            components_list = []
            for gui_component in screen["dynGuiComponents"]:
                content_screen = {}

                if "name" in gui_component:
                    type = gui_component["name"]
                    if parse_type(type):
                        content_screen["type"] = type.split(".")[-1]

                        if "text" in gui_component:
                            if gui_component["text"]:
                                content_screen["text"] = gui_component["text"]

                        if "contentDescription" in gui_component:
                            if gui_component["contentDescription"]:
                                content_screen["content_description"] = gui_component["contentDescription"]

                        if "idXml" in gui_component:
                            each_idXml = gui_component["idXml"]
                            xml_result = parse_dXml(each_idXml)
                            if xml_result is not None:
                                content_screen["idXml"] = xml_result

                if len(content_screen) > 0:
                    components_list.append(content_screen)

            step_contents["screen"] = components_list

            result.append(step_contents)

        return result


def write_csv_from_json_list(result, exec_output_file_path):
    with open(exec_output_file_path, "w" ,newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["dataSource", "stepID", "screenshotPath", "text", "idXml", "type"])

        for each_json in result:
            data_source = each_json["dataSource"]
            stepID = each_json["stepID"]
            screenshotPath = each_json["screenshotPath"]
            action = each_json["action"]
            print(data_source)
            print(screenshotPath)
            print(stepID)
            if not action:
                if "text" in action:
                    text = action["text"]
                else:
                    text = ""

                if "idXml" in action:
                    idXml = action["idXml"]
                else:
                    idXml = ""

                if "type" in action:
                    type = action["type"]
                else:
                    type = ""

                writer.writerow([data_source, stepID, screenshotPath, text, idXml, type])
            components = each_json["screen"]
            for component in components:
                if "text" in component:
                    text_cp = component["text"]
                else:
                    text_cp = ""

                if "idXml" in component:
                    idXml_cp = component["idXml"]
                else:
                    idXml_cp = ""

                if "type" in component:
                    type_cp = component["type"]
                else:
                    type_cp = ""
                writer.writerow([data_source, stepID, screenshotPath, text_cp, idXml_cp, type_cp])


if __name__ == '__main__':

    app_version_packages = {
        "ATimeTracker": "com.markuspage.android.atimetracker",
        "GnuCash": "org.gnucash.android",
        "mileage": "com.evancharlton.mileage",
        "droidweight": "de.delusions.measure",
        "AntennaPod": "de.danoeh.antennapod.debug",
        "growtracker": "me.anon.grow",
        "androidtoken": "uk.co.bitethebullet.android.token"
    }

    systems = [
        ("GnuCash", "2.1.3"),
        ("mileage", "3.1.1"),
        ("droidweight", "1.5.4"),
        ("GnuCash", "1.0.3"),
        ("AntennaPod", "1.6.2.3"),
        ("ATimeTracker", "0.20"),
        ("growtracker", "2.3.1"),
        ("androidtoken", "2.10"),

    ]

    data_sources = ["CrashScope", "TraceReplayer"]
    json_list = []
    output_folder = "extracted_data"
    csv_output_file_path = "extracted_information.csv"

    for system in systems:
        system_name, system_version = system

        # read execution file
        data_folder = "../data"

        for data_source in data_sources:

            data_source_folder = data_source + "-Data"
            package_name = app_version_packages[system_name]

            data_location = os.path.join(data_folder, data_source_folder, package_name + "-" + system_version)

            onlyExecutionfiles = [f for f in listdir(data_location) if "Execution-" in f]

            for file in onlyExecutionfiles:

                try:
                    execution_file_path = os.path.join(data_location, file)
                    result = read_json(execution_file_path, data_source + "-" + file)

                    json_list.extend(result)

                    Path(os.path.join(output_folder, package_name + "-" + system_version)).mkdir(parents=True,
                                                                                                 exist_ok=True)
                    exec_output_file_path = os.path.join(output_folder, package_name + "-" + system_version,
                                                         data_source + "-" + file)

                    write_json_line_by_line(result, exec_output_file_path)

                except UnicodeError as e:
                    logging.error('Failed to read execution file: ' + str(e))

    write_csv_from_json_list(json_list, os.path.join(output_folder, csv_output_file_path))

import os
from os import listdir
import json
import logging
from pathlib import Path


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
        return type.split(".")[-1]
    else:
        return None


def read_json(file):

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
            step_contents["step_id"] = step["sequenceStep"]

            concepts = {}
            if "screenshot" not in step:
                print("the", step["sequenceStep"], "step does not have screenshot")
            else:
                screenshot_path = step["screenshot"]

            step_contents["screenshotPath"] = screenshot_path
            step_contents["action"] = {}

            if "dynGuiComponent" not in step:
                print("the", step["sequenceStep"], "step does not have dynGuiComponent")
            else:
                dyn_gui_component = step["dynGuiComponent"]

            if "text" in dyn_gui_component:
                text = dyn_gui_component["text"]
                step_contents["action"]["text"] = text

            if "contentDescription" in dyn_gui_component:
                content_description = dyn_gui_component["contentDescription"]
                step_contents["action"]["content_description"] = content_description

            if "idXml" not in dyn_gui_component:
                print("the", step["sequenceStep"], "step does not have idXml")
            else:
                idXml = dyn_gui_component["idXml"]
                key_word = parse_dXml(idXml)
                if key_word:
                    step_contents["action"]["idXml"] = key_word

            if "name" not in dyn_gui_component:
                print("the", step["sequenceStep"], "step does not have name")
            else:
                type = dyn_gui_component["name"]
                type_key_word = parse_type(type)
                if type_key_word:
                    step_contents["action"]["type"] = type_key_word

            # ----------------------------
            screen = step["screen"]
            components_list = []
            for gui_component in screen["dynGuiComponents"]:
                each_type = gui_component["name"]
                view_group = ["viewgroup", "layout", "viewpager", "tabhost", "tabwidget", "tablerow"]
                if any(s in each_type.lower() for s in view_group) or each_type.lower() == "view":
                    continue

                content_screen = {}

                if "text" in gui_component:
                    each_text = gui_component["text"]
                    content_screen["text"] = each_text

                if "contentDescription" in gui_component:
                    each_content_description = gui_component["contentDescription"]
                    content_screen["content_description"] = each_content_description

                each_idXml = gui_component["idXml"]
                each_key_word = parse_dXml(each_idXml)
                if each_key_word:
                    content_screen["idXml"] = each_key_word

                each_type = gui_component["name"]
                each_type_key_word = parse_type(each_type)
                if each_type_key_word:
                    content_screen["type"] = each_type_key_word
                if len(content_screen) > 0:
                    components_list.append(content_screen)

            step_contents["screen"] = components_list

            result.append(step_contents)

        return result


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

    for system in systems:
        system_name, system_version = system

        # read execution file
        data_folder = "../data"
        output_folder = "extracted_data"
        for data_source in data_sources:

            data_source_folder = data_source + "-Data"
            package_name = app_version_packages[system_name]

            data_location = os.path.join(data_folder, data_source_folder, package_name + "-" + system_version)

            onlyExecutionfiles = [f for f in listdir(data_location) if "Execution-" in f]

            for file in onlyExecutionfiles:

                try:
                    execution_file_path = os.path.join(data_location, file)
                    result = read_json(execution_file_path)
                    Path(os.path.join(output_folder, package_name + "-" + system_version)).mkdir(parents=True, exist_ok=True)
                    exec_output_file_path = os.path.join(output_folder, package_name + "-" + system_version, data_source + "-" + file)
                    write_json_line_by_line(result, exec_output_file_path)

                except UnicodeError as e:
                    logging.error('Failed to read execution file: ' + str(e))

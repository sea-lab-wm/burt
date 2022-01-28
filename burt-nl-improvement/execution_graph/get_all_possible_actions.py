import json
import multiprocessing
import os
from os import listdir





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
    tokens_csv_output_file_path = "extracted_tokens.csv"


    num_workers = multiprocessing.cpu_count()
    actionset = set()
    typeset = set()

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


                execution_file_path = os.path.join(data_location, file)

                with open(execution_file_path,encoding="utf8") as json_file:
                    execution_dict = json.load(json_file)
                    steps = execution_dict["steps"]
                    for step in steps:
                        actionset.add(step["action"])


                        screen = step["screen"]
                        components = screen["dynGuiComponents"]
                        for comp in components:
                            typeset.add(comp["name"])
                            if "switch" in comp["name"].lower():

                                print(comp["name"], step.get("screenshot", None))
    print(actionset)
    print(typeset)




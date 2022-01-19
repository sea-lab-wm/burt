import concurrent
import math
import re
import traceback

from os import listdir
import json
import logging
from pathlib import Path
import csv
import spacy
from spacy.symbols import *
import concurrent.futures

import multiprocessing
import os

from main import *

from execution_graph.main import read_json
from execution_graph.main import parse_type
from execution_graph.utils import write_json


def camel_case_split(str):
    return re.split('(?=[A-Z])', str)


def parse_idXml(idXml):
    if "NO_ID" in idXml or idXml == "" or "/" not in idXml:
        return None
    last_word = idXml.split("/")[-1]
    last_word = ''.join([i for i in last_word if not i.isdigit()])

    result = []
    if "_" in last_word:
        new_idXml_list = last_word.replace("_", " ").split(" ")
    elif last_word != (last_word.lower()):
        new_idXml_list = camel_case_split(last_word)
        # remove words such as edit, editing, input
    else:
        return last_word.lower()

    remove_words = ["edit", "input", "spinner", "checkbox", "entry", "button"]
    for word in new_idXml_list:
        if any(s in word.lower() for s in remove_words):
            continue
        result.append(word.lower())
    new_idXml = " ".join(result).lower()
    return new_idXml


def parse_window(curr_win):
    if curr_win == "":
        return None
    last_word = curr_win.split(".")[1]
    if "_" in last_word:
        new_idXml_list = last_word.replace("_", " ").split(" ")
    else:
        new_idXml_list = camel_case_split(last_word)
        # remove words such as edit, editing, input

    new_win = " ".join(new_idXml_list).lower()

    return new_win


def get_phrases_click(doc):
    phrases = set()
    # FIXME: do we need to add more verb except click?
    root = [token for token in doc if token.head == token][0]
    if root.pos == VERB:
        verb = root.text

        prts = [w for w in root.rights if w.dep_ == "prt"]

        # -- search the synonyms of verb + prt, or just verb

        for prt in prts:
            verb = root.text + " " + prt.text

        # -- search the synonyms of noun phrases here

        dobj = [w for w in root.rights if w.dep_ == "dobj"]

        for obj in dobj:
            phrases.add("I " + verb + " " + obj.text)
            phrases.add("I click " + verb + " " + obj.text + " button")

        # FIXME 可以检查下形容词

        # find subject
        # process the cases like "Navigation drawer opened"
        nsubjs = [w for w in root.lefts if w.dep_ == "nsubj"]
        for sub in nsubjs:
            if sub.pos != PRON:
                phrases.add("I " + verb + " " + sub.text)
                phrases.add("I click " + verb + " " + sub.text + " button")

        # FIXME: the text on the button is just a verb or verb phrase
        if len(dobj) == 0 and len(nsubjs) == 0:
            phrases.add("I click " + verb + " button")
            phrases.add("I select " + root.text)

    elif root.pos in [NOUN, PROPN]:
        # FIXME 可以检查下形容词

        if any(s in root.text for s in ["date, time"]):  # process a special case
            phrases.add("I edit " + root.text)
            phrases.add("I change " + root.text)

        else:
            phrases.add("I click " + root.text + " button")
            phrases.add("I click " + root.text)  # for textview components

    elif root.text in ["ok", "next"]:  # process a special case
        phrases.add("I click " + root.text + " button")



    elif root.pos == ADJ:
        if root.text in ["more", "complete", "subscribe"]:
            phrases.add("I click " + root.text)
            phrases.add("I click " + root.text + " button")

    elif root.pos == ADJ:
        if root.text in ["sort"]:
            phrases.add("I click " + root.text)
            phrases.add("I click " + root.text + " button")

    return phrases


def get_phrases_edit(doc, title_window):
    phrases = set()

    # process some special cases
    if "url" in doc.text or "http" in doc.text:
        phrases.add("I input url")
        phrases.add("I edit url")
        return phrases

    if "search" in doc.text:
        phrases.add("I input to search")  # FIXME: How to get context?
        phrases.add("I write to search")
        return phrases

    # elif "pin" in doc.text:
    #     new_text = doc.text.replace("pin change", "")

    root = [token for token in doc if token.head == token][0]
    if root.pos == NOUN:
        new_string = root.text
        if title_window:
            new_string = new_string + " in " + title_window.lower()
        phrases.add("I input " + new_string)  # FIXME: do we need to add the inpux box information
        phrases.add("I edit " + new_string)
        phrases.add("I write " + new_string)
        phrases.add("I enter " + new_string)
    elif root.pos == VERB:
        verb = root.text
        if verb == "notes":  # process a special case
            phrases.add("I write " + verb)
            phrases.add("I enter " + verb)
            phrases.add("I edit " + verb)

        prts = [w for w in root.rights if w.dep_ == "prt"]

        # -- search the synonyms of verb + prt, or just verb
        for prt in prts:
            verb = verb + " " + prt.text

        dobj = [w for w in root.rights if w.dep_ == "dobj"]

        for obj in dobj:
            new_obj = obj.text
            if title_window:
                new_obj = new_obj + " in " + title_window.lower()
            phrases.add("I input" + " " + new_obj)
            phrases.add("I edit" + " " + new_obj)
            phrases.add("I enter" + " " + new_obj)
            phrases.add("I write " + new_obj)

        # process a special case
        nsubjs = [w for w in root.lefts if w.dep_ == "nsubj"]
        if len(dobj) == 0:
            for word in root.rights:
                if word.text == "new" and "pin" in [word.text for word in nsubjs]:
                    phrases.add("I input" + " " + word.text + " pin")

                    phrases.add("I edit" + " " + word.text + " pin")
                    phrases.add("I enter" + " " + word.text + " pin")
    return phrases


def get_phrases_button(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if text:
        # the basic phrase
        # phrases.add("I clicked the " + text.lower() + " " + "button")

        # extract verb and noun phrases
        doc = nlp(text.lower())
        phrases.update(list(get_phrases_click(doc)))
        if len(phrases) == 0:
            if idXml:
                extracted_idXml = parse_idXml(idXml)
                doc = nlp(extracted_idXml)
                phrases.update(list(get_phrases_click(doc)))

    return phrases


def get_phrases_imagebutton(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if content:
        phrases.add("I click the " + content.lower() + " button")

        content = "I " + content
        # parse the context
        doc = nlp(content.lower())

        phrases.update(list(get_phrases_click(doc)))

    elif idXml:
        extracted_idxml = parse_idXml(idXml)
        if extracted_idxml:
            new_idxml = "I " + extracted_idxml
            doc = nlp(new_idxml.lower())
            phrases.update(list(get_phrases_click(doc)))
    else:
        phrases.add("I click menu button")  # FIXME: a special case, image button does not have valid information
        print("There is no valid information !")
    return phrases


def get_phrases_textview(type, text, content, idXml, dyn_gui_component):
    # FIXME: it might need to write a sperate method for textview
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()

    # need to check idXml
    if idXml:
        parsed_idXml = parse_idXml(idXml)
        if parsed_idXml:
            words = ["label", "unit", "usage", "credits", "version", "text1", "permission message", "empty",
                     "txtvPodcastDirectoriesDescr", "txtvPubDate", "txtvDescription", "txtvAuthor", "txtvItemname",
                     "txtvLenSize", "txtvPublished", "statusUnread", "txtvCount"]

            if not any(x in parsed_idXml for x in words):  # this is a special case
                if text:

                    doc = nlp(text.lower())
                    if len(doc) < 10:
                        phrases.update(list(get_phrases_click(doc)))

                elif content:
                    doc = nlp(content.lower())
                    phrases.update(list(get_phrases_click(doc)))

                if len(phrases) == 0:
                    if parsed_idXml != "title":
                        doc = nlp(parsed_idXml.lower())
                        phrases.update(list(get_phrases_click(doc)))

    return phrases


def get_phrases_checkedtextview(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if text:
        phrases.add("I click " + text.lower())
        phrases.add("I choose " + text.lower())
    return phrases


def get_phrases_imageview(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    clickable = dyn_gui_component["clickable"]
    if clickable:

        if content:  # check contentDescription first
            doc = nlp(content.lower())
            phrases.update(list(get_phrases_click(doc)))
        elif idXml:
            extracted_idxml = parse_idXml(idXml)
            if extracted_idxml:
                doc = nlp(extracted_idxml.lower())
                phrases.update(list(get_phrases_click(doc)))
    return phrases


def get_phrases_long_click(doc):
    phrases = set()
    root = [token for token in doc if token.head == token][0]
    if root.pos == VERB:
        verb = root.text
        prts = [w for w in root.rights if w.dep_ == "prt"]

        # -- search the synonyms of verb + prt, or just verb
        for prt in prts:
            verb = verb + " " + prt.text

        dobj = [w for w in root.rights if w.dep_ == "dobj"]
        for obj in dobj:
            phrases.add("I long click " + verb + " " + obj.text)
        if len(dobj) == 0:
            phrases.add("I long click " + verb)
    elif root.pos in [NOUN, PROPN]:
        if "list" in root.text:  # FIXME: process a special case
            phrases.add("I long click one created item in the list")  # do not know which kind of item

        if "bmi" in root.text:  # FIXME: process a special case
            phrases.add("I long click one created weight record in the list")

        if "task" in root.text:  # FIXME: process a special case
            phrases.add("I long click created " + root.text + " in the list")

        else:
            phrases.add("I long click " + root.text)

    return phrases


def get_phrases_edittext(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    title_window = dyn_gui_component.get("titleWindow", None)
    if idXml:
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            dyn_gui_component.get("contentDescription", None)
            doc = nlp(new_idXml)
            phrases.update(get_phrases_edit(doc, title_window))

        else:
            if text:
                doc = nlp(text.lower())
                phrases.update(get_phrases_edit(doc, title_window))
    return phrases


def get_phrases_switch(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    # FIXME: how to connect this switch with connected component?
    if text.lower() == "on":
        phrases.add("I turn the switch on")
    if text.lower() == "off":
        phrases.add("I turn the switch off")
    return phrases


def get_phrases_spinner(type, text, content, idXml, dyn_gui_component):
    # nlp = spacy.load("en_core_web_sm")
    # nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    # check idxml
    if idXml:  # open spinner // select/choose xxx
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            phrases.add("I select " + new_idXml)
            phrases.add("I select one value from the " + new_idXml + " menu")

    return phrases


def get_phrases_datepicker(type, text, content, idXml, dyn_gui_component):
    phrases = set()
    # check idxml
    if idXml:  # FIXME: need more phrases like open spinner, select/choose xxx
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            phrases.add("I select " + new_idXml)
            phrases.add("I edit " + new_idXml)

    return phrases


def get_phrases_timepicker(type, text, content, idXml, dyn_gui_component):
    phrases = set()
    # check idxml
    if idXml:  # FIXME: need more phrases like open spinner, select/choose xxx
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            phrases.add("I select " + new_idXml)
            phrases.add("I edit" + new_idXml)

    return phrases


def get_phrases_radiobutton(type, text, content, idXml, dyn_gui_component):
    phrases = set()
    # use text
    if text:
        phrases.add("I select " + text.lower() + " option")
        phrases.add("I select " + text.lower() + " button")

    # currently it does not need to check idXml
    return phrases


def get_phrases_togglebutton(type, text, content, idXml, dyn_gui_component):
    phrases = set()
    # use text and idxml together
    if text and idXml:
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            phrases.add("I select " + text.lower() + "as the " + new_idXml)
    return phrases


def get_phrases_checkbox(type, text, content, idXml, dyn_gui_component):
    phrases = set()

    # if there is valid text, use text first
    if text:
        phrases.add("I select " + text.lower())
        phrases.add("I choose " + text.lower())
    elif idXml:
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            phrases.add("I select " + new_idXml)
            phrases.add("I choose " + new_idXml)
    return phrases


def get_phrases_listview(type, text, content, idXml, dyn_gui_component):
    phrases = set()

    # if there is valid text, use text first
    phrases.add("I select one record from the list")
    return phrases


def process_click_action(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if parse_type(type):
        type_name = type.split(".")[-1]

        # ---------------------------
        if type_name.lower() == "button":
            # use text
            phrases.update(get_phrases_button(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() == "imagebutton":
            phrases.update(get_phrases_imagebutton(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        # FIXME: textview type components have both clickable and unclickable types, how to differentiate these?
        if type_name.lower() == "textview":
            phrases.update(get_phrases_textview(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() in ["checkedtextview"]:
            phrases.update(get_phrases_textview(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        # need to check "clickable",
        if type_name.lower() == "imageview":
            phrases.update(get_phrases_imageview(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        # FIXME: how to connect checkbox with related component that provides more information
        if type_name.lower() == "checkbox":
            phrases.update(get_phrases_checkbox(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() == "radiobutton":
            phrases.update(get_phrases_radiobutton(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() == "togglebutton":
            phrases.update(get_phrases_togglebutton(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() == "spinner":
            phrases.update(get_phrases_spinner(type, text, content, idXml, dyn_gui_component))

        # ----------------------------------
        if type_name.lower() == "listview":
            phrases.update(get_phrases_listview(type, text, content, idXml, dyn_gui_component))

    return phrases


def process_click_type_action(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if parse_type(type):
        type_name = type.split(".")[-1]

        # ---------------------------
        if type_name.lower() == "edittext":
            # FIXME: how to connect this EditText component with nearby textView information?
            # we do not use the "text" here
            phrases.update(get_phrases_edittext(type, text, content, idXml, dyn_gui_component))
    return phrases


def process_long_click_action(type, text, content, idXml, curr_win):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()

    # ignore text information here
    # first check idXml, if Xml did not give valid information, check "currentWindow"

    if idXml:
        new_idXml = parse_idXml(idXml)
        if new_idXml:
            doc = nlp(new_idXml)
            phrases.update(get_phrases_long_click(doc))
    elif curr_win:
        new_win = parse_window(curr_win)
        if new_win:
            doc = nlp(new_win)
            phrases.update(get_phrases_long_click(doc))
    return phrases


def augment_graph(file_path, data_location, file):
    with open(file_path, encoding="utf-8") as json_file:
        execution_dict = json.load(json_file)
        steps = execution_dict["steps"]

        for i in range(len(steps)):

            step = steps[i]
            action_code = step["action"]
            if action_code == 99:
                continue

            screen = step["screen"]
            dynGuiComponents = screen["dynGuiComponents"]

            if "dynGuiComponent" in step:
                dyn_gui_component = step["dynGuiComponent"]

                type = dyn_gui_component.get("name", None)

                text = dyn_gui_component.get("text", None)

                content = dyn_gui_component.get("contentDescription", None)

                idXml = dyn_gui_component.get("idXml", None)

                curr_win = dyn_gui_component.get("currentWindow", None)

                if action_code == 0:  # click
                    phrases = process_click_action(type, text, content, idXml, dyn_gui_component)
                    dyn_gui_component["phrases"] = list(phrases)

                if action_code == 3:  # click and type
                    phrases = process_click_type_action(type, text, content, idXml, dyn_gui_component)
                    dyn_gui_component["phrases"] = list(phrases)

                if action_code == 1:  # long click
                    phrases = process_long_click_action(type, text, content, idXml, curr_win)
                    dyn_gui_component["phrases"] = list(phrases)

                # if action_code == 11: # FIXME: MENU_BTN

                if action_code == 20:  # SWIPE_UP
                    dyn_gui_component["phrases"] = ["I swiped up in the screen"]

                if action_code == 21:  # SWIPE_RIGHT
                    dyn_gui_component["phrases"] = ["I swiped right in the screen"]

                if action_code == 22:  # SWIPE_DOWN
                    dyn_gui_component["phrases"] = ["I swiped down in the screen"]

                if action_code == 23:  # SWIPE_LEFT
                    dyn_gui_component["phrases"] = ["I swiped left in the screen"]

            # add phrases for each component of screen
            nlp = spacy.load("en_core_web_sm")
            nlp.add_pipe("merge_noun_chunks")
            for comp in dynGuiComponents:
                phrases = set()

                type_comp = comp.get("name", None)

                text = comp.get("text", None)

                content = comp.get("contentDescription", None)

                idXml = comp.get("idXml", None)

                if type_comp:
                    if parse_type(type_comp):
                        type_name = type_comp.split(".")[-1]

                        # ---------------------------
                        if type_name.lower() == "button":
                            phrases.update(get_phrases_button(type, text, content, idXml, comp))

                        elif type_name.lower() == "imagebutton":
                            phrases.update(get_phrases_imagebutton(type, text, content, idXml, comp))

                        elif type_name.lower() == "textview":
                            phrases.update(get_phrases_textview(type, text, content, idXml, comp))

                        elif type_name.lower() == "checkedtextview":
                            phrases.update(get_phrases_checkedtextview(type, text, content, idXml, comp))

                        elif type_name.lower() == "imageview":
                            phrases.update(get_phrases_imageview(type, text, content, idXml, comp))

                        elif type_name.lower() == "edittext":
                            phrases.update(get_phrases_edittext(type, text, content, idXml, comp))

                        elif type_name.lower() == "checkbox":
                            phrases.update(get_phrases_checkbox(type, text, content, idXml, comp))

                        elif type_name.lower() == "switch":
                            phrases.update(get_phrases_switch(type, text, content, idXml, comp))

                        elif type_name.lower() == "spinner":
                            phrases.update(get_phrases_spinner(type, text, content, idXml, comp))

                        elif type_name.lower() == "datepicker":
                            phrases.update(get_phrases_datepicker(type, text, content, idXml, comp))

                        elif type_name.lower() == "timepicker":
                            phrases.update(get_phrases_timepicker(type, text, content, idXml, comp))

                        elif type_name.lower() == "radiobutton":
                            phrases.update(get_phrases_radiobutton(type, text, content, idXml, comp))

                        elif type_name.lower() == "togglebutton":
                            phrases.update(get_phrases_togglebutton(type, text, content, idXml, comp))

                        elif type_name.lower() == "listview":
                            phrases.update(get_phrases_listview(type, text, content, idXml, comp))

                comp["phrases"] = list(phrases)
        # write augmented execution graph
        print(os.path.join(data_location, "Augmented-" + file))
        with open(os.path.join(data_location, "Augmented-" + file), 'w') as f:
            json.dump(execution_dict, f, indent=4)


if __name__ == '__main__':

    app_version_packages = {
        "ATimeTracker": "com.markuspage.android.atimetracker",  # checked ?
        "GnuCash": "org.gnucash.android",
        "mileage": "com.evancharlton.mileage",
        "droidweight": "de.delusions.measure",  # checked
        "AntennaPod": "de.danoeh.antennapod.debug",
        "growtracker": "me.anon.grow",
        "androidtoken": "uk.co.bitethebullet.android.token"  # checked
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

    num_workers = multiprocessing.cpu_count()
    with concurrent.futures.ProcessPoolExecutor(max_workers=num_workers) as executor:
        futures = []
        try:

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

                        futures.append(
                            executor.submit(augment_graph, execution_file_path, data_location, file))

            for future in concurrent.futures.as_completed(futures):
                        future.result()


        except Exception as e:
            print(e)
            traceback.print_exc()

import os
import multiprocessing
import re
import traceback
import concurrent.futures
import spacy
from spacy.symbols import *
import xml.etree.ElementTree as ET


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


def parse_type(type):
    if type:
        key_word = type.split(".")[-1]
        # print(key_word)
        view_group = ["viewgroup", "layout", "viewpager", "tabhost", "tabwidget", "tablerow", "viewpager"]
        if key_word.lower() == "view" or any(s in key_word.lower() for s in view_group):
            return False
        else:
            return True
    else:
        return False


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

        # FIXME: we may need to check adj

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
        # FIXME: we may need to check adj

        if any(s in root.text for s in ["date, time"]):  # process a special case
            phrases.add("I edit " + root.text)
            phrases.add("I change " + root.text)

        else:
            phrases.add("I click " + root.text + " button")
            phrases.add("I click " + root.text)  # for textview components

    elif root.text in ["ok", "next", "back"]:  # process a special case
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


def get_phrases_edit(doc):
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
        npadvmods = [w for w in root.rights if w.dep_ == "npadvmod"]
        for npadvmod in npadvmods:
            if npadvmod.text == "ph":
                phrases.add("I input " + new_string + " " + npadvmod.text)
                phrases.add("I edit " + new_string + " " + npadvmod.text)
                phrases.add("I write " + new_string + " " + npadvmod.text)
                phrases.add("I enter " + new_string + " " + npadvmod.text)
                return phrases

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

        xcomps = [w for w in root.rights if w.dep_ == "xcomp"]
        for xcomp in xcomps:
            phrases.add("I " + root.text + " " + xcomp.text)
            phrases.add("I edit" + " " + root.text + " " + xcomp.text)

    return phrases


def get_phrases_click_textview(doc):
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
            phrases.add("I click " + verb + " " + obj.text)

        # FIXME: we may need to check adj

        # find subject
        # process the cases like "Navigation drawer opened"
        nsubjs = [w for w in root.lefts if w.dep_ == "nsubj"]
        for sub in nsubjs:
            if sub.pos != PRON:
                phrases.add("I " + verb + " " + sub.text)
                phrases.add("I click " + verb + " " + sub.text)

        # FIXME: the text on the button is just a verb or verb phrase
        if len(dobj) == 0 and len(nsubjs) == 0:
            phrases.add("I click " + verb)
            phrases.add("I select " + root.text)

    elif root.pos in [NOUN, PROPN]:
        # FIXME: we may need to check adj

        if any(s in root.text for s in ["date, time"]):  # process a special case
            phrases.add("I edit " + root.text)
            phrases.add("I change " + root.text)

        else:
            phrases.add("I click " + root.text)
    elif root.text in ["ok", "next", "back"]:  # process a special case
        phrases.add("I click " + root.text)

    elif root.pos == ADJ:
        if root.text in ["more", "complete", "subscribe"]:
            phrases.add("I click " + root.text)

    elif root.pos == ADJ:
        if root.text in ["sort"]:
            phrases.add("I click " + root.text)

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
        phrases.add("I click " + content.lower() + " button")

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
            words = ["label", "unit", "usage", "credits", "version", "text1", "text", "permission message", "empty",
                     "txtv podcast directories descr", "txtv pubDate", "txtv description", "txtv author",
                     "txtv itemname",
                     "txtv len size", "txtv published", "status unread", "txtv count", "summary", "txtv url",
                     "txtv opml import expl"]

            if not any(x in parsed_idXml for x in words):
                if parsed_idXml in ["date", "time"]:  # process date-related components first

                    phrases.add("I edit " + parsed_idXml)
                    phrases.add("I change " + parsed_idXml)
                    phrases.add("I input " + parsed_idXml)
                    return phrases
                special = ["'", ":", "&amp;", "-"]

                if text and any(x in text for x in special):
                    return phrases

                if text:
                    doc = nlp(text.lower())
                    if len(doc) < 3 and parsed_idXml in ["txtv title", "title"]:
                        phrases.add("I click " + text.lower())
                        phrases.add("I tap " + text.lower())

                elif content:
                    doc = nlp(content.lower())
                    phrases.update(list(get_phrases_click_textview(doc)))

                if len(phrases) == 0:
                    if parsed_idXml not in ["txtv title", "title"]:
                        doc = nlp(parsed_idXml.lower())
                        phrases.update(list(get_phrases_click_textview(doc)))

    return phrases


def get_phrases_checkedtextview(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    if text:
        special = ["'", ":", "&amp;", "-"]

        if text and any(x in text for x in special):
            return phrases

        phrases.add("I choose " + text.lower())
        phrases.add("I select " + text.lower())
        phrases.add("I check " + text.lower())
    return phrases


def get_phrases_imageview(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    clickable = dyn_gui_component.data.attrib["clickable"]
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

    # check the parent component and get the text field description

    parent_node = dyn_gui_component.parent
    attr_dict = parent_node.data.attrib
    text_parent_node = attr_dict.get("text", None)
    if text_parent_node:
        phrases.add("I input " + text_parent_node.lower())
        phrases.add("I edit " + text_parent_node.lower())
        phrases.add("I write " + text_parent_node.lower())
        phrases.add("I enter " + text_parent_node.lower())

    # if we cannot get valid information from parent node, we parse idXml
    elif idXml:

        new_idXml = parse_idXml(idXml)

        if text and new_idXml == "description":
            doc = nlp(text.lower())
            phrases.update(get_phrases_edit(doc))

        elif new_idXml:
            doc = nlp(new_idXml)
            phrases.update(get_phrases_edit(doc))

        else:
            if text:
                doc = nlp(text.lower())
                phrases.update(get_phrases_edit(doc))
    return phrases


def get_phrases_switch(type, text, content, idXml, dyn_gui_component):
    nlp = spacy.load("en_core_web_sm")
    nlp.add_pipe("merge_noun_chunks")
    phrases = set()
    # FIXME: how to connect this switch with connected component?
    # the connected component is one of the parent's sibling
    if text.lower() == "on":
        parent_node = dyn_gui_component.parent
        parent_parent_node = parent_node.parent
        if len(parent_parent_node.children) == 2:
            for child in parent_parent_node.children:
                if child.data != dyn_gui_component.data:
                    for child_2 in child.children:
                        if "textview" in type.lower() and "title" in idXml.lower():
                            title = child_2.data.attrib.get("text", None)
                            if title:
                                phrases.add("I turn " + title + " switch on")
                            # if text.lower() == "on":
                            #     phrases.add("I turn " +  title + " switch on")
                            # if text.lower() == "off":
                            #     phrases.add("I turn " +  title + " switch off")


    elif text.lower() == "off":
        parent_node = dyn_gui_component.parent
        parent_parent_node = parent_node.parent
        if len(parent_parent_node.children) == 2:
            for child in parent_parent_node.children:
                if child.data != dyn_gui_component.data:
                    for child_2 in child.children:
                        if "textview" in type.lower() and "title" in idXml.lower():
                            title = child_2.data.attrib.get("text", None)
                            if title:
                                phrases.add("I turn " + title + " switch off")
    else:
        phrases.add("I turn " + text.lower() + " on")

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
        phrases.add("I choose " + text.lower() + " option")
        phrases.add("I select " + text.lower() + " option")
        phrases.add("I choose " + text.lower() + " button")

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

    # case 1: the checkbox itself has the text information
    # case 2: the sibling nodes has related text information. FIXME: use idxml is also ok
    # case 3: the sibling of parent node has related text information, in this case, we just get text from idXml
    # case 4: the children of the sibling of parent, for simplicity, we do not consider this.

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


def generate_phrases(node):
    phrases = set()

    tag = node.data.tag
    attributes = node.data.attrib

    type_comp = attributes.get("class", None)

    text = attributes.get("text", None)

    content = attributes.get("content-desc", None)

    idXml = attributes.get("resource-id", None)

    if type_comp:
        if parse_type(type_comp):
            type_name = type_comp.split(".")[-1]

            # ---------------------------
            if type_name.lower() == "button":
                phrases.update(get_phrases_button(type_name, text, content, idXml, node))

            elif type_name.lower() == "imagebutton":
                phrases.update(get_phrases_imagebutton(type_name, text, content, idXml, node))

            elif type_name.lower() == "textview":
                phrases.update(get_phrases_textview(type_name, text, content, idXml, node))

            elif type_name.lower() == "checkedtextview":
                phrases.update(get_phrases_checkedtextview(type_name, text, content, idXml, node))

            elif type_name.lower() == "imageview":
                phrases.update(get_phrases_imageview(type_name, text, content, idXml, node))

            elif type_name.lower() == "edittext":
                phrases.update(get_phrases_edittext(type_name, text, content, idXml, node))

            elif type_name.lower() == "checkbox":
                phrases.update(get_phrases_checkbox(type_name, text, content, idXml, node))

            elif type_name.lower() == "switch":
                phrases.update(get_phrases_switch(type_name, text, content, idXml, node))

            elif type_name.lower() == "spinner":
                phrases.update(get_phrases_spinner(type_name, text, content, idXml, node))

            elif type_name.lower() == "datepicker":
                phrases.update(get_phrases_datepicker(type_name, text, content, idXml, node))

            elif type_name.lower() == "timepicker":
                phrases.update(get_phrases_timepicker(type_name, text, content, idXml, node))

            elif type_name.lower() == "radiobutton":
                phrases.update(get_phrases_radiobutton(type_name, text, content, idXml, node))

            elif type_name.lower() == "togglebutton":
                phrases.update(get_phrases_togglebutton(type_name, text, content, idXml, node))

            elif type_name.lower() == "listview":
                phrases.update(get_phrases_listview(type_name, text, content, idXml, node))
    return phrases


class Tree(object):
    def __init__(self, data):
        self.data = data
        self.children = []
        self.parent = None

    def add_child(self, data):
        new_child = Tree(data)
        self.children.append(new_child)

        return new_child

    def add_parent(self, node):
        self.parent = node

    def is_root(self):
        return self.parent is None

    def is_leaf(self):
        return not self.children

    def __str__(self):
        if self.is_leaf():
            return str(self.data)
        return '{data} [{children}]'.format(data=self.data, children=', '.join(map(str, self.children)))


def parse_tree(node):
    if len(list(node.data)) == 0:
        return
    # add child to current node
    for child in node.data:
        new_child = node.add_child(child)  # return a nod
        new_child.add_parent(node)  # add parent to current child

        parse_tree(new_child)

    return node


def replaceMultiple(phrase):
    # Iterate over the strings to be replaced
    phrase = phrase.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace(" txtv", "")

    return phrase


def traverse(node):
    if node.is_leaf():
        return

    for child in node.children:
        phrases = generate_phrases(child)

        new_phrases = [replaceMultiple(x) for x in phrases]
        print(list(new_phrases))
        child.data.attrib["phrases"] = list(new_phrases)
        traverse(child)


def parse_xml(fullname, data_location, filename):
    xml_tree = ET.parse(fullname)
    root_data = xml_tree.getroot()
    root_node = Tree(root_data)

    tree_root = parse_tree(root_node)

    # traverse the tree and to add phrases to XML file
    traverse(tree_root)
    #
    # for node in root_data.iter("node"):
    #     try:
    #
    #         phrases = node.get("phrases")
    #         if phrases and len(phrases) > 0:
    #
    #             for phrase in phrases:
    #
    #                 phrase.replace("&", "&amp;")
    #                 phrase.replace("<", "&lt;")
    #                 phrase.replace(">", "&gt;")
    #
    #
    #     except AttributeError:
    #         pass

    directory = "Augmented-XML"
    if not os.path.exists(os.path.join(data_location, directory)):
        os.makedirs(os.path.join(data_location, directory))

    xml_tree.write(os.path.join(os.path.join(data_location, directory, filename)), xml_declaration=True,
                   encoding='utf-8',
                   method="xml")


if __name__ == '__main__':
    app_version_packages = {
        # "ATimeTracker": "com.markuspage.android.atimetracker",
        "GnuCash": "org.gnucash.android",
        # "mileage": "com.evancharlton.mileage",
        # "droidweight": "de.delusions.measure",
        # "AntennaPod": "de.danoeh.antennapod.debug",
        # "growtracker": "me.anon.grow",
        # "androidtoken": "uk.co.bitethebullet.android.token"
    }

    systems = [
        ("GnuCash", "2.1.3"),
        # ("mileage", "3.1.1"),
        # ("droidweight", "1.5.4"),
        # ("GnuCash", "1.0.3"),
        # ("AntennaPod", "1.6.2.3"),
        # ("ATimeTracker", "0.20"),
        # ("growtracker", "2.3.1"),
        # ("androidtoken", "2.10")
    ]

    data_sources = ["CrashScope", "TraceReplayer"]

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
                    for filename in os.listdir(data_location):
                        if not filename.endswith('.xml'):
                            continue
                        fullname = os.path.join(data_location, filename)
                        print(fullname)
                        futures.append(
                            executor.submit(parse_xml, fullname, data_location, filename))

            for future in concurrent.futures.as_completed(futures):
                future.result()

        except Exception as e:
            print(e)
            traceback.print_exc()

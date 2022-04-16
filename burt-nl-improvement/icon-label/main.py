from lxml import etree
import re
import os
import shutil
import json
from screen import Screen
from icon import Icon

data_path = os.path.dirname(os.path.abspath(__file__)).replace('burt-nl-improvement/icon-label', 'data/TraceReplayer-Data')
bounds_pattern = re.compile(r'\[-?(\d+),-?(\d+)\]\[-?(\d+),-?(\d+)\]')
json_data = []

# mark_tag = {'icon': '//*[@resource-id="android:id/icon"]',
# 'image': '//*[@class="android.widget.ImageView"]',
# 'button': '//*[@class="android.widget.ImageButton"]'}
mark_tag = {'icon': '//*[@resource-id="android:id/icon"]',
            'ImageButton': '//*[@class="android.widget.ImageButton"]',
            'ImageView': '//*[@class="android.widget.ImageView"]'}

dirs = ['com.evancharlton.mileage-3.1.1',
        'me.anon.grow-2.3.1',
        'com.markuspage.android.atimetracker-0.20',
        'org.gnucash.android-1.0.3',
        'de.danoeh.antennapod.debug-1.6.2.3',
        'org.gnucash.android-2.1.3',
        'de.delusions.measure-1.5.4',
        'uk.co.bitethebullet.android.token-2.10']
# dirs = ['org.gnucash.android-2.1.3', ]


def query_xml(xml_file, xpath):
    tree = etree.parse(open(xml_file, 'r'), etree.XMLParser(ns_clean=True))
    root = tree.getroot()
    res = root.xpath(xpath)
    rotation = int(root.get('rotation'))
    return res, rotation


def extract_bounds_from_node(node):
    match = bounds_pattern.match(node.attrib['bounds'])
    if match:
        return [int(match[1]), int(match[2]), int(match[3]), int(match[4])]
    return None


def launch():
    # clear and create out dir
    out_path = os.path.join(out_root, 's0')
    if os.path.exists(out_path):
        shutil.rmtree(out_path)
    os.makedirs(out_path)

    data = []
    for d in dirs:
        xml_data_path = os.path.join(data_path, d)
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        duplicated = set()
        for xml in xml_list:
            screen = Screen(xml, xml_data_path)
            print(screen)
            for tag, xpath in mark_tag.items():
                nodes, rotation = query_xml(xml, xpath)
                bounds = [extract_bounds_from_node(node) for node in nodes]
                for idx, bound in enumerate(bounds):
                    if bound is None:
                        continue
                    if str(bound) not in duplicated:
                        icon = Icon(screen, nodes[idx], bound, rotation, tag, 's0')
                        icon.crop_image(screen.png)
                        data.append(icon.get_json())
                        duplicated.add(str(bound))
    json.dump(data, open(json_out, 'w'))


def remove_invalid_item_json():
    data = json.load(open(json_out, 'r'))
    new_data = []
    for item in data:
        img_path = item["img_path"]
        if os.path.exists(os.path.join(out_root,img_path)):
            new_data.append(item)
    print("item number:", len(new_data))
    json.dump(new_data, open(json_out, 'w'))


def get_content_labeldroid():
    res = os.path.join(out_root, 'result.json')
    out = os.path.join(out_root, 'sample_ld.json')
    data = json.load(open(json_out, 'r'))
    data_res = json.load(open(res, 'r'))
    caption_dict = dict()
    for item in data_res:
        idx = item["image_id"].index('icon-label/')
        image_id = item["image_id"][idx+11:]
        caption_dict[image_id] = item["caption"]
    for item in data:
        item["content"] = caption_dict[item["img_path"]]
    json.dump(data, open(out, 'w'))


def get_content_title():
    data = json.load(open(json_out, 'r'))
    out = os.path.join(out_root, 'sample_title.json')
    for item in data:
        item["content"] = item["context"]["title"]
    json.dump(data, open(out, 'w'))


out_root = 'data/2'
json_out = out = os.path.join(out_root, 'sample_title_.json')
if __name__ == '__main__':
    # launch()
    remove_invalid_item_json()
    # get_content_labeldroid()
    # get_content_title()
from lxml import etree
import re
import os
import shutil
from PIL import Image
import json

data_path = os.path.dirname(os.path.abspath(__file__)).replace('burt-nl-improvement/icon-label', 'data/TraceReplayer-Data')
bounds_pattern = re.compile(r'\[-?(\d+),-?(\d+)\]\[-?(\d+),-?(\d+)\]')
json_data = []

# mark_tag = {'icon': '//*[@resource-id="android:id/icon"]',
# 'image': '//*[@class="android.widget.ImageView"]',
# 'button': '//*[@class="android.widget.ImageButton"]'}
mark_tag = {'icon': '//*[@resource-id="android:id/icon"]', 'ImageButton': '//*[@class="android.widget.ImageButton"]'}
appName = {'de.delusions.measure': 'droidweight',
           'org.gnucash.android': 'gnucash',
           'me.anon.grow': 'growtracker',
           'com.evancharlton.mileage': 'mileage',
           'com.markuspage.android.atimetracker': 'atimetracker',
           'de.danoeh.antennapod.debug': 'antennapod',
           'uk.co.bitethebullet.android.token': 'token'
           }


def clear_slash(text):
    if '/' not in text:
        return text
    return text[text.index('/')+1:]


def query_xml(xml_file, xpath):
    tree = etree.parse(open(xml_file, 'r'), etree.XMLParser(ns_clean=True))
    root = tree.getroot()
    return root.xpath(xpath)


def extract_bounds_from_node(node):
    match = bounds_pattern.match(node.attrib['bounds'])
    if match:
        return [int(match[1]), int(match[2]), int(match[3]), int(match[4])]
    return None


dirs = ['com.evancharlton.mileage-3.1.1',
        'me.anon.grow-2.3.1',
        'com.markuspage.android.atimetracker-0.20',
        'org.gnucash.android-1.0.3',
        'de.danoeh.antennapod.debug-1.6.2.3',
        'org.gnucash.android-2.1.3',
        'de.delusions.measure-1.5.4',
        'uk.co.bitethebullet.android.token-2.10']
# dirs = ['org.gnucash.android-2.1.3', ]


class Screen:
    def __init__(self, xml, root):
        self.root = root
        self.xml = xml
        self.appPackage = None
        self.appVersion = None
        self.executionCtr = None
        self.executionJson = None
        self.seq = None
        self.png = None
        self.parse_xml()
        self.set_png()
        self.set_execution_json()

    def parse_xml(self):
        _xml = os.path.basename(self.xml).split('-')
        self.appPackage = _xml[0]
        self.appVersion = _xml[1]
        self.executionCtr = _xml[2]
        self.seq = int(_xml[5][:-4])

    def set_execution_json(self):
        json_path = os.path.join(self.root, 'Execution-{}.json'.format(self.executionCtr))
        self.executionJson = json.load(open(json_path, 'r'))

    def set_png(self):
        self.png = os.path.join(self.root, 'screenshots', '{}.User-Trace.{}.{}_{}_{}{}.png'.
                                format(self.appPackage,
                                       self.executionCtr,
                                       self.appPackage,
                                       self.appVersion,
                                       appName[self.appPackage],
                                       self.seq))

    '''
    self.xml = xml
    self.appPackage = None
    self.appVersion = None
    self.executionCtr = None
    self.executionJson = None
    self.seq = None
    self.png = None
    '''

    def __str__(self):
        return '''
        -----------------------
        xml: {}
        png: {}
        appPackage: {}
        executionCtr: {}
        seq: {}
        -----------------------
        '''.format(self.xml, self.png, self.appPackage, self.executionCtr, self.seq)


class Icon:
    def __init__(self, screen: Screen, node, bound, icon_type, root):
        self.root = root
        self.screen = screen
        self.node = node
        self.parent = node.getparent()
        self.siblings = []
        self.get_siblings()
        self.bound = bound
        self.icon_type = icon_type
        self.img_id = None
        self.img_path = None
        self.set_img_id()
        self.set_img_path()

    def get_siblings(self):
        pointer = self.node.getprevious()
        while pointer is not None:
            self.siblings.append(pointer)
            pointer = pointer.getprevious()
        pointer = self.node.getnext()
        while pointer is not None:
            self.siblings.append(pointer)
            pointer = pointer.getnext()

    def set_img_id(self):
        s = '{}.{}'.format(self.screen.png, str(self.bound))
        self.img_id = abs(hash(s)) % (10 ** 5)

    def set_img_path(self):
        self.img_path = os.path.join(self.root, str(self.img_id) + '.png')

    def get_json(self):
        # todo: context ..
        activity_name = self.screen.executionJson['steps'][self.screen.seq-1]['screen']['activity']
        android_id = clear_slash(self.node.get('resource-id'))
        siblings = [{"id": clear_slash(x.get('resource-id')), "text": x.get('text')} for x in self.siblings]
        return {
            "id": self.img_id,
            "img_id": self.img_id,
            "img_path": self.img_path,
            "pkg_name": self.screen.appPackage,
            "activity_name": activity_name,
            "content": "",
            "bounds": self.bound,
            "android-id": android_id,
            "context": {
                "activity_name": "",
                "title": "",
                "family": {
                    "father": {
                        "id": clear_slash(self.parent.get('resource-id')),
                        "text": self.parent.get('text')
                    },
                    "siblings": siblings
                },
                "category": "<UNK>",
                "location": -1
            },
            "type": self.icon_type,
            "rotation": "0"
        }

    def crop_image(self, src):
        img = Image.open(src)
        img_res = img.crop((self.bound[0], self.bound[1], self.bound[2], self.bound[3]))
        img_res.save(self.img_path, dpi=(600, 600))


def launch():
    data = []
    for d in dirs:
        xml_data_path = os.path.join(data_path, d)
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        duplicated = set()
        for xml in xml_list:
            screen = Screen(xml, xml_data_path)
            print(screen)
            for tag, xpath in mark_tag.items():
                nodes = query_xml(xml, xpath)
                bounds = [extract_bounds_from_node(node) for node in nodes]
                for idx, bound in enumerate(bounds):
                    if bound is None:
                        continue
                    if str(bound) not in duplicated:
                        icon = Icon(screen, nodes[idx], bound, tag, out_path)
                        icon.crop_image(screen.png)
                        data.append(icon.get_json())
                        duplicated.add(str(bound))
    out = 'sample_test.json'
    json.dump(data, open(out, 'w'))


def update_json():
    out = 'sample_test.json'
    data = json.load(open(out, 'r'))
    new_data = []
    for item in data:
        img_path = item["img_path"]
        if os.path.exists(img_path):
            new_data.append(item)
    print("item number:", len(new_data))
    json.dump(new_data, open(out, 'w'))


def add_content():
    out = 'sample_test.json'
    res = 'result.json'
    data = json.load(open(out, 'r'))
    data_res = json.load(open(res, 'r'))
    caption_dict = dict()
    for item in data_res:
        image_id = item["image_id"].replace("/Users/zhouying/git/burt-project/burt/burt-nl-improvement/icon-label/", "")
        caption_dict[image_id] = item["caption"]
    for item in data:
        item["content"] = caption_dict[item["img_path"]]
    json.dump(data, open(out, 'w'))


out_path = 's0'
if __name__ == '__main__':
    # if os.path.exists(out_path):
    #     shutil.rmtree(out_path)
    # os.makedirs(out_path)
    # launch()
    # update_json()
    add_content()
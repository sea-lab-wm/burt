from lxml import etree
import re
import os
from PIL import Image

data_path = os.path.dirname(os.path.abspath(__file__)).replace('burt-nl-improvement/icon-label', 'data/TraceReplayer-Data')
bounds_pattern = re.compile(r'\[-?(\d+),-?(\d+)\]\[-?(\d+),-?(\d+)\]')
json_data = []

# mark_tag = {'icon': '//*[@resource-id="android:id/icon"]',
# 'image': '//*[@class="android.widget.ImageView"]',
# 'button': '//*[@class="android.widget.ImageButton"]'}
mark_tag = {'image': '//*[@class="android.widget.ImageView"]'}
appName = {'de.delusions.measure': 'droidweight',
           'org.gnucash.android': 'gnucash',
           'me.anon.grow': 'growtracker',
           'com.evancharlton.mileage': 'mileage',
           'com.markuspage.android.atimetracker': 'atimetracker',
           'de.danoeh.antennapod.debug': 'antennapod',
           'uk.co.bitethebullet.android.token': 'token'
           }


def query_xml(xml_file, xpath):
    tree = etree.parse(open(xml_file, 'r'), etree.XMLParser(ns_clean=True))
    root = tree.getroot()
    res = []
    for item in root.xpath(xpath):
        match = bounds_pattern.match(item.attrib['bounds'])
        if match:
            res.append([int(match[1]), int(match[2]), int(match[3]), int(match[4])])
    return res


def crop_image(src, dest, bounds):
    img = Image.open(src)
    img_res = img.crop((bounds[0], bounds[1], bounds[2], bounds[3]))
    img_res.save(dest, dpi=(600, 600))


# dirs = ['com.evancharlton.mileage-3.1.1',
#         'me.anon.grow-2.3.1',
#         'com.markuspage.android.atimetracker-0.20',
#         'org.gnucash.android-1.0.3',
#         'de.danoeh.antennapod.debug-1.6.2.3',
#         'org.gnucash.android-2.1.3',
#         'de.delusions.measure-1.5.4',
#         'uk.co.bitethebullet.android.token-2.10']
dirs = ['org.gnucash.android-2.1.3', ]


class Screen:
    def __init__(self, xml, root):
        self.root = root
        self.xml = xml
        self.appPackage = None
        self.appVersion = None
        self.executionCtr = None
        self.seq = None
        self.png = None
        self.parse_xml()
        self.get_png()

    def parse_xml(self):
        _xml = os.path.basename(self.xml).split('-')
        self.appPackage = _xml[0]
        self.appVersion = _xml[1]
        self.executionCtr = _xml[2]
        self.seq = _xml[5][:-4]

    def get_png(self):
        self.png = os.path.join(self.root, 'screenshots', '{}.User-Trace.{}.{}_{}_{}{}.png'.
                                format(self.appPackage,
                                       self.executionCtr,
                                       self.appPackage,
                                       self.appVersion,
                                       appName[self.appPackage],
                                       self.seq))


class Icon:
    def __init__(self, screen: Screen, bound, root):
        self.root = root
        self.screen = screen
        self.bound = bound
        self.img_id = None
        self.img_path = None
        self.set_img_id()
        self.set_img_path()

    def set_img_id(self):
        s = '{}.{}'.format(self.screen.png, str(self.bound))
        self.img_id = abs(hash(s)) % (10 ** 5)

    def set_img_path(self):
        self.img_path = os.path.join(self.root, str(self.img_id) + '.png')

    def get_json(self):
        # todo: activity_name, context ..
        return {
            "id": '',
            "img_id": self.img_id,
            "img_path": self.img_id,
            "pkg_name": self.screen.appPackage,
            "activity_name": "",
            "content": "",
            "bounds": self.bound,
            "android-id": "",
            "context": {
                "activity_name": "",
                "title": "",
                "family": {
                    "father": {
                        "id": "",
                        "text": ""
                    },
                    "siblings": []
                },
            },
            "type": "",
            "rotation": "0"
        }


def launch():
    for d in dirs:
        xml_data_path = os.path.join(data_path, d)
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        duplicated = set()
        for xml in xml_list:
            # print(xml)
            screen = Screen(xml, xml_data_path)
            bounds = query_xml(xml, '//*[@class="android.widget.ImageView"]')
            src = screen.png
            for bound in bounds:
                if str(bound) not in duplicated:
                    icon = Icon(screen, bound, 's0')
                    # crop_image(src, icon.img_path, bound)
                    duplicated.add(str(bound))


if __name__ == '__main__':
    launch()

from screen import Screen
import os
import re
from PIL import Image

# zones = {1: [0, 0, 100, 200], 2: [100, 0, 700, 200], 3: [700, 0, 800, 200],
#              7: [0, 1100, 100, 1216], 8: [100, 1100, 700, 1216], 9: [700, 1100, 800, 1216],
#              4: [0, 200, 100, 1100], 5: [100, 200, 700, 1100], 6: [700, 200, 800, 1100]}
zones = {1: [0, 0, 100, 400], 2: [100, 0, 900, 400], 3: [900, 0, 1080, 400],
             7: [0, 1720, 100, 1920], 8: [100, 1720, 900, 1920], 9: [900, 1720, 1080, 1920],
             4: [0, 400, 100, 1720], 5: [100, 400, 900, 1720], 6: [900, 400, 1080, 1720]}


def clear_slash(text):
    if '/' not in text:
        return text
    return text[text.index('/')+1:]


def camel_case_split(text):
    words = re.findall(r'[A-Z](?:[a-z]+|[A-Z]*(?=[A-Z]|$))', text)
    return ' '.join(words)


class Icon:
    def __init__(self, screen: Screen, node, bound, rotation, icon_type, root):
        self.root = root
        self.screen = screen
        self.node = node
        self.parent = node.getparent()
        self.siblings = []
        self.get_siblings()
        self.bound = bound
        self.rotation = rotation
        self.icon_type = icon_type
        self.img_id = None
        self.img_path = None
        self.location = None
        self.activity_name = None
        self.title = None
        self.set_img_id()
        self.set_img_path()
        self.set_location()
        self.set_activity_name()
        self.set_title()

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

    def set_location(self):
        for zone, zone_bounds in zones.items():
            if zone <= 3:
                if zone_bounds[0] <= self.bound[0] and self.bound[2] <= zone_bounds[2]:
                    self.location = zone
            elif zone >= 7:
                if zone_bounds[1] <= self.bound[1] and self.bound[3] <= zone_bounds[3]:
                    self.location = zone
            else:
                if zone_bounds[0] <= self.bound[0] and self.bound[2] <= zone_bounds[2]:
                    self.location = zone

    def set_activity_name(self):
        # class[-1] split by camelcase
        lastname = self.node.get('class').split('.')[-1]
        self.activity_name = camel_case_split(lastname)

    def set_title(self):
        # title -> content_desc -> idXml
        title = self.node.get('text')
        content_desc = self.node.get('content-desc')
        idXml = clear_slash(self.node.get('resource-id'))
        if len(title) == 0:
            if len(content_desc) == 0:
                if len(idXml) == 0:
                    self.title = ""
                else:
                    self.title = idXml
            else:
                self.title = content_desc
        else:
            self.title = title

    def get_json(self):
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
                "activity_name": self.activity_name,
                "title": self.title,
                "family": {
                    "father": {
                        "id": clear_slash(self.parent.get('resource-id')),
                        "text": self.parent.get('text')
                    },
                    "siblings": siblings
                },
                "category": "<UNK>",
                "location": self.location
            },
            "type": self.icon_type,
            "rotation": self.rotation
        }

    def crop_image(self, src):
        img = Image.open(src)
        img_res = img.crop((self.bound[0], self.bound[1], self.bound[2], self.bound[3]))
        img_res.save(self.img_path, dpi=(600, 600))
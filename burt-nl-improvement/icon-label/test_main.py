from unittest import TestCase
import os
from lxml import etree
from main import *

data_path = os.path.dirname(os.path.abspath(__file__)).replace('burt-nl-improvement/icon-label', 'data/TraceReplayer-Data')


class TestScreen(TestCase):
    def test_get_execution_json(self):
        xml_data_path = os.path.join(data_path, 'org.gnucash.android-2.1.3')
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        for xml in xml_list:
            screen = Screen(xml, xml_data_path)
            print(screen.executionJson['steps'][screen.seq]['screen']['activity'])
            # print(screen.executionJson['steps'][screen.seq]['dynGuiComponents'])
            break

    def test_get_id(self):
        xml_data_path = os.path.join(data_path, 'org.gnucash.android-2.1.3')
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        for xml in xml_list:
            screen = Screen(xml, xml_data_path)
            nodes = query_xml(xml, '//*[@class="android.widget.ImageView"]')
            bounds = [extract_bounds_from_node(node) for node in nodes]
            src = screen.png
            for idx, bound in enumerate(bounds):
                if bound is None:
                    continue
                icon = Icon(screen, nodes[idx], bound, 's0')
                print("xml", xml)
                node = nodes[idx]
                print("id", clear_slash(node.get('resource-id')))
                parent = node.getparent()
                # print(etree.tostring(parent))
                print("parent text", parent.get('text'))
                print('node')
                # print(etree.tostring(node))
                print('siblings')
                siblings = [{"id": clear_slash(x.get('resource-id')), "text": x.get('text')} for x in icon.siblings]
                print(siblings)
                # crop_image(src, icon.img_path, bound)
            break

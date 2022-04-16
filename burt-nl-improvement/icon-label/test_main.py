from unittest import TestCase
import os
from lxml import etree
from main import *
from icon import zones

data_path = os.path.dirname(os.path.abspath(__file__)).replace('burt-nl-improvement/icon-label', 'data/TraceReplayer-Data')


class TestScreen(TestCase):
    def test_get_execution_json(self):
        xml_data_path = os.path.join(data_path, 'org.gnucash.android-2.1.3')
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        xml = xml_list[0]
        screen = Screen(xml, xml_data_path)
        print(screen.executionJson['steps'][screen.seq]['screen']['activity'])

    def test_get_rotation(self):
        xml_data_path = os.path.join(data_path, 'org.gnucash.android-2.1.3')
        xml_list = [os.path.join(xml_data_path, f) for f in os.listdir(xml_data_path) if '.xml' in f]
        xml = xml_list[0]
        xpath = '//*[@class="android.widget.ImageButton"]'
        nodes, rotation = query_xml(xml, xpath)
        print(rotation)

    def test(self):
        bounds = [
            183,
            283,
            293,
            441
        ]
        location = None
        print(bounds)
        for zone, zone_bounds in zones.items():
            print(zone_bounds)
            if zone <= 3:
                if zone_bounds[0] <= bounds[0] and bounds[2] <= zone_bounds[2]:
                    location = zone
            elif zone >= 7:
                if zone_bounds[1] <= bounds[1] and bounds[3] <= zone_bounds[3]:
                    location = zone
            else:
                if zone_bounds[0] <= bounds[0] and bounds[2] <= zone_bounds[2]:
                    location = zone
        print(location)


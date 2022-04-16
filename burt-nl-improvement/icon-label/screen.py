import os
import json
appName = {'de.delusions.measure': 'droidweight',
           'org.gnucash.android': 'gnucash',
           'me.anon.grow': 'growtracker',
           'com.evancharlton.mileage': 'mileage',
           'com.markuspage.android.atimetracker': 'atimetracker',
           'de.danoeh.antennapod.debug': 'antennapod',
           'uk.co.bitethebullet.android.token': 'token'
           }


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

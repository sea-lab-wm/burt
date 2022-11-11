#!/usr/bin/env python

import re
import sys
import math
#from evdev.ecodes import *

EV_SYN = 0
EV_NAV = 2
EV_KEY = 1
EV_ABS = 3
ABS_X = 0
ABS_Y = 1
ABS_MT_POSITION_X = 53
ABS_MT_POSITION_Y = 54
ABS_MT_TRACKING_ID = 57
SYN_REPORT = 0
BACK = 6
BTN_TOUCH = 330

LONG_CLICK_DURATION = 0.5
CLICK_RING = 20
pattern = re.compile(
        r'\[\s*(\d*\.\d*)\] /dev/input/event(\d*): ' \
        r'([0-9a-f]{4}) ([0-9a-f]{4}) ([0-9a-f]{8})')

input_file = open(sys.argv[1])

x, y = 0, 0
was_finger_down = False
finger_down = False
events = []
coords = []
cnt = 0

for line in input_file:
    #print(line)
    if line[0] != '[':
        continue
    info = re.match(pattern, line)
    if info is None:
        continue
    time, device, type, code, value = re.match(pattern, line).groups()
    time = float(time)
    device = int(device)
    type = int(type, 16)
    code = int(code, 16)
    value = int(value, 16)
    event = device, type, code, value
    events.append(event)
    
    # Button down.
    if type == EV_KEY:

        # If the touch screen has been toggled, let sync events
        # handle the logic.
        if code == BTN_TOUCH:
            finger_down = value

        # For any other button, print the action after the button has
        # been released.
        elif value == 0:
            duration = time - start_time
            #print str(duration)+','+str(start_time)+','+str(time)
            print ('BACK')
            events = []
        elif value == 1:
            start_time = time

    # Absolute coordinates from a touchscreen.
    elif type == EV_ABS:
        if code in (ABS_X, ABS_MT_POSITION_X):
            x = value
        elif code in (ABS_Y, ABS_MT_POSITION_Y):
            y = value
        elif (code == ABS_MT_TRACKING_ID):
            finger_down = (value != 0xffffffff) 

    # Sync.
    elif (type == EV_SYN and code == SYN_REPORT) :

        # If the finger has changed:
        if finger_down != was_finger_down:

            # Restart the coordinate list.
            if finger_down:
                start_time = time
                coords = [(x, y)]

            # If the finger is removed from the touchscreen, end the
            # action and print the events.
            else:
                duration = time - start_time
                initial_location = coords[0]
                last_location = coords[ len(coords)-1]
                event_label = "";
                event_type = 0;
                
                distance = math.sqrt(( (int(initial_location[0]) - int(last_location[0]))**2) + ((int(initial_location[1]) - int(last_location[1]))**2))
                        
                if duration >=  LONG_CLICK_DURATION:
                    event_label = "LONG_CLICK";
                    event_type = 1;
                    if distance > CLICK_RING:
                        event_label = "SWIPE";
                        event_type = 2;
                        #print coords
                else:
                    event_label = "CLICK";
                    #print coords
                    if distance > CLICK_RING:
                        event_label = "SWIPE";
                        event_type = 2; 
                    
                cnt = cnt + 1
                #print (cnt)
                print (str(event_type)+'#'+event_label+'#'+str(distance)+'#'+str(duration)+'#'+str(initial_location)+'#'+str(last_location))
                events = []
                coords = []

            was_finger_down = finger_down

        # Append the current coordinates to the list.
        else:
            coords.append((x, y))

    elif type == EV_NAV and code == BACK :
        print ('BACK')
    else:
         print ('type:', type, 'code:', code)
         raise Exception('unrecognized event: {}'.format(event))

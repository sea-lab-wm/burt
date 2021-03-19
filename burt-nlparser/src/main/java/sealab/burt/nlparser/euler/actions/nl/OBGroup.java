package sealab.burt.nlparser.euler.actions.nl;

/**
 * This contains categorization for OB action types
 *
 * @author Carlos Bernal
 */
public enum OBGroup {
    CRASH(200), VISIBILITY(201), CONTENT(202), FOCUS(203), NO_CHANGE(204);

    private int event;

    OBGroup(int event) {
        this.event = event;
    }

    /**
     * @return the event
     */
    public int getEvent() {
        return event;
    }

    public static OBGroup getByEvent(int event) {
        for (OBGroup ob : OBGroup.values()) {
            if (ob.getEvent() == event) {
                return ob;
            }
        }
        return null;
    }
    
    public static OBGroup getByString(String event) {
        for (OBGroup ob : OBGroup.values()) {
            if (ob.toString().equals(event)) {
                return ob;
            }
        }
        return null;
    }

}

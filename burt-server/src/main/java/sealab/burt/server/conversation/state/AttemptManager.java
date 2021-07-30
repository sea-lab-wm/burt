package sealab.burt.server.conversation.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.LinkedHashMap;

public @Slf4j
class AttemptManager {

    public enum AttemptType {
        OB_MATCHED, OB_NO_MATCH, OB_SCREENS
    }

    public static final Integer MAX_ATTEMPTS_OB_MATCHED = 3;
    public static final Integer MAX_ATTEMPTS_OB_NO_MATCH = 3;
    public static final Integer MAX_ATTEMPTS_OB_SCREENS = 3;

    private final HashMap<AttemptType, MutablePair<Integer, Integer>> attempts = new LinkedHashMap<>() {
        {
            put(AttemptType.OB_MATCHED, new MutablePair<>(MAX_ATTEMPTS_OB_MATCHED, -1));
            put(AttemptType.OB_NO_MATCH, new MutablePair<>(MAX_ATTEMPTS_OB_NO_MATCH, -1));
            put(AttemptType.OB_SCREENS, new MutablePair<>(MAX_ATTEMPTS_OB_SCREENS, -1));
        }
    };

    //-------------------------------------------------

    public void increaseCurrentAttemptObScreens() {
        increaseCurrentAttempt(AttemptType.OB_SCREENS);
    }

    public Integer getCurrentAttemptObScreens() {
        return getCurrentAttempt(AttemptType.OB_SCREENS);
    }

    public void initOrIncreaseCurrentAttemptObScreens() {
        initOrIncreaseCurrentAttempt(AttemptType.OB_SCREENS);
    }

    public boolean checkNextAttemptAndResetObScreens() {
        return checkNextAttemptAndReset(AttemptType.OB_SCREENS);
    }

    //-------------------------------------------------

    public boolean checkNextAttemptAndResetObMatched() {
        return checkNextAttemptAndReset(AttemptType.OB_MATCHED);
    }


    public void initOrIncreaseCurrentAttemptObMatched() {
        initOrIncreaseCurrentAttempt(AttemptType.OB_MATCHED);
    }


    public void initAttemptObMatched() {
        initiateAttempt(AttemptType.OB_MATCHED);
    }

    public Integer getCurrentAttemptObMatched() {
        return getCurrentAttempt(AttemptType.OB_MATCHED);
    }


    public boolean isCurrentAttemptInitiatedObMatched() {
        return isCurrentAttemptInitiated(AttemptType.OB_MATCHED);
    }

    //-------------------------------------------------


    public void initOrIncreaseCurrentAttemptObNoMatch() {
        initOrIncreaseCurrentAttempt(AttemptType.OB_NO_MATCH);
    }

    public boolean checkNextAttemptAndResetObNoMatch() {
        return checkNextAttemptAndReset(AttemptType.OB_NO_MATCH);
    }

    //------------------------------------------------

    private boolean isCurrentAttemptInitiated(AttemptType attemptType) {
        return getCurrentAttempt(attemptType) != -1;
    }

    private void initiateAttempt(AttemptType attemptType) {
        Integer currentAttempt = getCurrentAttempt(attemptType);
        if (currentAttempt != -1) {
            log.warn(String.format("The current attempt (%s) was already initiated: %s",
                    attemptType,
                    currentAttempt));
        }
        setCurrentAttempt(attemptType, 1);
    }

    private void setCurrentAttempt(AttemptType type, int value) {
        attempts.get(type).setRight(value);
    }

    private Integer getCurrentAttempt(AttemptType type) {
        return attempts.get(type).right;
    }

    private MutablePair<Integer, Integer> getAttempt(AttemptType type) {
        return attempts.get(type);
    }

    private boolean checkNextAttemptAndReset(AttemptType attemptType) {
        MutablePair<Integer, Integer> attempt = getAttempt(attemptType);
        Integer currentAttempt = attempt.right;
        Integer maxAttempt = attempt.left;
        if (currentAttempt >= maxAttempt) {
            resetAttempt(attemptType);
            return false;
        }
        return true;
    }

    private void resetAttempt(AttemptType attemptType) {
        setCurrentAttempt(attemptType, -1);
    }

    private void initOrIncreaseCurrentAttempt(AttemptType attemptType) {
        Integer currentAttempt = getCurrentAttempt(attemptType);
        if (currentAttempt != -1) {
            setCurrentAttempt(attemptType, currentAttempt + 1);
        }else{
            setCurrentAttempt(attemptType, 1);
        }
    }

    private void increaseCurrentAttempt(AttemptType attemptType) {
        Integer currentAttempt = getCurrentAttempt(attemptType);
        if (currentAttempt != -1) {
            setCurrentAttempt(attemptType, currentAttempt + 1);
        } else{
            log.warn(String.format("The current attempt (%s) was not initiated: %s",
                    attemptType,
                    currentAttempt));
        }
    }
}

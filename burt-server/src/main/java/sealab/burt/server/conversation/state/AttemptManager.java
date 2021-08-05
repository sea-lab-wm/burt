package sealab.burt.server.conversation.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.LinkedHashMap;

public @Slf4j
class AttemptManager {

    public enum AttemptType {
        //OB
        OB_MATCHED, OB_NO_MATCH, OB_NOT_PARSED, OB_SCREENS,
        //EB
        EB_NO_MATCH, EB_NOT_PARSED,
        //S2R
        S2R_NO_MATCH, S2R_NOT_PARSED, S2R_MATCHED, S2R_INPUT, S2R_AMBIGUOUS
    }

    public static final Integer MAX_ATTEMPTS_3 = 3;
    public static final Integer MAX_ATTEMPTS_2 = 2;

    private final HashMap<AttemptType, MutablePair<Integer, Integer>> attempts = new LinkedHashMap<>() {
        {
            //each pair is: (max # of attempts, current attempt)
            //initially, the current attempt is -1

            //OB
            put(AttemptType.OB_MATCHED, new MutablePair<>(MAX_ATTEMPTS_3, -1)); //confirmation of OB HQ
            put(AttemptType.OB_NO_MATCH, new MutablePair<>(MAX_ATTEMPTS_3, -1)); //no screen match
            put(AttemptType.OB_SCREENS, new MutablePair<>(MAX_ATTEMPTS_3, -1)); //multiple screens matched
            put(AttemptType.OB_NOT_PARSED, new MutablePair<>(MAX_ATTEMPTS_3, -1)); //no parsed

            //EB
            put(AttemptType.EB_NO_MATCH, new MutablePair<>(MAX_ATTEMPTS_2, -1));
            put(AttemptType.EB_NOT_PARSED, new MutablePair<>(MAX_ATTEMPTS_2, -1));

            //S2R
            put(AttemptType.S2R_NO_MATCH, new MutablePair<>(MAX_ATTEMPTS_2, -1)); //no step matched
            put(AttemptType.S2R_NOT_PARSED, new MutablePair<>(MAX_ATTEMPTS_2, -1)); //no step parsed
            put(AttemptType.S2R_AMBIGUOUS, new MutablePair<>(MAX_ATTEMPTS_2, -1)); //multiple match
            put(AttemptType.S2R_MATCHED, new MutablePair<>(MAX_ATTEMPTS_2, -1)); //confirmation of S2R HQ
            put(AttemptType.S2R_INPUT, new MutablePair<>(MAX_ATTEMPTS_2, -1)); //missing input

        }
    };

    //---------------------------------------------------

    public void initOrIncreaseCurrentAttemptS2RInput() {
        initOrIncreaseCurrentAttempt(AttemptType.S2R_INPUT);
    }

    public boolean checkNextAttemptAndResetS2RInput() {
        return checkNextAttemptAndReset(AttemptType.S2R_INPUT);
    }

    public void resetCurrentAttemptS2RInput() {
        resetAttempt(AttemptType.S2R_INPUT);
    }

    //---------------------------------------------------

    public void initOrIncreaseCurrentAttemptS2RMatch() {
        initOrIncreaseCurrentAttempt(AttemptType.S2R_MATCHED);
    }

    public boolean checkNextAttemptAndResetS2RMatch() {
        return checkNextAttemptAndReset(AttemptType.S2R_MATCHED);
    }

    public void resetCurrentAttemptS2RMatch() {
        resetAttempt(AttemptType.S2R_MATCHED);
    }

    public Integer getCurrentAttemptS2RMatched() {
        return getCurrentAttempt(AttemptType.S2R_MATCHED);
    }

    public Integer getMaxAttemptsS2RMatched() {
        return getMaxAttempt(AttemptType.S2R_MATCHED);
    }

    //---------------------------------------------------

    public void initOrIncreaseCurrentAttemptS2RNotParsed() {
        initOrIncreaseCurrentAttempt(AttemptType.S2R_NOT_PARSED);
    }

    public boolean checkNextAttemptAndResetS2RNotParsed() {
        return checkNextAttemptAndReset(AttemptType.S2R_NOT_PARSED);
    }

    public void resetCurrentAttemptS2RNotParsed() {
        resetAttempt(AttemptType.S2R_NOT_PARSED);
    }

    //---------------------------------------------------

    public void initOrIncreaseCurrentAttemptS2RNoMatch() {
        initOrIncreaseCurrentAttempt(AttemptType.S2R_NO_MATCH);
    }

    public boolean checkNextAttemptAndResetS2RNoMatch() {
        return checkNextAttemptAndReset(AttemptType.S2R_NO_MATCH);
    }

    public void resetCurrentAttemptS2RNoMatch() {
        resetAttempt(AttemptType.S2R_NO_MATCH);
    }

    //---------------------------------------------------

    public void initOrIncreaseCurrentAttemptS2RAmbiguous() {
        initOrIncreaseCurrentAttempt(AttemptType.S2R_AMBIGUOUS);
    }

    public boolean checkNextAttemptAndResetS2RAmbiguous() {
        return checkNextAttemptAndReset(AttemptType.S2R_AMBIGUOUS);
    }

    public void resetCurrentAttemptS2RAmbiguous() {
        resetAttempt(AttemptType.S2R_AMBIGUOUS);
    }

    //---------------------------------------------------


    public void initOrIncreaseCurrentAttemptEbNoMatch() {
        initOrIncreaseCurrentAttempt(AttemptType.EB_NO_MATCH);
    }

    public boolean checkNextAttemptAndResetEbNoMatch() {
        return checkNextAttemptAndReset(AttemptType.EB_NO_MATCH);
    }

    //-------------------------------------------------

    public void initOrIncreaseCurrentAttemptEbNotParsed() {
        initOrIncreaseCurrentAttempt(AttemptType.EB_NOT_PARSED);
    }

    public boolean checkNextAttemptAndResetEbNotParsed() {
        return checkNextAttemptAndReset(AttemptType.EB_NOT_PARSED);
    }

    //-------------------------------------------------

    public void initOrIncreaseCurrentAttemptObNotParsed() {
        initOrIncreaseCurrentAttempt(AttemptType.OB_NOT_PARSED);
    }

    public boolean checkNextAttemptAndResetObNotParsed() {
        return checkNextAttemptAndReset(AttemptType.OB_NOT_PARSED);
    }

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

    public Integer getMaxAttemptObScreens() {
        return getMaxAttempt(AttemptType.OB_SCREENS);
    }


    //-------------------------------------------------

    public boolean checkNextAttemptAndResetObMatched() {
        return checkNextAttemptAndReset(AttemptType.OB_MATCHED);
    }

    public Integer getMaxAttemptsObMatched() {
        return getMaxAttempt(AttemptType.OB_MATCHED);
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

    private Integer getMaxAttempt(AttemptType type) {
        return attempts.get(type).left;
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
        } else {
            setCurrentAttempt(attemptType, 1);
        }
    }

    private void increaseCurrentAttempt(AttemptType attemptType) {
        Integer currentAttempt = getCurrentAttempt(attemptType);
        if (currentAttempt != -1) {
            setCurrentAttempt(attemptType, currentAttempt + 1);
        } else {
            log.warn(String.format("The current attempt (%s) was not initiated: %s",
                    attemptType,
                    currentAttempt));
        }
    }
}

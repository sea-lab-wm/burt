package sealab.burt.qualitychecker.actionparser;

public enum MatchingResult {

    //action results
    ACTION_NOT_MAPPED, AMBIGUOUS_ACTION, ACTION_NOT_MATCHED, UNKNOWN_ACTION,

    //component matching results
    EMPTY_OBJECTS, COMPONENT_NOT_FOUND, MULTIPLE_COMPONENTS_FOUND, COMPONENT_NOT_SPECIFIED, INCORRECT_COMPONENT_FOUND,
    COMPONENT_FOUND,

     //text parsing results
    EMPTY_TEXT, 
    
    //behavior results
    BEHAVIOR_NOT_FOUND, BEHAVIOR_NOT_SUPPORTED,  BEHAVIOR_NOT_MAPPED
}

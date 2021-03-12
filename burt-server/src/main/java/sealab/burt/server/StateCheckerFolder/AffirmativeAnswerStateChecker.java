class AffirmativeAnswerStateChecker implements StateChecker{
    String defaultAction;
    NoStateChecker(String defaultAction){
        this.defaultAction = defaultAction;
    }
    public String nextAction(){
        State state = getState();
        //check the state...
        String nextAction = determineNextAction(state);
        return nextAction;
    }
}
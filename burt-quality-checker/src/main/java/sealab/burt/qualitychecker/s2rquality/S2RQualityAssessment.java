package sealab.burt.qualitychecker.s2rquality;


import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.AppStep;

import java.util.ArrayList;
import java.util.List;

/**
 * The quality assessment comprises:
 * 1. the quality category
 * 2. details about the assessment
 */
public class S2RQualityAssessment {

    //quality category
    private S2RQualityCategory category;

    //details of the assessment in the form of matched and inferred steps for the HQ and M categories
    private List<AppStep> matchedSteps;
    private List<AppStep> inferredSteps;

    // input value in the case of  the I-LQ category
    private String inputValue;

    // these are for vocabulary mismatch cases (V-LQ)
    private boolean verbVocabMismatch;
    private boolean objsVocabMismatch;

    // ambiguous cases (A-LQ)
    private List<String> ambiguousCases;
    private List<AppGuiComponent> ambiguousComponents;
    private List<String> ambiguousActions;

    public List<AppGuiComponent> getAmbiguousComponents() {
        return ambiguousComponents;
    }

    public void setAmbiguousComponents(List<AppGuiComponent> ambiguousComponents) {
        this.ambiguousComponents = ambiguousComponents;
    }

    public List<String> getAmbiguousActions() {
        return ambiguousActions;
    }

    public void setAmbiguousActions(List<String> ambiguousActions) {
        this.ambiguousActions = ambiguousActions;
    }

    public List<String> getAmbiguousCases() {
        return ambiguousCases;
    }

    public void setAmbiguousCases(List<String> ambiguousCases) {
        this.ambiguousCases = ambiguousCases;
    }

    public boolean isVerbVocabMismatch() {
        return verbVocabMismatch;
    }

    public void setVerbVocabMismatch() {
        this.verbVocabMismatch = true;
    }

    public boolean isObjsVocabMismatch() {
        return objsVocabMismatch;
    }

    public void setObjsVocabMismatch() {
        this.objsVocabMismatch = true;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public S2RQualityAssessment() {
    }

    public S2RQualityAssessment(S2RQualityCategory category) {
        this.category = category;
    }

    public void addMatchedStep(AppStep step){
        if (matchedSteps == null ) matchedSteps = new ArrayList<>();
        matchedSteps.add(step);
    }

    public void addInferredStep(AppStep step){
        if (inferredSteps == null ) inferredSteps = new ArrayList<>();
        inferredSteps.add(step);
    }

    public void addInferredSteps(List<AppStep> steps){
        if (inferredSteps == null ) inferredSteps = new ArrayList<>();
        inferredSteps.addAll(steps);
    }

    public void setQualityCategory(S2RQualityCategory category){
        this.category = category;
    }

    public S2RQualityCategory getCategory() {
        return category;
    }

    public List<AppStep> getMatchedSteps() {
        return matchedSteps;
    }

    public List<AppStep> getInferredSteps() {
        return inferredSteps;
    }

    public void setCategory(S2RQualityCategory category) {
        this.category = category;
    }

    public void setMatchedSteps(List<AppStep> matchedSteps) {
        this.matchedSteps = matchedSteps;
    }

    public void setInferredSteps(List<AppStep> inferredSteps) {
        this.inferredSteps = inferredSteps;
    }
}

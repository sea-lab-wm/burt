package sealab.burt.qualitychecker;

import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphTransition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class S2RCheckerUtils {


    //function that finds the index of stepToFind in the transition list
    public static BiFunction<List<GraphTransition>, AppStep, Integer> indexOf = (transitions, stepToFind)
            -> IntStream.range(0, transitions.size())
            .filter(j -> {
                final AppStep step = transitions.get(j).getStep();
                return stepToFind.equals(step);
            })
            .findFirst()
            .orElse(-1);


    public static List<GraphTransition> filterAndSortTransitions(Set<GraphTransition> graphTransitions,
                                                           List<AppGuiComponent> enabledComponents) {

        if (enabledComponents == null) return new ArrayList<>();

        final List<ImmutablePair<GraphTransition, Integer>> indexedComponents = graphTransitions.stream()
                .map(t -> new ImmutablePair<>(t,
                        enabledComponents.indexOf(t.getStep().getComponent())))
                .filter(tp -> tp.right != -1)
                .collect(Collectors.toList());

        return indexedComponents.stream()
                .sorted(Comparator.comparing(ImmutablePair::getRight))
                .map(ImmutablePair::getLeft)
                .collect(Collectors.toList());
    }



    public static List<AppStep> removeCheckedSteps(List<AppStep> stepsToExecute,
                                                 List<AppGuiComponent> enabledComponents) {
        List<Integer> stepsToRemove = new ArrayList<>();

        //-------------------------------
        List<ImmutablePair<Integer, Integer>> stepGroups = getStepGroupsWithCheckComponent(stepsToExecute);

        if (stepGroups.isEmpty())
            return stepsToExecute;

        for (ImmutablePair<Integer, Integer> stepGroup : stepGroups) {
            Integer selected = getSelected(stepGroup, enabledComponents, stepsToExecute);
            if (selected != null) {
                IntStream.range(stepGroup.left, selected).forEach(stepsToRemove::add);
                IntStream.range(selected + 1, stepGroup.right).forEach(stepsToRemove::add);
            } else {
                IntStream.range(stepGroup.left + 1, stepGroup.right).forEach(stepsToRemove::add);
            }
        }

        //-------------------------------

        return IntStream.range(0, stepsToExecute.size())
                .filter(i -> !stepsToRemove.contains(i))
                .mapToObj(stepsToExecute::get)
                .collect(Collectors.toList());
    }

    private static Integer getSelected(ImmutablePair<Integer, Integer> stepGroup,
                                List<AppGuiComponent> enabledComponents,
                                List<AppStep> stepsToExecute) {

        final List<AppStep> appSteps = stepsToExecute.subList(stepGroup.left, stepGroup.right);

        final List<AppGuiComponent> checkedComponents = enabledComponents.stream()
                .filter(AppGuiComponent::getChecked)
                .collect(Collectors.toList());

        final int idx = IntStream.range(0, appSteps.size())
                .filter(j -> {
                    final AppGuiComponent component = appSteps.get(j).getComponent();
                    return checkedComponents.contains(component);
                })
                .findFirst()
                .orElse(-1);

        if (idx == -1)
            return null;

        return idx + stepGroup.left;
    }



    private static List<ImmutablePair<Integer, Integer>> getStepGroupsWithCheckComponent(List<AppStep> stepsToExecute) {
        List<ImmutablePair<Integer, Integer>> stepGroups = new ArrayList<>();

        int ini = -1;
        int end;
        for (int i = 0; i < stepsToExecute.size(); i++) {
            final AppStep appStep = stepsToExecute.get(i);
            final AppGuiComponent component = appStep.getComponent();

            if (NLActionS2RMatcher.isCheckedComponent(component.getType())) {
                if (ini == -1)
                    ini = i;
            } else {
                if (ini != -1) {
                    end = i;
                    stepGroups.add(new ImmutablePair<>(ini, end));
                    ini = -1;
                }
            }
        }

        if (ini != -1) {
            end = stepsToExecute.size();
            stepGroups.add(new ImmutablePair<>(ini, end));
        }

        return stepGroups;
    }

}

package sealab.burt.qualitychecker.graph;

import org.jgrapht.traverse.DepthFirstIterator;

import java.io.Serializable;
import java.util.*;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class AppGraphInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<AppStep> steps = new ArrayList<>();
    private AppGraph<GraphState, GraphTransition> graph;

    private Map<Integer, GraphState> stateIndex = new HashMap<>();
    private Appl appl;

    public Map<Integer, GraphState> getStateIndex() {
        return stateIndex;
    }

    public void setStateIndex(Map<Integer, GraphState> stateIndex) {
        this.stateIndex = stateIndex;
    }

    /**
     * @return the steps
     */
    public List<AppStep> getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(List<AppStep> steps) {
        this.steps = steps;
    }

    public void addSteps(List<AppStep> newSteps) {
        if (this.steps == null)
            this.steps = new ArrayList<>(newSteps);
        else
            this.steps.addAll(newSteps);
    }

    public AppGraph<GraphState, GraphTransition> getGraph() {
        return graph;
    }

    public void setGraph(AppGraph<GraphState, GraphTransition> graph) {
        this.graph = graph;
    }

    public void printGraph() {
        System.out.print(graphToString());
    }

    public String graphToString() {
        StringBuilder statesBuilder = new StringBuilder();

        statesBuilder.append("States (")
                .append(graph.vertexSet().size())
                .append("):");
        statesBuilder.append("\n");

        //---------------------------------------------

        StringBuilder transitionsBuilder = new StringBuilder();

        transitionsBuilder.append("Transitions (")
                .append(graph.edgeSet().size())
                .append("):");
        transitionsBuilder.append("\n");

        int numTransitions = 0;

        DepthFirstIterator<GraphState, GraphTransition> iterator = new DepthFirstIterator<>(graph, GraphState
                .START_STATE);
        while (iterator.hasNext()) {
            final GraphState state = iterator.next();

            statesBuilder.append(String.format("%s, %s, (%s): %s", state.getName(), state.getDataSource(),
                    state.getScreen(),
                    state.getUnformattedXml()));
            statesBuilder.append("\n");

            final List<GraphTransition> transitions = new LinkedList<>(graph.outgoingEdgesOf(state));
            transitions.sort((t1, t2) -> {

                AppStep s1 = t1.getStep();
                AppStep s2 = t2.getStep();
                int out = s1.getExecution().compareTo(s2.getExecution());
                if (out != 0) {
                    return out;
                } else {
                    return s1.getSequence().compareTo(s2.getSequence());
                }

            });

            numTransitions += transitions.size();

            transitions.forEach(t -> {
                transitionsBuilder.append(t.getId()).append(": ")
                        .append(t.getName())
                        .append(" weight=")
                        .append(t.getWeight())
                        .append(" ds=")
                        .append(t.getDataSource());
                transitionsBuilder.append("\n");
            });

            transitionsBuilder.append("\n");
        }

        statesBuilder.append("\n");

        StringBuilder checkBuilder = new StringBuilder();
        checkBuilder.append(String.format("Transitions correctness: %s",
                (numTransitions == graph.edgeSet().size()))
        );
        checkBuilder.append("\n");
        checkBuilder.append("\n");

/*        transitions.sort((t1, t2) -> {
            AppStep s1 = t1.getStep();
            AppStep s2 = t2.getStep();

            int out = s1.getExecution().compareTo(s2.getExecution());
            if (out != 0) {
                return out;
            } else {
                return s1.getSequence().compareTo(s2.getSequence());
            }

        });*/


        return checkBuilder.toString() + transitionsBuilder.toString() + statesBuilder.toString();
    }

    public boolean isGraphEmpty() {
        return graph.vertexSet().isEmpty();
    }

    public Set<GraphTransition> getTransitions() {
        return graph.edgeSet();
    }

    public Set<GraphState> getStates() {
        return graph.vertexSet();
    }

    public Appl getApp() {
        return appl;
    }

    public void setApp(Appl appl) {
        this.appl = appl;
    }

    public GraphState getState(Integer stateId) {
        return this.getStateIndex().get(stateId);
    }
}

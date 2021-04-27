package sealab.burt.qualitychecker.actionparser;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.DepthFirstIterator;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;


import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphUtils {


    /**
     * This method returns K paths from a startState to an endState given a
     * graph containing app's information
     *
     * @param graph
     * @param startState
     * @param endState
     * @param considerLoops
     * @param numPaths
     * @return
     */
    public static List<GraphPath<GraphState, GraphTransition>> findPaths(
            DirectedGraph<GraphState, GraphTransition> graph, GraphState startState, GraphState endState,
			boolean considerLoops, int numPaths) {

		List<GraphPath<GraphState, GraphTransition>> paths = new ArrayList<>();
		if (startState == null || endState == null) {
			return paths;
		}

		if (startState.equals(endState)) {

			if (!considerLoops) {
				return paths;
			}

			// return only one path with the loops sorted

			List<GraphTransition> transitions = new ArrayList<>(graph.getAllEdges(startState, startState));
			sortTransitions(transitions);

			paths.add(new GraphWalk<>(graph, startState, endState, null, transitions,
					transitions.size()));

			return paths;
		}

		// -----------------------------------------

		// find the k-shortest paths
		KShortestPaths<GraphState, GraphTransition> pathInspector = new KShortestPaths<>(
				graph, numPaths, Integer.MAX_VALUE);
		paths = pathInspector.getPaths(startState, endState);

		if (!considerLoops) {
			return paths;
		}

		// add the loops
		List<GraphPath<GraphState, GraphTransition>> newPaths = new ArrayList<>();
		for (GraphPath<GraphState, GraphTransition> path : paths) {
			List<GraphTransition> edgeList = path.getEdgeList();

			List<GraphTransition> newEdgeList = new ArrayList<>(edgeList);
			for (int i = 0; i < edgeList.size(); i++) {
				GraphTransition transition = edgeList.get(i);

				GraphState sourceState = transition.getSourceState();

				Set<GraphTransition> edgesSource = graph.getAllEdges(sourceState, sourceState);
				newEdgeList.addAll(edgesSource);

				if (i == edgeList.size() - 1) {

					GraphState targetState = transition.getTargetState();
					Set<GraphTransition> edgesTarget = graph.getAllEdges(targetState, targetState);
					newEdgeList.addAll(edgesTarget);
				}

			}

			sortTransitions(newEdgeList);

			newPaths.add(new GraphWalk<>(graph, startState, endState, null, newEdgeList,
					path.getWeight()));
		}

		return newPaths;
	}

    /**
     * Sort transitions by execution - sequence
     *
     * @param newEdgeList
     */
	private static void sortTransitions(List<GraphTransition> newEdgeList) {
		// sort by execution - sequence
		newEdgeList.sort((t1, t2) -> {
			AppStep s1 = t1.getStep();
			AppStep s2 = t2.getStep();

			int out = s1.getExecution().compareTo(s2.getExecution());
			if (out != 0) {
				return out;
			} else {
				return s1.getSequence().compareTo(s2.getSequence());
			}

		});
	}

	/**
	 * Return GraphStates with no parents given a graph
	 *
	 * @param graph
	 * @return
	 */
	public static List<GraphState> findRoots(DirectedGraph<GraphState, GraphTransition> graph) {
		DepthFirstIterator<GraphState, GraphTransition> it = new DepthFirstIterator<>(graph);

		List<GraphState> roots = new ArrayList<>();
		while (it.hasNext()) {
			GraphState vertex = it.next();

			Set<GraphTransition> in = graph.incomingEdgesOf(vertex);
			if (in.isEmpty()) {
				roots.add(vertex);
			}

		}
		return roots;
	}

	public static List<GraphState> findLeaves(DirectedGraph<GraphState, GraphTransition> graph) {
		DepthFirstIterator<GraphState, GraphTransition> it = new DepthFirstIterator<>(graph);

		List<GraphState> leaves = new ArrayList<>();
		while (it.hasNext()) {
			GraphState vertex = it.next();

			Set<GraphTransition> out = graph.outgoingEdgesOf(vertex);
			if (out.isEmpty()) {
				leaves.add(vertex);
			}
		}
		return leaves;
	}

	public static void visualizeGraph(DirectedGraph<GraphState, GraphTransition> graph2) {

		JGraphXAdapter<GraphState, GraphTransition> graph = getVisualGraph(graph2, GraphLayout.CIRCLE);

		mxGraphComponent graphComponent = new mxGraphComponent(graph);

		JFrame frame = new JFrame();
		frame.add(graphComponent);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public static JGraphXAdapter<GraphState, GraphTransition> getVisualGraph(
			DirectedGraph<GraphState, GraphTransition> appGraph, GraphLayout layoutType) {
		JGraphXAdapter<GraphState, GraphTransition> graph = new JGraphXAdapter<>(appGraph);
		graph.setAllowLoops(true);
		// mxStylesheet style = graph.getStylesheet();
		// frame.setSize(new Dimension( (int)bounds.getWidth(),(int)
		// bounds.getHeight()));

		// // define layout
		// mxIGraphLayout layout = new mxCompactTreeLayout(graph);
		// mxIGraphLayout layout = new mxCircleLayout(graph);
		// mxIGraphLayout layout = new mxFastOrganicLayout(graph);
		// mxIGraphLayout layout = new mxOrganicLayout(graph);
		// mxIGraphLayout layout = new mxOrthogonalLayout(graph);
		// mxIGraphLayout layout = new mxPartitionLayout(graph);
		// mxIGraphLayout layout = new mxStackLayout(graph);

		// mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		// layout.setOrientation(SwingConstants.NORTH);
		// layout.setDisableEdgeStyle(false);
		// layout.setIntraCellSpacing(200);
		// layout.setParallelEdgeSpacing(100);
		// // graphComponent.zoomTo(2, true);

		mxIGraphLayout layout;
		switch (layoutType) {
		case CIRCLE:
			layout = new mxCircleLayout(graph);
			break;
		case HIERARCHY:
			layout = new mxHierarchicalLayout(graph);
			break;
		case COMPACT_TREE:
            layout = new mxCompactTreeLayout(graph);
            break;
		default:
			throw new RuntimeException("Layout type not supported: " + layoutType);
		}

		// graphComponent.zoomTo(1.5, true);
		layout.execute(graph.getDefaultParent());

		mxParallelEdgeLayout parallelEdgeLayout = new mxParallelEdgeLayout(graph, 40);
		parallelEdgeLayout.execute(graph.getDefaultParent());

		mxEdgeLabelLayout layout2 = new mxEdgeLabelLayout(graph);
		layout2.execute(graph.getDefaultParent());
		return graph;
	}



}

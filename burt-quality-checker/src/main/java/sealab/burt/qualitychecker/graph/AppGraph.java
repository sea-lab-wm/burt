package sealab.burt.qualitychecker.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.builder.DirectedGraphBuilder;
import org.jgrapht.graph.builder.DirectedGraphBuilderBase;

/**
 * A directed graph. A default directed graph is a non-simple directed graph in
 * which multiple edges between any two vertices are permitted. Loops are also
 * permitted.
 *
 * 
 * @param <V>
 *            the graph vertex type
 * @param <E>
 *            the graph edge type
 * 
 */
public class AppGraph<V, E> extends AbstractBaseGraph<V, E> implements DirectedGraph<V, E> {
	private static final long serialVersionUID = 3544953246956466230L;

	/**
	 * Creates a new directed graph.
	 *
	 * @param edgeClass
	 *            class on which to base factory for edges
	 */
	public AppGraph(Class<? extends E> edgeClass) {
		this(new ClassBasedEdgeFactory<>(edgeClass));
	}

	/**
	 * Creates a new directed graph with the specified edge factory.
	 *
	 * @param ef
	 *            the edge factory of the new graph.
	 */
	public AppGraph(EdgeFactory<V, E> ef) {
		super(ef, true, true);
	}

	/**
	 * Create a builder for this kind of graph.
	 * 
	 * @param edgeClass
	 *            class on which to base factory for edges
	 * @param <V>
	 *            the graph vertex type
	 * @param <E>
	 *            the graph edge type
	 * @return a builder for this kind of graph
	 */
	public static <V, E> DirectedGraphBuilderBase<V, E, ? extends DefaultDirectedGraph<V, E>, ?> builder(
			Class<? extends E> edgeClass) {
		return new DirectedGraphBuilder<>(new DefaultDirectedGraph<>(edgeClass));
	}

	/**
	 * Create a builder for this kind of graph.
	 * 
	 * @param ef
	 *            the edge factory of the new graph
	 * @param <V>
	 *            the graph vertex type
	 * @param <E>
	 *            the graph edge type
	 * @return a builder for this kind of graph
	 */
	public static <V, E> DirectedGraphBuilderBase<V, E, ? extends DefaultDirectedGraph<V, E>, ?> builder(
			EdgeFactory<V, E> ef) {
		return new DirectedGraphBuilder<>(new DefaultDirectedGraph<>(ef));
	}
}


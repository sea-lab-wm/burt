/*******************************************************************************
 * Copyright (c) 2016, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
/**
 * FindAllPaths.java
 * 
 * Created on Aug 9, 2014, 7:59:17 PM
 */
package edu.semeru.android.testing.helpers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Given a connected directed graph, find all paths between any two input
 * points.
 * 
 * Based on
 * http://codereview.stackexchange.com/questions/45678/find-all-paths-from
 * -source-to-destination
 *
 * @author Carlos Bernal
 * @since Aug 9, 2014
 */
public class FindPath<T> {

    private final GraphFindPath<T> graph;

    /**
     * Takes in a graph. This graph should not be changed by the client
     */
    public FindPath(GraphFindPath<T> graph) {
	if (graph == null) {
	    throw new NullPointerException("The input graph cannot be null.");
	}
	this.graph = graph;
    }

    private void validate(T source, T destination) {

	if (source == null) {
	    throw new NullPointerException("The source: " + source + " cannot be  null.");
	}
	if (destination == null) {
	    throw new NullPointerException("The destination: " + destination + " cannot be  null.");
	}
	if (source.equals(destination)) {
	    throw new IllegalArgumentException("The source and destination: " + source + " cannot be the same.");
	}
    }

    /**
     * Returns the list of paths, where path itself is a list of nodes.
     * 
     * @param source
     *            the source node
     * @param destination
     *            the destination node
     * @return List of all paths
     */
    public List<T> getAllPaths(T source, T destination) {
	validate(source, destination);

	return recursive(source, destination, new LinkedHashSet<T>());
    }

    // so far this dude ignore's cycles.
    private List<T> recursive(T current, T destination, LinkedHashSet<T> path) {
	path.add(current);

	if (current.equals(destination)) {
//	    path.remove(current);
	    return new ArrayList<T>(path);
	}

	final Set<T> edges = graph.edgesFrom(current).keySet();
	List<T> recursive = new ArrayList<T>();
	for (T t : edges) {
		System.out.println(t);
	    if (!path.contains(t)) {
		recursive = recursive(t, destination, path);
		if (recursive != null) {
		    return recursive;
		}
	    }
	}
	path.remove(current);
	return null;
    }

    public static void main(String[] args) {
	GraphFindPath<String> graphFindAllPaths = new GraphFindPath<String>();
	graphFindAllPaths.addNode("1");
	graphFindAllPaths.addNode("2");
	graphFindAllPaths.addNode("3");
	graphFindAllPaths.addNode("4");
	graphFindAllPaths.addNode("5");

	graphFindAllPaths.addEdge("3", "1", 10);
	graphFindAllPaths.addEdge("2", "3", 10);
	graphFindAllPaths.addEdge("2", "4", 10);
	graphFindAllPaths.addEdge("4", "5", 10);
	graphFindAllPaths.addEdge("5", "4", 10);
	graphFindAllPaths.addEdge("1", "2", 10);

	FindPath<String> findAllPaths = new FindPath<String>(graphFindAllPaths);

	List<String> path = findAllPaths.getAllPaths("1", "5");
	System.out.println(path);

	// assertEquals(paths, findAllPaths.getAllPaths("A", "D"));
    }
}

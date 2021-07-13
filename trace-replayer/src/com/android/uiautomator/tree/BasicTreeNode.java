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

package com.android.uiautomator.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicTreeNode {

    private static final BasicTreeNode[] CHILDREN_TEMPLATE = new BasicTreeNode[] {};

    protected BasicTreeNode mParent;

    protected final List<BasicTreeNode> mChildren = new ArrayList<BasicTreeNode>();
    
	private ArrayList<BasicTreeNode> leafNodes = new  ArrayList<BasicTreeNode>();

    public int x, y, width, height;

    // whether the boundary fields are applicable for the node or not
    // RootWindowNode has no bounds, but UiNodes should
    protected boolean mHasBounds = false;

    public void addChild(BasicTreeNode child) {
	if (child == null) {
	    throw new NullPointerException("Cannot add null child");
	}
	if (mChildren.contains(child)) {
	    throw new IllegalArgumentException("node already a child");
	}
	mChildren.add(child);
	child.mParent = this;
    }

    public List<BasicTreeNode> getChildrenList() {
	return Collections.unmodifiableList(mChildren);
    }

    public BasicTreeNode[] getChildren() {
	return mChildren.toArray(CHILDREN_TEMPLATE);
    }

    public BasicTreeNode getParent() {
	return mParent;
    }

    public boolean hasChild() {
	return mChildren.size() != 0;
    }

    public int getChildCount() {
	return mChildren.size();
    }

    public void clearAllChildren() {
	for (BasicTreeNode child : mChildren) {
	    child.clearAllChildren();
	}
	mChildren.clear();
    }

    /**
     *
     * Find nodes in the tree containing the coordinate
     *
     * The found node should have bounds covering the coordinate, and none of
     * its children's bounds covers it. Depending on the layout, some app may
     * have multiple nodes matching it, the caller must provide a
     * {@link IFindNodeListener} to receive all found nodes
     *
     * @param px
     * @param py
     * @return
     */
    public boolean findLeafMostNodesAtPoint(int px, int py,
	    IFindNodeListener listener) {
	boolean foundInChild = false;
	for (BasicTreeNode node : mChildren) {
	    foundInChild |= node.findLeafMostNodesAtPoint(px, py, listener);
	}
	// checked all children, if at least one child covers the point, return
	// directly
	if (foundInChild)
	    return true;
	// check self if the node has no children, or no child nodes covers the
	// point
	if (mHasBounds) {
	    if (x <= px && px <= x + width && y <= py && py <= y + height) {
		listener.onFoundNode(this);
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    public ArrayList<BasicTreeNode> getLeafNodes(){
		leafNodes.clear();
		for(BasicTreeNode node : this.mChildren){
			getLeafNodesHelper(node);
		}
		return leafNodes;
	}
    
    /**
	 * A helper function to get leaf nodes
	 * @param node
	 */
	private void getLeafNodesHelper(BasicTreeNode node){
		if(node.mChildren.size() == 0){
			leafNodes.add(node);
		}
		for(BasicTreeNode child : node.mChildren){
			getLeafNodesHelper(child);
		}
	}
    
    public Object[] getAttributesArray() {
	return null;
    };

    public static interface IFindNodeListener {
	void onFoundNode(BasicTreeNode node);
    }
}

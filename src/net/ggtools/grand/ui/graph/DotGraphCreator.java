// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2004, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ggtools.grand.ui.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ggtools.grand.ant.AntLink;
import net.ggtools.grand.ant.AntTargetNode;
import net.ggtools.grand.ant.AntTaskLink;
import net.ggtools.grand.ant.SubantTaskLink;
import net.ggtools.grand.graph.Graph;
import net.ggtools.grand.graph.Link;
import net.ggtools.grand.graph.Node;
import net.ggtools.grand.graph.visit.LinkVisitor;
import net.ggtools.grand.graph.visit.NodeVisitor;
import net.ggtools.grand.ui.Application;
import net.ggtools.grand.ui.GrandUiPrefStore;
import net.ggtools.grand.ui.prefs.PreferenceKeys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import sf.jzgraph.IDotGraph;
import sf.jzgraph.IEdge;
import sf.jzgraph.IGraph;
import sf.jzgraph.IVertex;
import sf.jzgraph.dot.impl.DotGraph;

/**
 * Factory class creating a JzGraph graph for a Grand graph using Grand's
 * visitor API.
 * 
 * @author Christophe Labouisse
 */
public class DotGraphCreator implements NodeVisitor, LinkVisitor, DotGraphAttributes {
    private static final Log log = LogFactory.getLog(GraphControler.class);

    private String currentLinkName;

    private final IDotGraph dotGraph;

    private Graph graph;

    private final Map nameDimensions;

    private final Node startNode;

    private final boolean useBusRouting;

    private final Map vertexLUT;

    /**
     *  
     */
    public DotGraphCreator(final Graph graph, final boolean useBusRouting) {
        this.graph = graph;
        this.useBusRouting = useBusRouting;
        nameDimensions = new HashMap();
        dotGraph = new DotGraph(IGraph.GRAPH, graph.getName());
        vertexLUT = new HashMap();
        startNode = graph.getStartNode();
    }

    public IDotGraph getGraph() {
        if (startNode != null) {
            startNode.accept(this);
        }

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            final Node node = (Node) iter.next();
            if ("".equals(node.getName()) || (node == startNode)) {
                continue;
            }
            node.accept(this);
        }

        // Update width and height in nodes.
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                final Font systemFont = Application.getInstance().getFont(Application.NODE_FONT);
                for (Iterator iter = nameDimensions.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry) iter.next();
                    final String name = (String) entry.getKey();
                    final IVertex vertex = (IVertex) entry.getValue();

                    final Dimension dim = FigureUtilities.getTextExtents(name, systemFont);
                    vertex.setAttr(MINWIDTH_ATTR, Math.max(dim.width, 50));
                    vertex.setAttr(MINHEIGHT_ATTR, Math.max(dim.height, 25));
                }
            }
        });

        for (Iterator iter = graph.getNodes(); iter.hasNext();) {
            Node node = (Node) iter.next();
            final Collection deps = node.getLinks();
            int index = 1;
            final int numDeps = deps.size();
            for (Iterator iterator = deps.iterator(); iterator.hasNext();) {
                final Link link = (Link) iterator.next();
                currentLinkName = "";
                if (numDeps > 1) {
                    currentLinkName += index++;
                }
                link.accept(this);
            }
        }

        return dotGraph;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntLink)
     */
    public void visitLink(AntLink link) {
        addLink(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.AntTaskLink)
     */
    public void visitLink(AntTaskLink link) {
        final IEdge edge = addLink(link);
        edge.setAttr(LINK_TASK_ATTR, link.getTaskName());
        edge.setAttr(LINK_PARAMETERS_ATTR, link.getParameterMap());
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.graph.Link)
     */
    public void visitLink(Link link) {
        addLink(link);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.LinkVisitor#visitLink(net.ggtools.grand.ant.SubantTaskLink)
     */
    public void visitLink(SubantTaskLink link) {
        final IEdge edge = addLink(link);
        edge.setAttr(LINK_TASK_ATTR, link.getTaskName());
        edge.setAttr(LINK_PARAMETERS_ATTR, link.getParameterMap());
        edge.setAttr(LINK_SUBANT_DIRECTORIES, link.getDirectories());
        final GrandUiPrefStore preferenceStore = Application.getInstance().getPreferenceStore();
        edge.setAttr(DRAW2DFGCOLOR_ATTR, preferenceStore
                .getColor(PreferenceKeys.LINK_SUBANT_COLOR));
        edge.setAttr(DRAW2DLINEWIDTH_ATTR, preferenceStore
                .getInt(PreferenceKeys.LINK_SUBANT_LINEWIDTH));

    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.ant.AntTargetNode)
     */
    public void visitNode(AntTargetNode node) {
        IVertex vertex = addNode(node);
        final String ifCondition = node.getIfCondition();
        if (ifCondition != null) {
            vertex.setAttr(IF_CONDITION_ATTR, ifCondition);
        }

        final String unlessCondition = node.getUnlessCondition();
        if (unlessCondition != null) {
            vertex.setAttr(UNLESS_CONDITION_ATTR, unlessCondition);
        }

        final String buildFile = node.getBuildFile();
        if (buildFile != null) {
            vertex.setAttr(BUILD_FILE_ATTR, buildFile);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.graph.visit.NodeVisitor#visitNode(net.ggtools.grand.graph.Node)
     */
    public void visitNode(Node node) {
        addNode(node);
    }

    /**
     * Creates a basic link.
     * @param link
     * @param name
     * @return
     */
    private IEdge addLink(final Link link) {
        final IEdge edge = dotGraph.newEdge((IVertex) vertexLUT.get(link.getStartNode().getName()),
                (IVertex) vertexLUT.get(link.getEndNode().getName()), currentLinkName, link);
        final GrandUiPrefStore preferenceStore = Application.getInstance().getPreferenceStore();
        if (link.hasAttributes(Link.ATTR_WEAK_LINK)) {
            edge.setAttr(DRAW2DFGCOLOR_ATTR, preferenceStore
                    .getColor(PreferenceKeys.LINK_WEAK_COLOR));
            edge.setAttr(DRAW2DLINEWIDTH_ATTR, preferenceStore
                    .getInt(PreferenceKeys.LINK_WEAK_LINEWIDTH));
        }
        else {
            edge.setAttr(DRAW2DFGCOLOR_ATTR, preferenceStore
                    .getColor(PreferenceKeys.LINK_DEFAULT_COLOR));
            edge.setAttr(DRAW2DLINEWIDTH_ATTR, preferenceStore
                    .getInt(PreferenceKeys.LINK_DEFAULT_LINEWIDTH));
        }
        return edge;
    }

    /**
     * Creates a <em>basic node</em>
     * @param node
     * @return
     */
    private final IVertex addNode(final Node node) {
        final String name = node.getName();
        final IVertex vertex = dotGraph.newVertex(name, node);

        if (node.equals(startNode)) {
            setVertexPreferences(vertex, "start");
        }
        else if (node.hasAttributes(Node.ATTR_MAIN_NODE)) {
            setVertexPreferences(vertex, "main");
        }
        else if (node.hasAttributes(Node.ATTR_MISSING_NODE)) {
            setVertexPreferences(vertex, "missing");
        }

        else {
            setVertexPreferences(vertex, "default");
        }

        if (node.getDescription() != null) {
            vertex.setAttr(DESCRIPTION_ATTR, node.getDescription());
        }

        if (useBusRouting) {
            final GrandUiPrefStore preferenceStore = Application.getInstance().getPreferenceStore();
            vertex.setAttr("inthreshold", preferenceStore
                    .getInt(PreferenceKeys.GRAPH_BUS_IN_THRESHOLD));
            vertex.setAttr("outthreshold", preferenceStore
                    .getInt(PreferenceKeys.GRAPH_BUS_OUT_THRESHOLD));
        }

        vertexLUT.put(name, vertex);
        nameDimensions.put(name, vertex);
        return vertex;
    }

    /**
     * @param vertex
     * @param preferenceStore
     */
    private void setVertexPreferences(final IVertex vertex, final String nodeType) {
        final GrandUiPrefStore preferenceStore = Application.getInstance().getPreferenceStore();
        final String keyPrefix = PreferenceKeys.NODE_PREFIX + nodeType;
        vertex.setAttr(SHAPE_ATTR, preferenceStore.getString(keyPrefix + ".shape"));
        vertex.setAttr(DRAW2DFGCOLOR_ATTR, preferenceStore.getColor(keyPrefix + ".fgcolor"));
        vertex.setAttr(DRAW2DFILLCOLOR_ATTR, preferenceStore.getColor(keyPrefix + ".fillcolor"));
        vertex.setAttr(DRAW2DLINEWIDTH_ATTR, preferenceStore.getInt(keyPrefix + ".linewidth"));
    }

}

//$Id$
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
package net.ggtools.grand.ui.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ggtools.grand.filters.GraphFilter;
import net.ggtools.grand.filters.NodeRemoverFilter;
import net.ggtools.grand.ui.graph.GraphControlerProvider;
import net.ggtools.grand.ui.graph.draw2d.Draw2dNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Christophe Labouisse
 */
public class FilterSelectedNodesAction extends GraphListenerAction {
    private static final String DEFAULT_ACTION_NAME = "Filter out selected nodes";

    private static final Log log = LogFactory.getLog(FilterSelectedNodesAction.class);


    /**
     * @param parent
     */
    public FilterSelectedNodesAction(final GraphControlerProvider parent) {
        super(parent,DEFAULT_ACTION_NAME);
        
        boolean isEnabled = false;

        if (getGraphControler() != null) {
            final Collection<Draw2dNode> selectedNodes = getGraphControler().getSelection();
            isEnabled = !selectedNodes.isEmpty();
        }
        setEnabled(isEnabled);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("run() - start");
        }

        final Collection<Draw2dNode> selection = getGraphControler().getSelection();
        final List<String> nodeList = new LinkedList<String>();
        for (final Iterator<Draw2dNode> iter = selection.iterator(); iter.hasNext();) {
            final Draw2dNode node = iter.next();
            nodeList.add(node.getName());
        }
        final GraphFilter filter = new NodeRemoverFilter(nodeList);
        getGraphControler().addFilter(filter);

        if (log.isDebugEnabled()) {
            log.debug("run() - end");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.graph.GraphListener#selectionChanged(java.util.Collection)
     */
    @Override
    public void selectionChanged(final Collection selectedNodes) {
        setEnabled(!selectedNodes.isEmpty());
    }


}

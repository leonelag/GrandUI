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
package net.ggtools.grand.ui.actions;

import java.util.Collection;

import net.ggtools.grand.ui.graph.GraphControler;
import net.ggtools.grand.ui.graph.GraphControlerProvider;
import net.ggtools.grand.ui.graph.GraphListener;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An abstract class implementing basic features for actions listening to a graph.
 * This class manages the subscription/unsubscription process whenever the controler
 * gets available/unavailable and implements do nothing methods on events.
 * 
 * @author Christophe Labouisse
 */
public abstract class GraphListenerAction extends GraphControlerAction implements
        GraphListener {
    /**
     * @param parent
     */
    public GraphListenerAction(final GraphControlerProvider parent) {
        super(parent);
    }

    /**
     * @param parent
     * @param text
     */
    public GraphListenerAction(final GraphControlerProvider parent, final String text) {
        super(parent, text);
    }

    /**
     * @param parent
     * @param text
     * @param image
     */
    public GraphListenerAction(final GraphControlerProvider parent, final String text, final ImageDescriptor image) {
        super(parent, text, image);
    }

    /**
     * @param parent
     * @param text
     * @param style
     */
    public GraphListenerAction(final GraphControlerProvider parent, final String text, final int style) {
        super(parent, text, style);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.graph.GraphListener#parameterChanges(net.ggtools.grand.ui.graph.GraphControler)
     */
    public void parameterChanged(final GraphControler controler) {
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.graph.GraphListener#selectionChanged(java.util.Collection)
     */
    public void selectionChanged(final Collection selectedNodes) {}

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.actions.GraphControlerAction#postAddHook()
     */
    @Override
    protected void postAddHook() {
        getGraphControler().addListener(this);
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.actions.GraphControlerAction#postInitHook()
     */
    @Override
    protected void postInitHook() {
        if (getGraphControler() != null) {
            getGraphControler().addListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.actions.GraphControlerAction#preRemoveHook()
     */
    @Override
    protected void preRemoveHook() {
        getGraphControler().removeSelectionListener(this);
        setEnabled(false);
    }
}

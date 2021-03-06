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
package net.ggtools.grand.ui.graph.draw2d;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.ggtools.grand.ui.Application;
import net.ggtools.grand.ui.graph.DotGraphAttributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.BlockFlow;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Font;

import sf.jzgraph.IEdge;

/**
 * A figure for node's tooltips.
 * 
 * @author Christophe Labouisse
 */
public class LinkTooltip extends AbstractGraphTooltip implements DotGraphAttributes {
    private static final String ELLIPSIS = "...";

    private static final Log log = LogFactory.getLog(LinkTooltip.class);

    private final IEdge edge;

    /**
     * Creates a new tooltip from a Jzgraph node.
     * @param vertex
     */
    public LinkTooltip(final IEdge edge) {
        super();
        this.edge = edge;
        createContents();
    }

    @Override
    protected void createContents() {
        if (log.isDebugEnabled()) {
            log.debug("createContents() - start");
        }

        final Label type;
        if (edge.hasAttr(LINK_TASK_ATTR)) {
            type = new Label(edge.getAttrAsString(LINK_TASK_ATTR), Application.getInstance()
                    .getImage(Application.LINK_ICON));
        }
        else {
            type = new Label("depency", Application.getInstance().getImage(Application.LINK_ICON));
        }
        type.setFont(Application.getInstance().getBoldFont(Application.TOOLTIP_FONT));
        add(type);

        final Font italicMonospaceFont = Application.getInstance().getItalicFont(
                Application.TOOLTIP_MONOSPACE_FONT);
        final Font monospaceFont = Application.getInstance().getFont(
                Application.TOOLTIP_MONOSPACE_FONT);

        final FlowPage page = createFlowPage();
        BlockFlow blockFlow = new BlockFlow();
        TextFlow textFlow = new TextFlow("From: ");
        blockFlow.add(textFlow);
        InlineFlow inline = new InlineFlow();
        textFlow = new TextFlow(edge.getTail().getName());
        textFlow.setFont(italicMonospaceFont);
        inline.add(textFlow);
        blockFlow.add(inline);
        blockFlow.setBorder(new SectionBorder());
        page.add(blockFlow);

        blockFlow = new BlockFlow();
        textFlow = new TextFlow("To: ");
        blockFlow.add(textFlow);
        inline = new InlineFlow();
        textFlow = new TextFlow(edge.getHead().getName());
        textFlow.setFont(italicMonospaceFont);
        inline.add(textFlow);
        blockFlow.add(inline);
        page.add(blockFlow);

        if (!"".equals(edge.getName())) {
            blockFlow = new BlockFlow();
            textFlow = new TextFlow("Link #" + edge.getName());
            blockFlow.add(textFlow);
            page.add(blockFlow);
        }

        if (edge.hasAttr(LINK_PARAMETERS_ATTR)) {
            final Map parameters = (Map) edge.getAttr(LINK_PARAMETERS_ATTR);
            if (!parameters.isEmpty()) {
                final BlockFlow outterBlock = new BlockFlow();
                for (final Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry) iter.next();
                    final BlockFlow innerBlock = new BlockFlow();
                    textFlow = new TextFlow(((String) entry.getKey()) + ": ");
                    textFlow.setFont(monospaceFont);
                    innerBlock.add(textFlow);
                    inline = new InlineFlow();
                    textFlow = new TextFlow((String) entry.getValue());
                    textFlow.setFont(italicMonospaceFont);
                    inline.add(textFlow);
                    innerBlock.add(inline);
                    outterBlock.add(innerBlock);
                }
                outterBlock.setBorder(new SectionBorder());
                page.add(outterBlock);
            }
        }

        if (edge.hasAttr(LINK_SUBANT_DIRECTORIES)) {
            final Collection directories = (Collection) edge.getAttr(LINK_SUBANT_DIRECTORIES);
            if (!directories.isEmpty()) {
                final BlockFlow outterBlock = new BlockFlow();
                textFlow = new TextFlow("Generic ant file to be applied to:");
                outterBlock.add(textFlow);
                for (final Iterator iter = directories.iterator(); iter.hasNext();) {
                    String currentDirectory = (String) iter.next();

                    final Dimension dim = FigureUtilities.getTextExtents(currentDirectory, monospaceFont);
                    if (dim.width > TOOLTIP_WIDTH) {
                        if (log.isDebugEnabled()) {
                            log.debug("createContents() - Filename too long, truncating : dim = "
                                    + dim + ", currentDirectory = " + currentDirectory);
                        }

                        final int length = currentDirectory.length();
                        int index = length;
                        String part = "";
                        while (true) {
                            index = currentDirectory.lastIndexOf(File.separatorChar, index - 1);
                            final String tmp = currentDirectory.substring(index);
                            if (FigureUtilities.getTextExtents(ELLIPSIS + tmp, monospaceFont).width > TOOLTIP_WIDTH) {
                                break;
                            }
                            part = tmp;
                        }
                        currentDirectory = ELLIPSIS + part;

                        if (log.isDebugEnabled()) {
                            log.debug("createContents() - dir truncated to: currentDirectory = "
                                    + currentDirectory);
                        }
                    }

                    final BlockFlow innerBlock = new BlockFlow();
                    textFlow = new TextFlow(currentDirectory);
                    textFlow.setFont(monospaceFont);
                    innerBlock.add(textFlow);
                    outterBlock.add(innerBlock);
                }
                outterBlock.setBorder(new SectionBorder());
                page.add(outterBlock);

            }
        }

        if (log.isDebugEnabled()) {
            log.debug("createContents() - end");
        }
    }
}

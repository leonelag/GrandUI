// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
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

package net.ggtools.grand.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import net.ggtools.grand.ui.widgets.GraphWindow;
import net.ggtools.grand.ui.widgets.Splash;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Singleton holding the application data.
 * 
 * @author Christophe Labouisse
 */
public class Application {

    final public static String APPLICATION_ICON = "net.ggtools.grand.ui.appicon";

    final public static String GRAPH_FONT = "net.ggtools.grand.ui.graphfont";

    final public static String LINK_FONT = "net.ggtools.grand.ui.linkfont";

    final public static String LINK_ICON = "net.ggtools.grand.ui.linkicon";

    final public static String MONOSPACE_FONT = "net.ggtools.grand.ui.linkfont";

    final public static String NODE_FONT = "net.ggtools.grand.ui.nodefont";

    final public static String NODE_ICON = "net.ggtools.grand.ui.nodeicon";

    final public static String TOOLTIP_FONT = "net.ggtools.grand.ui.tooltipfont";

    final public static String TOOLTIP_MONOSPACE_FONT = "net.ggtools.grand.ui.tooltipmonospacefont";

    private static final Log log = LogFactory.getLog(Application.class);

    private static Application singleton;

    static public Application getInstance() {
        return singleton;
    }

    public static void main(String[] args) {
        try {
            log.info("Starting application");
            Thread.currentThread().setName("Display thread");
            final Application application = new Application();
            application.run();
        } catch (IOException e) {
            log.fatal("Cannot run application", e);
        }
        log.info("Exiting ...");
        System.exit(0);
    }

    final private Properties buildProperties;

    private FontRegistry fontRegistry;

    private ImageRegistry imageRegistry;

    final private String versionString;

    private Application() throws IOException {
        singleton = this;
        buildProperties = new Properties();
        buildProperties.load(getClass().getResourceAsStream("/buildnum.properties"));
        versionString = "v" + buildProperties.getProperty("build.version.string") + " (build "
                + buildProperties.getProperty("build.number") + " "
                + buildProperties.getProperty("build.date") + ")";

    }

    /**
     * @param symbolicName
     * @return
     */
    final public Font getBoldFont(final String symbolicName) {
        return fontRegistry.getBold(symbolicName);
    }

    /**
     * @param symbolicName
     * @return
     */
    final public Font getFont(final String symbolicName) {
        return fontRegistry.get(symbolicName);
    }

    /**
     * @param key
     * @return
     */
    final public Image getImage(final String key) {
        return imageRegistry.get(key);
    }

    /**
     * @param symbolicName
     * @return
     */
    final public Font getItalicFont(final String symbolicName) {
        return fontRegistry.getItalic(symbolicName);
    }

    /**
     * Initializes the application resources. This method must be called from an
     * active display thread.
     *  
     */
    final private void initResources() {
        if (log.isDebugEnabled()) log.debug("Initializing application resources");
        fontRegistry = new FontRegistry("net.ggtools.grand.ui.resource.fonts");
        for (Iterator iter = fontRegistry.getKeySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            fontRegistry.get(key);
        }
        imageRegistry = new ImageRegistry();

        imageRegistry.put(APPLICATION_ICON, ImageDescriptor.createFromFile(Application.class,
                "resource/application.png"));
        imageRegistry.put(LINK_ICON, ImageDescriptor.createFromFile(Application.class,
                "resource/link-icon.png"));
        imageRegistry.put(NODE_ICON, ImageDescriptor.createFromFile(Application.class,
                "resource/node-icon.png"));
    }

    /**
     * @throws IOException
     *  
     */
    final private void run() {
        final Display display = Display.getDefault();
        final Splash splash = new Splash(display, versionString);
        splash.open();
        initResources();
        ApplicationWindow mainWindow = new GraphWindow();
        mainWindow.setBlockOnOpen(true);
        splash.close();
        splash.dispose();
        mainWindow.open();
    }

    /**
     * @return Returns the buildProperties.
     */
    final Properties getBuildProperties() {
        return buildProperties;
    }

    /**
     * @return Returns the versionString.
     */
    final String getVersionString() {
        return versionString;
    }
}
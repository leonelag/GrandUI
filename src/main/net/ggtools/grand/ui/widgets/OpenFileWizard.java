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
package net.ggtools.grand.ui.widgets;

import java.io.File;

import org.eclipse.jface.wizard.Wizard;

/**
 * @author Christophe Labouisse
 */
public class OpenFileWizard extends Wizard {

    /**
     * @author Christophe Labouisse
     */
    interface SelectedFileProvider {
        void addListener(SelectedFileListener listener);
        
        void removeListener(SelectedFileListener listener);
    }
    
    interface SelectedFileListener {
        void fileSelected(File selectedFile);
    }

    private final GraphWindow window;

    private PropertySettingPage propertySettingPage;

    private FileSelectionPage fileSelectionPage;

    /**
     * @param window
     * 
     */
    public OpenFileWizard(final GraphWindow window) {
        super();
        this.window = window;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        fileSelectionPage = new FileSelectionPage();
        addPage(fileSelectionPage);
        propertySettingPage = new PropertySettingPage(fileSelectionPage);
        addPage(propertySettingPage);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean rc = false;
        final File selectedFile = fileSelectionPage.getSelectedFile();
        if (selectedFile != null) {
            window.openGraphInNewDisplayer(selectedFile, propertySettingPage.getProperties());
            rc = true;
        }

        return rc;
    }

    @Override
    public boolean canFinish() {
        return fileSelectionPage.getSelectedFile() != null;
    }
}

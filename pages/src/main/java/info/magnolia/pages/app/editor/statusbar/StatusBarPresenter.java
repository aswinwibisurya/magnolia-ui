/**
 * This file Copyright (c) 2014 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.pages.app.editor.statusbar;

import info.magnolia.pages.app.editor.PagesEditorSubAppDescriptor;
import info.magnolia.pages.app.editor.extension.ExtensionContainer;
import info.magnolia.pages.app.editor.extension.Extension;
import info.magnolia.pages.app.editor.extension.ExtensionFactory;
import info.magnolia.pages.app.editor.extension.definition.ExtensionDefinition;
import info.magnolia.pages.app.editor.statusbar.definition.StatusBarDefinition;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.app.SubAppDescriptor;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.workbench.StatusBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;

/**
 * A presenter class for the {@link StatusBarView} displayed at the bottom of the page editor. Takes care of loading and
 * displaying the components inside the status bar.
 */
public class StatusBarPresenter implements ExtensionContainer {

    private static final Logger log = LoggerFactory.getLogger(StatusBarPresenter.class);

    private StatusBarView view;
    private SubAppContext subAppContext;
    private final ExtensionFactory extensionFactory;

    private List<Extension> extensions = new ArrayList<Extension>();

    @Inject
    public StatusBarPresenter(final StatusBarView view, SubAppContext subAppContext, ExtensionFactory extensionFactory) {
        this.view = view;
        this.subAppContext = subAppContext;
        this.extensionFactory = extensionFactory;

        view.asVaadinComponent().setHeight(24, Sizeable.Unit.PIXELS);
    }

    @Override
    public StatusBarView start() {
        SubAppDescriptor subAppDescriptor = subAppContext.getSubAppDescriptor();

        if (subAppDescriptor instanceof PagesEditorSubAppDescriptor) {
            StatusBarDefinition definition = ((PagesEditorSubAppDescriptor) subAppDescriptor).getStatusBar();
            if (definition != null) {
                Map<String, ExtensionDefinition> extensionDefinitions = definition.getExtensions();
                extensions = extensionFactory.createExtensions(extensionDefinitions);
            }
            else {
                log.error("No statusBar definition defined for pages detail app, no extensions will be loaded.");
            }
        } else {
            log.error("Expected an instance of {} but got {}. No extensions will be loaded.", PagesEditorSubAppDescriptor.class.getSimpleName(), subAppDescriptor.getClass().getName());
        }
        for (Extension extension : extensions) {
            view.addComponent(extension.start().asVaadinComponent(), Alignment.MIDDLE_CENTER);
        }
        return view;
    }

    @Override
    public void onLocationUpdate(DetailLocation location) {
        for (Extension extension : extensions) {
            extension.onLocationUpdate(location);
        }
    }

    @Override
    public void deactivateExtensions() {
        for (Extension extension : extensions) {
            extension.deactivate();
        }
    }

}
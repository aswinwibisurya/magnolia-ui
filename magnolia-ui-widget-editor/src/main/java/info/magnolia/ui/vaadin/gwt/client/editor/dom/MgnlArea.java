/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.ui.vaadin.gwt.client.editor.dom;

import static info.magnolia.ui.vaadin.gwt.client.editor.jsni.JavascriptUtils.getI18nMessage;

import info.magnolia.cms.security.operations.OperationPermissionDefinition;
import info.magnolia.ui.vaadin.gwt.client.editor.event.EditComponentEvent;
import info.magnolia.ui.vaadin.gwt.client.editor.event.NewAreaEvent;
import info.magnolia.ui.vaadin.gwt.client.editor.event.NewComponentEvent;
import info.magnolia.ui.vaadin.gwt.client.shared.AbstractElement;
import info.magnolia.ui.vaadin.gwt.client.shared.AreaElement;
import info.magnolia.ui.vaadin.gwt.client.widget.controlbar.AreaEndBar;
import info.magnolia.ui.vaadin.gwt.client.widget.controlbar.listener.AreaListener;
import info.magnolia.ui.vaadin.gwt.client.widget.controlbar.ComponentPlaceHolder;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;

/**
 * MgnlArea.
 */
public class MgnlArea extends MgnlElement implements AreaListener {

    private static final String NODE_TYPE = "mgnl:area";
    private AreaEndBar areaEndBar;
    private ComponentPlaceHolder componentPlaceHolder;
    private Element componentMarkerElement;
    private EventBus eventBus;

    /**
     * MgnlElement. Represents a node in the tree built on cms-tags.
     */
    public MgnlArea(MgnlElement parent, EventBus eventBus) {
        super(parent);
        this.eventBus = eventBus;
    }

    public AreaEndBar getAreaEndBar() {
        return areaEndBar;
    }

    public void setAreaEndBar(AreaEndBar areaEndBar) {
        this.areaEndBar = areaEndBar;
    }

    public ComponentPlaceHolder getComponentPlaceHolder() {
        return componentPlaceHolder;
    }

    public void setComponentPlaceHolder(ComponentPlaceHolder componentPlaceHolder) {
        this.componentPlaceHolder = componentPlaceHolder;
    }


    public void setComponentMarkerElement(Element componentElement) {
        this.componentMarkerElement = componentElement;
    }

    public Element getComponentMarkerElement() {
        return componentMarkerElement;
    }

    @Override
    public AbstractElement getTypedElement() {
        AreaElement area = new AreaElement(getAttribute("workspace"), getAttribute("path"), getAttribute("dialog"), getAttribute("availableComponents"));

        boolean addible = true;
        if (getAttributes().containsKey(OperationPermissionDefinition.ADDIBLE)) {
            addible = Boolean.parseBoolean(getAttribute(OperationPermissionDefinition.ADDIBLE));
        }
        area.setAddible(addible);

        return area;
    }

    @Override
    public boolean isPage() {
        return false;
    }

    @Override
    public boolean isArea() {
        return true;
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public void createOptionalArea() {
        String workspace = getAttribute("workspace");
        String path = getAttribute("path");
        eventBus.fireEvent(new NewAreaEvent(workspace, NODE_TYPE, path));

    }

    @Override
    public void editArea() {
        String workspace = getAttribute("workspace");
        String path = getAttribute("path");
        String dialog = getAttribute("dialog");
        eventBus.fireEvent(new EditComponentEvent(workspace, path, dialog));

    }

    @Override
    public void createNewComponent() {
        String workspace = getAttribute("workspace");
        String path = getAttribute("path");
        String availableComponents = getAttribute("availableComponents");

        eventBus.fireEvent(new NewComponentEvent(workspace, path, availableComponents));
    }

    @Override
    public boolean hasAddButton() {
        boolean optional = Boolean.parseBoolean(getAttribute("optional"));
        boolean created = Boolean.parseBoolean(getAttribute("created"));

        return optional && !created;
    }

    @Override
    public boolean hasEditButton() {
        boolean optional = Boolean.parseBoolean(getAttribute("optional"));
        boolean created = Boolean.parseBoolean(getAttribute("created"));
        boolean dialog = null != getAttribute("dialog");

        if (dialog) {
            // do not show edit-icon if the area has not been created
            if (optional && created) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAddComponentButton() {
        return Boolean.parseBoolean(getAttribute("showAddButton"));
    }

    @Override
    public String getLabel() {
        String label = getAttribute("label");
        boolean optional = Boolean.parseBoolean(getAttribute("optional"));
        return label + ((optional) ? " (optional)" : "");
    }

    @Override
    public String getPlaceHolderLabel() {
        String label = getAttribute("label");
        boolean showAddButton = Boolean.parseBoolean(getAttribute("showAddButton"));
        boolean showNewComponentArea = Boolean.parseBoolean(getAttribute("showNewComponentArea"));

        String labelString;
        // if the add new component area should be visible
        if (showNewComponentArea && !showAddButton) { // maximum of components is reached - show add new component area with the maximum reached message, but without the ADD button
            labelString = getI18nMessage("buttons.component.maximum.js");
        } else { // maximum of components is NOT reached - show add new component area with ADD button
            labelString = getI18nMessage("buttons.component.new.js");
            if (label != null && !label.isEmpty()) {
                labelString = getI18nMessage("buttons.new.js") + " " + label + " " + getI18nMessage("buttons.component.js");
            }
        }
        return labelString;
    }
}

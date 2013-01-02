/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.ui.vaadin.gwt.client.form.formsection.connector;

import info.magnolia.ui.vaadin.form.FormSection;
import info.magnolia.ui.vaadin.gwt.client.form.formsection.widget.FormSectionWidget;
import info.magnolia.ui.vaadin.gwt.client.form.tab.connector.FormTabConnector;

import java.util.List;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.shared.ui.Connect;

/**
 * FormSectionConnector.
 */
@Connect(FormSection.class)
public class FormSectionConnector extends AbstractLayoutConnector {

    private final StateChangeHandler errorHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent event) {
            updateChildError((ComponentConnector) event.getConnector());
        }
    };

    private void updateChildError(ComponentConnector connector) {
        final String errorMsg = connector.getState().errorMessage;
        boolean errorOccured = errorMsg != null && !errorMsg.isEmpty();
        if (getState().isValidationVisible && errorOccured) {
            getWidget().setFieldError(connector.getWidget(), errorMsg);
        } else {
            getWidget().clearError(connector.getWidget());
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setCaption(getState().caption);
        getWidget().setDescriptionVisible(getState().isDescriptionVisible);
        for (final ComponentConnector cc : getChildComponents()) {
            updateChildError(cc);
            getWidget().setFieldDescription(cc.getWidget(), getState().helpDescriptions.get(cc));
        }
        getParent().setHasErrors(getWidget().getErrorAmount() > 0);
    }

    @Override
    public FormTabConnector getParent() {
        return (FormTabConnector) super.getParent();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
        getWidget().setFieldCaption(connector.getWidget(), connector.getState().caption);
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent e) {
        final List<ComponentConnector> oldChildren = e.getOldChildren();
        final List<ComponentConnector> newChildren = getChildComponents();

        oldChildren.removeAll(newChildren);
        for (final ComponentConnector cc : oldChildren) {
            getWidget().remove(cc.getWidget());
        }

        int index = 0;
        for (final ComponentConnector cc : newChildren) {
            getWidget().insert(cc.getWidget(), index++);
            cc.addStateChangeHandler("errorMessage", errorHandler);
        }
    }

    @Override
    protected FormSectionState createState() {
        return new FormSectionState();
    }

    @Override
    public FormSectionState getState() {
        return (FormSectionState) super.getState();
    }

    @Override
    public FormSectionWidget getWidget() {
        return (FormSectionWidget) super.getWidget();
    }

}

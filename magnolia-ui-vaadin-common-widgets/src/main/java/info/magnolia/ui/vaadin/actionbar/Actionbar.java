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
package info.magnolia.ui.vaadin.actionbar;

import info.magnolia.ui.vaadin.gwt.client.actionbar.connector.ActionbarState;
import info.magnolia.ui.vaadin.gwt.client.actionbar.rpc.ActionbarServerRpc;
import info.magnolia.ui.vaadin.gwt.client.actionbar.shared.ActionbarFontItem;
import info.magnolia.ui.vaadin.gwt.client.actionbar.shared.ActionbarItem;
import info.magnolia.ui.vaadin.gwt.client.actionbar.shared.ActionbarResourceItem;
import info.magnolia.ui.vaadin.gwt.client.actionbar.shared.ActionbarSection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;

/**
 * The Actionbar widget, consisting of sections and groups of actions.
 */
public class Actionbar extends AbstractComponent implements ActionbarView {

    //private static final Logger log = LoggerFactory.getLogger(Actionbar.class);

    private ActionbarView.Listener listener;

    public Actionbar() {
        setSizeFull();
        setWidth(null);
        setImmediate(true);
        setOpened(true);
        registerRpc(new ActionbarServerRpc() {
            @Override
            public void onFullScreenModeToggle(boolean isFullScreen) {
                listener.onChangeFullScreen(isFullScreen);
            }
            @Override
            public void onActionTriggered(String actionToken) {
                listener.onActionbarItemClicked(actionToken);
            }
            @Override
            public void setOpen(boolean isOpen) {
                setOpen(isOpen);
            }
        });
    }

    /*@Override
    public Component asVaadinComponent() {
        return this;
    }*/

    @Override
    public void setListener(ActionbarView.Listener listener) {
        this.listener = listener;
    }

    public void setOpened(boolean isOpen) {
        getState().isOpen = isOpen;
        if (isOpen) {
            addStyleName("open");
        } else {
            removeStyleName("open");
        }
    }

    @Override
    protected ActionbarState getState() {
        return (ActionbarState)super.getState();
    }
    
    @Override
    protected ActionbarState getState(boolean markAsDirty) {
        return (ActionbarState)super.getState();
    }
    
    
    // ACTION BAR API ///////////////////////////

    @Override
    public void addSection(String sectionName, String caption) {
        getState().sections.put(sectionName, new ActionbarSection(sectionName, caption));
    }

    @Override
    public void removeSection(String sectionName) {
        getState().sections.remove(sectionName);
    }

    @Override
    public void addAction(String actionName, String label, Resource icon, String groupName, String sectionName) {
        final ActionbarItem action = new ActionbarResourceItem(actionName, label, /*icon,*/ groupName);
        addAction(action, sectionName);
    }

    @Override
    public void addAction(String actionName, String label, String icon, String groupName, String sectionName) {
        final ActionbarItem action = new ActionbarFontItem(actionName, label, icon, groupName);
        addAction(action, sectionName);
    }

    public void addAction(ActionbarItem action, String sectionName) {
        ActionbarSection section =getState().sections.get(sectionName);
        if (section != null) {
            section.addAction(action);
        } else {
            //log.warn("Action was not added: no section found with name '" + sectionName + "'.");
        }        
    }
    
    @Override
    public void setPreview(Resource previewResource, String sectionName) {
        setResource(sectionName, previewResource);
    }

    public Map<String, ActionbarSection> getSections() {
        return getState(false).sections;
    }

    @Override
    public void setActionEnabled(String actionName, boolean isEnabled) {
        final Collection<ActionbarSection> sections = getState().sections.values();
        for (ActionbarSection section : sections) {
            setActionEnabled(section, actionName, isEnabled);
        }
    }

    @Override
    public void setActionEnabled(String sectionName, String actionName, boolean isEnabled) {
        setActionEnabled(getState().sections.get(sectionName), actionName, isEnabled);
    }
   
    
    @Override
    public void setGroupEnabled(String groupName, boolean isEnabled) {
        for (ActionbarSection section : getState().sections.values()) {
            setGroupEnabled(section, groupName, isEnabled);
        }   
    }

    @Override
    public void setGroupEnabled(String groupName, String sectionName, boolean isEnabled) {
        setGroupEnabled(getState().sections.get(sectionName), groupName, isEnabled);
    }

    @Override
    public void setSectionVisible(String sectionName, boolean isVisible) {
        ActionbarSection section = getState().sections.get(sectionName);
        if (isVisible && section != null) {
            getState().visibleSections.add(section);
        } else {
            getState().visibleSections.remove(section);
        }
    }

    @Override
    public boolean isSectionVisible(String sectionName) {
        final Iterator<ActionbarSection> it = getState(false).visibleSections.iterator();
        boolean result = false;
        while (!result && it.hasNext()) {
            result = it.next().getName().equals(sectionName);
        }
        return result;
    }
    
    public void setGroupEnabled(ActionbarSection section, String groupName, boolean isEnabled) {
        for (ActionbarItem action : section.getActions().values()) {
            if (groupName.equals(action.getGroupName())) {
                if (isEnabled && !getState().enabledActions.contains(action)) {
                    getState().enabledActions.add(action);
                } else {
                    getState().enabledActions.remove(action);
                }
            }
        }        
    }
    
    public void setActionEnabled(ActionbarSection section, String actionName, boolean isEnabled) {
        ActionbarItem action = section.getActions().get(actionName);
        if (action != null && !getState().enabledActions.contains(action)) {
            getState().enabledActions.add(action);
        } else {
            getState().enabledActions.remove(action);
        }        
    }
}

/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.ui.model.dialog.definition;

import info.magnolia.ui.model.dialog.action.DialogActionDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A definition of a configured dialog. Holds a list of tabs.
 * --> align with AppDescriptor, use interface, same for fielddef, tabdef, extract common fields -> UiItem
 */
public class ConfiguredDialogDefinition implements DialogDefinition {

    private String id;
    private String name;
    private String label;
    private String i18nBasename;
    private List<TabDefinition> tabs = new ArrayList<TabDefinition>();
    private List<DialogActionDefinition> actions = new ArrayList<DialogActionDefinition>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public List<TabDefinition> getTabs() {
        return tabs;
    }

    @Override
    public void setTabs(List<TabDefinition> tabs) {
        this.tabs = tabs;
    }

    @Override
    public List<DialogActionDefinition> getActions() {
        return actions;
    }

    @Override
    public boolean addAction(DialogActionDefinition actionDefinition) {
        return this.actions.add(actionDefinition);
    }

    @Override
    public void setActions(List<DialogActionDefinition> actions) {
        this.actions = actions;
    }

    @Override
    public boolean addTab(TabDefinition tabDefinition) {
        return tabs.add(tabDefinition);
    }

    @Override
    public String getI18nBasename() {
        return i18nBasename;
    }

    @Override
    public void setI18nBasename(String i18nBasename) {
        this.i18nBasename = i18nBasename;
    }

}

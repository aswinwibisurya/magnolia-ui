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
package info.magnolia.ui.admincentral.shellapp.pulse.message.definition;

import info.magnolia.ui.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.form.definition.FormDefinition;
import info.magnolia.ui.model.action.ActionDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * A definition of a configured message view.
 */
public class ConfiguredMessageViewDefinition implements MessageViewDefinition {

    private List<ActionDefinition> actions = new LinkedList<ActionDefinition>();

    private FormDefinition form;

    private ActionbarDefinition actionbar;

    @Override
    public List<ActionDefinition> getActions() {
        return actions;
    }

    public void setActions(List<ActionDefinition> actions) {
        this.actions = actions;
    }
    @Override
    public FormDefinition getForm() {
        return form;
    }

    public void setForm(FormDefinition form) {
        this.form = form;
    }

    @Override
    public ActionbarDefinition getActionbar() {
        return actionbar;
    }

    public void setActionbar(ActionbarDefinition actionbar) {
        this.actionbar = actionbar;
    }
}

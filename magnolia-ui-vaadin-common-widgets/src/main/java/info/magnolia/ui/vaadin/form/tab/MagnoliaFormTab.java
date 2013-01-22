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
package info.magnolia.ui.vaadin.form.tab;

import info.magnolia.ui.vaadin.form.FormSection;
import info.magnolia.ui.vaadin.gwt.client.form.tab.connector.FormTabState;
import info.magnolia.ui.vaadin.tabsheet.MagnoliaTab;

/**
 * Dialog tab.
 */
public class MagnoliaFormTab extends MagnoliaTab {

    private final FormSection content;

    public MagnoliaFormTab(String caption, final FormSection content) {
        super(caption, content);
        this.content = content;
        // DialogLayout needs this info to display it when show all tab is active
        this.content.setCaption(caption);
    }

    @Override
    protected FormTabState getState() {
        return (FormTabState) super.getState();
    }

    @Override
    public FormSection getContent() {
        return content;
    }

    public void setValidationVisible(boolean isVisible) {
        content.setValidationVisible(isVisible);
        markAsDirty();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        getState().errorAmount = content.getErrorAmount();
    }

}

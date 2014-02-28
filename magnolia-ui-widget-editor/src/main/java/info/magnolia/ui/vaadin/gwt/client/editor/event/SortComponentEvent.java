/**
 * This file Copyright (c) 2012-2014 Magnolia International
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
package info.magnolia.ui.vaadin.gwt.client.editor.event;

import info.magnolia.ui.vaadin.gwt.client.shared.AreaElement;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when the {@link info.magnolia.ui.vaadin.gwt.client.editor.dom.MgnlComponent} of an
 * {@link info.magnolia.ui.vaadin.gwt.client.editor.dom.MgnlArea} should be sorted.
 * The event will be sent to server side.
 *
 * fired by: {@link info.magnolia.ui.vaadin.gwt.client.editor.dom.MgnlComponent#sortComponent(info.magnolia.ui.vaadin.gwt.client.editor.dom.MgnlComponent)}
 * handler registered in: {@link info.magnolia.ui.vaadin.gwt.client.connector.PageEditorConnector}.
 */
public class SortComponentEvent extends GwtEvent<SortComponentEventHandler> {

    public static Type<SortComponentEventHandler> TYPE = new Type<SortComponentEventHandler>();

    private AreaElement areaElement;


    public SortComponentEvent(AreaElement areaElement) {
        this.areaElement = areaElement;
    }

    @Override
    public Type<SortComponentEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SortComponentEventHandler handler) {
        handler.onSortComponent(this);
    }

    public AreaElement getAreaElement() {
        return areaElement;
    }

}

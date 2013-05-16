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
package info.magnolia.ui.vaadin.gwt.client.magnoliashell.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

/**
 * Fired when an app has to be loaded.
 * Emitter:
 * @see info.magnolia.ui.vaadin.gwt.client.magnoliashell.shell.MagnoliaShellConnector
 * Receiver:
 * @see info.magnolia.ui.vaadin.gwt.client.magnoliashell.viewport.connector.AppsViewportConnector
 */
public class AppRequestedEvent extends Event<AppRequestedEvent.Handler> {

    /**
     * Handler interface for AppRequestedEvent.
     */
    public interface Handler extends EventHandler {
        void onAppRequested(AppRequestedEvent event);
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private String appName;

    public AppRequestedEvent(String appName) {
        super();
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAppRequested(this);
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }
}

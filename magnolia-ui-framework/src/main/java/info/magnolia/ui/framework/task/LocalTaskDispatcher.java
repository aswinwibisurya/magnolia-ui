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
package info.magnolia.ui.framework.task;

import info.magnolia.event.EventBus;
import info.magnolia.task.event.TaskEvent;
import info.magnolia.ui.api.event.AdmincentralEventBus;

import javax.inject.Inject;
import javax.inject.Named;

import com.vaadin.server.VaadinSession;

/**
 * Dispatches Task events on an {@link EventBus} for a certain user.
 */
public class LocalTaskDispatcher implements TaskEventDispatcher {

    private EventBus eventBus;
    private VaadinSession vaadinSession;

    @Inject
    public LocalTaskDispatcher(@Named(AdmincentralEventBus.NAME) final EventBus eventBus, VaadinSession vaadinSession) {
        this.eventBus = eventBus;
        this.vaadinSession = vaadinSession;
    }

    @Override
    public void onTaskEvent(TaskEvent taskEvent) {
        VaadinSession previous = VaadinSession.getCurrent();
        try {
            VaadinSession.setCurrent(vaadinSession);
            eventBus.fireEvent(taskEvent);
        } finally {
            VaadinSession.setCurrent(previous);
        }
    }

}

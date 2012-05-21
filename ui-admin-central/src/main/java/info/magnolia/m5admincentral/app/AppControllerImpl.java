/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.m5admincentral.app;

import info.magnolia.objectfactory.Components;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import com.google.inject.Inject;

/**
 * Default AppController implementation.
 *
 * @version $Id$
 */
@Singleton
public class AppControllerImpl implements AppController {

    private AppRegistry appRegistry;

    private Map<String, AppLifecycle> runningApps = new HashMap<String, AppLifecycle>();

    @Inject
    public AppControllerImpl(AppRegistry appRegistry) {
        this.appRegistry = appRegistry;
    }

    @Override
    public void startIfNotAlreadyRunning(String name) {
        doStart(name);
    }

    @Override
    public void startIfNotAlreadyRunningThenFocus(String name) {
        final AppLifecycle lifecycle = doStart(name);
        lifecycle.focus();
    }

    private AppLifecycle doStart(String name) {
        AppLifecycle lifecycle = runningApps.get(name);
        if (lifecycle == null) {
            AppDescriptor descriptor = appRegistry.getAppDescriptor(name);
            lifecycle = Components.newInstance(descriptor.getAppClass());
            lifecycle.start();
            runningApps.put(name, lifecycle);
        }
        return lifecycle;
    }
}

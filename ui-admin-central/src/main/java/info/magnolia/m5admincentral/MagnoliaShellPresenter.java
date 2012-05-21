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
package info.magnolia.m5admincentral;

import info.magnolia.m5admincentral.app.AppController;
import info.magnolia.m5admincentral.app.AppDescriptor;
import info.magnolia.m5admincentral.app.AppRegistry;
import info.magnolia.m5admincentral.app.dialog.DialogTestActivity;
import info.magnolia.m5admincentral.app.dialog.DialogTestApp;
import info.magnolia.m5admincentral.app.dialog.DialogTestPlace;
import info.magnolia.m5admincentral.app.pages.PagesActivity;
import info.magnolia.m5admincentral.app.pages.PagesApp;
import info.magnolia.m5admincentral.app.pages.PagesPlace;
import info.magnolia.m5admincentral.dialog.DialogPresenterFactory;
import info.magnolia.m5admincentral.framework.AppActivityManager;
import info.magnolia.m5admincentral.framework.AppActivityMapper;
import info.magnolia.m5admincentral.framework.ShellAppActivityManager;
import info.magnolia.m5admincentral.framework.ShellAppActivityMapper;
import info.magnolia.m5admincentral.shellapp.applauncher.AppLauncherPlace;
import info.magnolia.m5admincentral.shellapp.favorites.FavoritesPlace;
import info.magnolia.m5admincentral.shellapp.pulse.PulsePlace;
import info.magnolia.m5vaadin.shell.MagnoliaShell;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.framework.event.EventBus;
import info.magnolia.ui.framework.place.Place;
import info.magnolia.ui.framework.place.PlaceController;
import info.magnolia.ui.framework.place.PlaceHistoryHandler;
import info.magnolia.ui.framework.place.PlaceHistoryMapper;
import info.magnolia.ui.framework.place.PlaceHistoryMapperImpl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.vaadin.ui.Window;

/**
 * Presenter meant to bootstrap the MagnoliaShell.
 * @version $Id$
 */
public class MagnoliaShellPresenter implements MagnoliaShellView.Presenter {

    private final MagnoliaShellView view;

    @Inject
    public MagnoliaShellPresenter(final MagnoliaShellView view, final EventBus bus, final AppRegistry appRegistry,
            final AppController appController, final PlaceController controller, final DialogPresenterFactory dialogPresenterFactory,
            ComponentProvider componentProvider) {
        super();
        this.view = view;
        this.view.setPresenter(this);

        AppDescriptor descriptor = new AppDescriptor();
        descriptor.setName("pages1");
        descriptor.setLabel("Pages1");
        descriptor.setIcon("img/icon-app-pages.png");
        descriptor.setAppClass(PagesApp.class);
        descriptor.setCategory("EDIT");
        descriptor.addActivityMapping(PagesPlace.class, PagesActivity.class);

        if (!appRegistry.isAppDescriptionRegistered(descriptor.getName())) {
            appRegistry.registerAppDescription(descriptor.getName(), descriptor);   
        }

        descriptor = new AppDescriptor();
        descriptor.setName("dialog");
        descriptor.setLabel("Dialog");
        descriptor.setIcon("img/icon-app-default.png");
        descriptor.setAppClass(DialogTestApp.class);
        descriptor.setCategory("MANAGE");
        descriptor.addActivityMapping(DialogTestPlace.class, DialogTestActivity.class);

        if (!appRegistry.isAppDescriptionRegistered(descriptor.getName())) {
            appRegistry.registerAppDescription(descriptor.getName(), descriptor);   
        }

        final ShellAppActivityManager shellAppManager = new ShellAppActivityManager(new ShellAppActivityMapper(componentProvider), bus);
        shellAppManager.setViewPort(view.getRoot().getShellAppViewport());

        final AppActivityManager appManager = new AppActivityManager(new AppActivityMapper(appRegistry, appController, componentProvider), bus);
        appManager.setViewPort(view.getRoot().getAppViewport());

        final PlaceHistoryMapper placeHistoryMapper = new PlaceHistoryMapperImpl(getSupportedPlaces(appRegistry));
        final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(placeHistoryMapper, view.getRoot());

        historyHandler.register(controller, bus, new AppLauncherPlace("test"));
    }

    public void start(final Window window) {
        final MagnoliaShell shell = view.getRoot();
        shell.setSizeFull();
        window.addComponent(shell);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Place>[] getSupportedPlaces(AppRegistry appRegistry) {
        List<Class<? extends Place>> places = new ArrayList<Class<? extends Place>>();
        places.add(AppLauncherPlace.class);
        places.add(PulsePlace.class);
        places.add(FavoritesPlace.class);
        for (AppDescriptor descriptor : appRegistry.getAppDescriptors()) {
            places.addAll(descriptor.getActivityMappings().keySet());
        }
        return places.toArray(new Class[places.size()]);
    }
}

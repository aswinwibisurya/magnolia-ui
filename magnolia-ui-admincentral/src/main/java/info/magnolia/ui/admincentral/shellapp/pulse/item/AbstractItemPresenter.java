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
package info.magnolia.ui.admincentral.shellapp.pulse.item;

import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.registry.RegistrationException;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.admincentral.shellapp.pulse.item.definition.ItemViewDefinition;
import info.magnolia.ui.admincentral.shellapp.pulse.item.registry.ItemViewDefinitionRegistry;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.availability.AvailabilityChecker;
import info.magnolia.ui.api.availability.AvailabilityDefinition;
import info.magnolia.ui.api.pulse.task.ItemPresenter;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.dialog.formdialog.FormBuilder;

import java.util.Arrays;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.vaadin.data.util.BeanItem;

/**
 * The item detail presenter. A base abstract class for specific pulse item presenters.
 * 
 * @param <T> the type of pulse item, e.g. Message, Task, etc.
 */
public abstract class AbstractItemPresenter<T> implements ItemPresenter, ItemView.Listener, ActionbarPresenter.Listener {

    private final ItemView view;
    private ItemActionExecutor itemActionExecutor;
    private ItemViewDefinitionRegistry itemViewDefinitionRegistry;
    private FormBuilder formbuilder;
    private ActionbarPresenter actionbarPresenter;
    private AvailabilityChecker availabilityChecker;
    private Listener listener;
    protected T item;
    private I18nizer i18nizer;

    public static final String DEFAULT_VIEW = "ui-admincentral:default";

    @Inject
    public AbstractItemPresenter(T item, ItemView view, ItemActionExecutor itemActionExecutor, AvailabilityChecker availabilityChecker, ItemViewDefinitionRegistry itemViewDefinitionRegistry, FormBuilder formbuilder, ActionbarPresenter actionbarPresenter, I18nizer i18nizer) {
        this.item = item;
        this.view = view;
        this.itemActionExecutor = itemActionExecutor;
        this.itemViewDefinitionRegistry = itemViewDefinitionRegistry;
        this.formbuilder = formbuilder;
        this.actionbarPresenter = actionbarPresenter;
        this.availabilityChecker = availabilityChecker;
        this.i18nizer = i18nizer;

        view.setListener(this);
        actionbarPresenter.setListener(this);
    }

    public final View start() {
        setItemViewTitle(view);
        final String itemView = getItemViewName();

        try {
            ItemViewDefinition itemViewDefinition = itemViewDefinitionRegistry.get(itemView);
            itemViewDefinition = i18nizer.decorate(itemViewDefinition);

            itemActionExecutor.setMessageViewDefinition(itemViewDefinition);

            BeanItem<T> beanItem = asBeanItem();
            View mView = formbuilder.buildView(itemViewDefinition.getForm(), beanItem);
            view.setItemView(mView);
            view.setActionbarView(actionbarPresenter.start(itemViewDefinition.getActionbar(), itemViewDefinition.getActions()));

            for (Entry<String, ActionDefinition> entry : itemViewDefinition.getActions().entrySet()) {
                final String actionName = entry.getValue().getName();
                AvailabilityDefinition availability = itemActionExecutor.getActionDefinition(actionName).getAvailability();
                if (availabilityChecker.isAvailable(availability, Arrays.asList(new Object[] { beanItem.getBean() }))) {
                    actionbarPresenter.enable(actionName);
                } else {
                    actionbarPresenter.disable(actionName);
                }
            }

        } catch (RegistrationException e) {
            throw new RuntimeException("Could not retrieve itemView for " + itemView, e);
        }
        return view;
    }

    public T getItem() {
        return item;
    }

    protected abstract String getItemViewName();

    protected abstract void setItemViewTitle(ItemView view);

    protected abstract BeanItem<T> asBeanItem();

    @Override
    public void onNavigateToList() {
        listener.showList();
    }

    @Override
    public void onUpdateDetailView(String itemId) {
        listener.updateDetailView(itemId);
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onActionbarItemClicked(String actionName) {
        try {
            itemActionExecutor.execute(actionName, item, this, itemActionExecutor);

        } catch (ActionExecutionException e) {
            throw new RuntimeException("Could not execute action " + actionName, e);
        }
    }

}

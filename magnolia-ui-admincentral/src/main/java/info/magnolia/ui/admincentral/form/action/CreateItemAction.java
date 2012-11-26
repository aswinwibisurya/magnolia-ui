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
package info.magnolia.ui.admincentral.form.action;

import info.magnolia.ui.admincentral.app.content.location.ItemLocation;
import info.magnolia.ui.admincentral.content.item.ItemView;
import info.magnolia.ui.admincentral.form.FormPresenterFactory;
import info.magnolia.ui.framework.location.LocationController;
import info.magnolia.ui.model.action.ActionBase;
import info.magnolia.ui.model.action.ActionExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * CreateItemAction.
 */
public class CreateItemAction extends ActionBase<CreateItemActionDefinition> {

    private static final Logger log = LoggerFactory.getLogger(CreateItemAction.class);

    private LocationController locationController;
    private final Node parent;
    private final FormPresenterFactory formPresenterFactory;

    public CreateItemAction(CreateItemActionDefinition definition, LocationController locationController,  Node parent, FormPresenterFactory formPresenterFactory) {
        super(definition);
        this.locationController = locationController;
        this.parent = parent;
        this.formPresenterFactory = formPresenterFactory;
    }

    @Override
    public void execute() throws ActionExecutionException {

        /**
         * The ItemSubApp only gets a location containing nodePath and View Type.
         * When creating a new node, we either create it here and pass the new path to the subapp or
         * we pass all needed parameters to the location. This is less messy, but not optimal.. at all.
         */
        try {
            Node newNode = parent.addNode("untitled", getDefinition().getNodeType());

            newNode.getSession().save();
            ItemLocation location = new ItemLocation(getDefinition().getAppId(), getDefinition().getSubAppId(), ItemView.ViewType.EDIT, newNode.getPath());
            locationController.goTo(location);

        } catch (RepositoryException e) {
            log.error("Could not create child node on parent.", e);
        }
    }
}

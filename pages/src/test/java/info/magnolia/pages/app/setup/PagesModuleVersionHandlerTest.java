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
package info.magnolia.pages.app.setup;

import static info.magnolia.jcr.nodebuilder.Ops.*;
import static org.junit.Assert.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.nodebuilder.NodeBuilderUtil;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.Version;
import info.magnolia.pages.setup.PagesModuleVersionHandler;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class.
 */
public class PagesModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Node dialog;
    private Node actions;
    private Session session;

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/ui-admincentral.xml";
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList(
                "/META-INF/magnolia/core.xml"
        );
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new PagesModuleVersionHandler();
    }


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        dialog = NodeUtil.createPath(session.getRootNode(), "/modules/pages/dialogs", NodeTypes.ContentNode.NAME);
        dialog.getSession().save();

        actions = NodeUtil.createPath(session.getRootNode(), "/modules/pages/apps/pages/subApps/browser/actions", NodeTypes.ContentNode.NAME);

    }

    @Test
    public void testUpdateTo501WithExistingLinkDefinition() throws ModuleManagementException, RepositoryException {
        // GIVEN
        dialog.addNode("link", NodeTypes.ContentNode.NAME);
        assertTrue(dialog.hasNode("link"));
        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0"));

        // THEN
        assertFalse(dialog.hasNode("link"));

    }

    @Test
    public void testUpdateTo501WithNonExistingLinkDefinition() throws ModuleManagementException, RepositoryException {
        // GIVEN

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0"));

        // THEN
        assertFalse(dialog.hasNode("link"));
    }

    @Test
    public void testUpdateTo501CreatePageDialogHasLabel() throws ModuleManagementException, RepositoryException {
        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node createPage = NodeUtil.createPath(session.getRootNode(), "/modules/pages/dialogs/createPage/form", NodeTypes.ContentNode.NAME);
        createPage.getSession().save();
        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0"));

        // THEN
        assertTrue(createPage.hasProperty("label"));
    }

    @Test
    public void testUpdateTo502HasNewActions() throws ModuleManagementException, RepositoryException {

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.1"));

        // THEN
        assertTrue(actions.hasNode("confirmDeletion"));
    }


    @Test
    public void testUpdateTo502CleanupDeleteAction() throws ModuleManagementException, RepositoryException {
        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node action = NodeUtil.createPath(session.getRootNode(), "/modules/pages/apps/pages/subApps/browser/actions/delete", NodeTypes.ContentNode.NAME);
        action.setProperty("label", "Delete item");
        action.setProperty("icon", "icon-delete");
        action.getSession().save();

        // WHEN
        NodeUtil.createPath(action, "availability", NodeTypes.ContentNode.NAME);

        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.1"));

        // THEN
        Node delete = actions.getNode("delete");
        assertFalse(delete.hasNode("availability"));
        assertFalse(delete.hasProperty("icon"));
        assertFalse(delete.hasProperty("label"));
    }

    @Test
    public void testUpdateTo502ActionbarNodesUpdated() throws ModuleManagementException, RepositoryException {

        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node actionbarItems = NodeUtil.createPath(session.getRootNode(), "/modules/pages/apps/pages/subApps/browser/actionbar/sections/pageActions/groups/addingActions/items", NodeTypes.ContentNode.NAME);

        NodeUtil.createPath(actionbarItems, "delete", NodeTypes.ContentNode.NAME);


        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.1"));

        // THEN
        assertFalse(actionbarItems.hasNode("delete"));
        assertTrue(actionbarItems.hasNode("confirmDeletion"));
    }

    @Test
    public void testUpdateTo5dot1ConfirmDeletionIsSetToMultiple() throws RepositoryException, ModuleManagementException {
        // GIVEN
        Node availability = NodeUtil.createPath(MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getRootNode(), "/modules/pages/apps/pages/subApps/browser/actions/confirmDeletion/availability", NodeTypes.ContentNode.NAME);
        assertFalse(availability.hasProperty("multiple"));

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.2"));

        // THEN
        assertTrue(availability.hasProperty("multiple"));
        assertEquals("true", availability.getProperty("multiple").getString());
    }

    @Test
    public void testUpdateTo51RemovesCommandChains() throws ModuleManagementException, RepositoryException {

        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node commands = NodeUtil.createPath(session.getRootNode(), "/modules/pages/commands/website", NodeTypes.ContentNode.NAME);
        // GIVEN
        NodeBuilderUtil.build(RepositoryConstants.CONFIG, commands.getPath(),
                addNode("activate", NodeTypes.ContentNode.NAME).then(
                        addNode("version", NodeTypes.ContentNode.NAME),
                        addNode("activate", NodeTypes.ContentNode.NAME)
                ),
                addNode("deactivate", NodeTypes.ContentNode.NAME).then(
                        addNode("deactivate", NodeTypes.ContentNode.NAME).then(
                                addNode("version", NodeTypes.ContentNode.NAME),
                                addNode("deactivate", NodeTypes.ContentNode.NAME)
                        )
                )
        );

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.2"));

        // THEN
        assertFalse(session.nodeExists("/modules/pages/commands/website"));
        assertFalse(session.nodeExists("/modules/pages/commands"));
    }

    @Test
    public void testUpdateTo51NonEmptyCommandsRemain() throws ModuleManagementException, RepositoryException {

        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node commands = NodeUtil.createPath(session.getRootNode(), "/modules/pages/commands/website", NodeTypes.ContentNode.NAME);

        NodeBuilderUtil.build(RepositoryConstants.CONFIG, commands.getPath(),
                addNode("activate", NodeTypes.ContentNode.NAME).then(
                        addNode("version", NodeTypes.ContentNode.NAME),
                        addNode("activate", NodeTypes.ContentNode.NAME)
                ),
                addNode("deactivate", NodeTypes.ContentNode.NAME).then(
                        addNode("deactivate", NodeTypes.ContentNode.NAME).then(
                                addNode("version", NodeTypes.ContentNode.NAME),
                                addNode("deactivate", NodeTypes.ContentNode.NAME)
                        )
                ),
                addNode("customCommand", NodeTypes.ContentNode.NAME).then(
                        addNode("custom", NodeTypes.ContentNode.NAME).then(
                                addNode("version", NodeTypes.ContentNode.NAME),
                                addNode("else", NodeTypes.ContentNode.NAME)
                        )
                )
        );

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.2"));

        // THEN
        assertTrue(session.nodeExists("/modules/pages/commands/website"));
        assertTrue(session.nodeExists("/modules/pages/commands"));
    }

    @Test
    public void testUpdateTo51ActivationUsesVersionedCatalog() throws RepositoryException, ModuleManagementException {

        // GIVEN
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node actions = NodeUtil.createPath(session.getRootNode(), "/modules/pages/apps/pages/subApps/browser/actions", NodeTypes.ContentNode.NAME);

        NodeBuilderUtil.build(RepositoryConstants.CONFIG, actions.getPath(),
                addNode("activate", NodeTypes.ContentNode.NAME).then(
                        addProperty("catalog", "website")
                ),
                addNode("activateRecursive", NodeTypes.ContentNode.NAME).then(
                        addProperty("catalog", "website")
                ),
                addNode("deactivate", NodeTypes.ContentNode.NAME).then(
                        addProperty("catalog", "website")
                ),
                addNode("activateDeletion", NodeTypes.ContentNode.NAME).then(
                        addProperty("catalog", "website")
                )
        );

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("5.0.2"));

        // THEN
        Node activate = actions.getNode("activate");
        Node activateRecursive = actions.getNode("activateRecursive");
        Node deactivate = actions.getNode("deactivate");
        Node activateDeletion = actions.getNode("activateDeletion");

        assertEquals("versioned", activate.getProperty("catalog").getString());
        assertEquals("versioned", activateRecursive.getProperty("catalog").getString());
        assertEquals("versioned", deactivate.getProperty("catalog").getString());
        assertEquals("versioned", activateDeletion.getProperty("catalog").getString());
    }
}

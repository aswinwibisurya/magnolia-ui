/**
 * This file Copyright (c) 2011 Magnolia International
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
package info.magnolia.ui.admincentral.tree.model;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.admincentral.container.JcrContainerSource;
import info.magnolia.ui.admincentral.workbench.action.WorkbenchActionFactory;
import info.magnolia.ui.model.action.Action;
import info.magnolia.ui.model.action.ActionDefinition;
import info.magnolia.ui.model.action.ActionExecutionException;
import info.magnolia.ui.model.workbench.definition.ItemTypeDefinition;
import info.magnolia.ui.model.workbench.definition.WorkbenchDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.jcr.Item;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model class for tree. Serves as a source for operations by AbstractJcrContainer and executes them.
 *
 */
public class TreeModel implements JcrContainerSource {

    private static final Logger log = LoggerFactory.getLogger(TreeModel.class);
    private WorkbenchActionFactory actionFactory;
    private WorkbenchDefinition workbenchDefinition;

    public TreeModel(WorkbenchDefinition workbenchDefinition,  WorkbenchActionFactory actionFactory) {
        this.workbenchDefinition = workbenchDefinition;
        this.actionFactory = actionFactory;
    }

    @Override
    public Collection<Item> getChildren(Item item) throws RepositoryException {
        if (!item.isNode()) {
            return Collections.emptySet();
        }

        Node node = (Node) item;

        ArrayList<Item> c = new ArrayList<Item>();

        for (ItemTypeDefinition itemType : workbenchDefinition.getItemTypes()) {
            ArrayList<Node> nodes = new ArrayList<Node>();
            NodeIterator iterator = node.getNodes();
            while (iterator.hasNext()) {
                Node next = (Node) iterator.next();
                if (itemType.getItemType().equals(next.getPrimaryNodeType().getName())) {
                    nodes.add(next);
                }
            }
            // TODO This behaviour is optional in old AdminCentral, you can set a custom comparator.
            Collections.sort(nodes, new Comparator<Node>() {
                @Override
                public int compare(Node lhs, Node rhs) {
                    try {
                        return lhs.getName().compareTo(rhs.getName());
                    } catch (RepositoryException e) {
                        throw new RuntimeRepositoryException(e);
                    }
                }
            });
            for (Node n : nodes) {
                c.add(n);
            }
        }

        boolean includeProperties = false;
        for (ItemTypeDefinition itemType : workbenchDefinition.getItemTypes()) {
            if (itemType.getItemType().equals(ItemTypeDefinition.ITEM_TYPE_PROPERTY)) {
                includeProperties = true;
                break;
            }
        }

        if (includeProperties) {
            ArrayList<Property> properties = new ArrayList<Property>();
            PropertyIterator propertyIterator = node.getProperties();
            while (propertyIterator.hasNext()) {
                Property property = propertyIterator.nextProperty();
                if (!property.getName().startsWith(MgnlNodeType.JCR_PREFIX)) {
                    properties.add(property);
                }
            }
            Collections.sort(properties, new Comparator<Property>() {
                @Override
                public int compare(Property lhs, Property rhs) {
                    try {
                        return lhs.getName().compareTo(rhs.getName());
                    } catch (RepositoryException e) {
                        throw new RuntimeRepositoryException(e);
                    }
                }
            });
            for (Property p : properties) {
                c.add(p);
            }
        }

        return Collections.unmodifiableCollection(c);
    }

    @Override
    public Collection<Item> getRootItemIds() throws RepositoryException {
        return getChildren(getRootNode());
    }

    @Override
    public boolean isRoot(Item item) throws RepositoryException {
        if (!item.isNode()) {
            return false;
        }
        int depthOfRootNodesInTree = getRootNode().getDepth() + 1;
        return item.getDepth() <= depthOfRootNodesInTree;
    }

    @Override
    public boolean hasChildren(Item item) throws RepositoryException {
        if (!item.isNode()) {
            return false;
        }
        return !getChildren(item).isEmpty();
    }


    @Override
    public String getItemIcon(Item item) throws RepositoryException {
        for (ItemTypeDefinition itemType : workbenchDefinition.getItemTypes()) {
            if (!item.isNode() && itemType.getItemType().equals(ItemTypeDefinition.ITEM_TYPE_PROPERTY)) {
                return itemType.getIcon();
            } else if (item.isNode()) {
                Node node = (Node) item;
                if (itemType.getItemType().equals(node.getPrimaryNodeType().getName())) {
                    return itemType.getIcon();
                }
            }
        }
        return null;
    }

    @Override
    public Node getNodeByIdentifier(String nodeIdentifier) throws RepositoryException {
        return getSession().getNodeByIdentifier(nodeIdentifier);
    }

    @Override
    public Item getItemByPath(String path) throws RepositoryException {
        String absolutePath = getPathInWorkspace(path);
        return getSession().getItem(absolutePath);
    }

    // Move operations performed by drag-n-drop in JcrBrowser

    // TODO these move methods need to be commands instead

    public boolean moveItem(Item source, Item target) throws RepositoryException {
        if(!basicMoveCheck(source,target)) {
            return false;
        }
        NodeUtil.moveNode((Node)source, (Node)target);
        source.getSession().save();

        return true;
    }

    public boolean moveItemBefore(Item source, Item target) throws RepositoryException {
        if(!basicMoveCheck(source,target)) {
            return false;
        }

        NodeUtil.moveNodeBefore((Node)source, (Node)target);
        source.getSession().save();

        return true;
    }

    public boolean moveItemAfter(Item source, Item target) throws RepositoryException {
        if(!basicMoveCheck(source,target)) {
            return false;
        }

        NodeUtil.moveNodeAfter((Node)source, (Node)target);
        source.getSession().save();

        return true;
    }

    /**
     * Perform basic check.
     */
    private boolean basicMoveCheck(Item source, Item target) throws RepositoryException {
        // One or both are not node... do nothing
        if (!target.isNode() && !source.isNode()) {
            return false;
        }
        // Source and origin are the same... do nothing
        if (target.getPath().equals(source.getPath())) {
            return false;
        }
        // Source can not be a child of target.
        if(target.getPath().startsWith(source.getPath())) {
            return false;
        }
        return true;
    }


    // Used by JcrBrowser and VaadinTreeView

    public String getPathInTree(Item item) throws RepositoryException {
        String base = workbenchDefinition.getPath();
        if ("/".equals(base)) {
            return item.getPath();
        } else {
            return StringUtils.substringAfter(item.getPath(), base);
        }
    }

    public void execute(ActionDefinition actionDefinition, Item item) throws ActionExecutionException {
        Action action = actionFactory.createAction(actionDefinition, item, null);
        action.execute();
    }


    private Session getSession() throws LoginException, RepositoryException {
        return MgnlContext.getJCRSession(workbenchDefinition.getWorkspace());
    }

    private Node getRootNode() throws RepositoryException {
        return getSession().getNode(workbenchDefinition.getPath());
    }

    private String getPathInWorkspace(String pathInTree) {
        String base = workbenchDefinition.getPath();
        if ("/".equals(base)) {
            return pathInTree;
        } else {
            return base + pathInTree;
        }
    }
}

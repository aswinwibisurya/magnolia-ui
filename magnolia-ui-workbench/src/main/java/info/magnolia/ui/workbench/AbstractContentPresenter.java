/**
 * This file Copyright (c) 2011-2013 Magnolia International
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
package info.magnolia.ui.workbench;

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.vaadin.integration.jcr.AbstractJcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemUtil;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrPropertyAdapter;
import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.definition.ContentPresenterDefinition;
import info.magnolia.ui.workbench.definition.NodeTypeDefinition;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.event.ItemDoubleClickedEvent;
import info.magnolia.ui.workbench.event.ItemRightClickedEvent;
import info.magnolia.ui.workbench.event.ItemsSelectedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Abstract generic logic for content presenters.
 */
public abstract class AbstractContentPresenter implements ContentPresenter, ContentView.Listener {

    private static final Logger log = LoggerFactory.getLogger(AbstractContentPresenter.class);

    private static final String ICON_PROPERTY = "icon-node-data";

    private static final String ICON_TRASH = "icon-trash";

    protected EventBus eventBus;

    protected WorkbenchDefinition workbenchDefinition;

    private List<String> selectedItemIds = new ArrayList<String>();

    protected String viewTypeName;

    // CONTENT PRESENTER

    @Override
    public ContentView start(WorkbenchDefinition workbenchDefinition, EventBus eventBus, String viewTypeName) {
        this.workbenchDefinition = workbenchDefinition;
        this.eventBus = eventBus;
        this.viewTypeName = viewTypeName;
        return null;
    }

    @Override
    public List<String> getSelectedItemIds() {
        return this.selectedItemIds;
    }

    public String getSelectedItemId() {
        return selectedItemIds.isEmpty() ? null : selectedItemIds.get(0);
    }

    @Override
    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemIds = new ArrayList<String>();
        this.selectedItemIds.add(selectedItemId);
    }

    // CONTENT VIEW LISTENER

    @Override
    public void onItemSelection(Set items) {
        try {
            Set<JcrItemAdapter> jcrItems = new HashSet<JcrItemAdapter>();
            if (items == null || items.isEmpty()) {
                log.debug("Got null com.vaadin.data.Item. ItemSelectedEvent will be fired with null path.");
                setSelectedItemId(getWorkbenchRoot().getIdentifier());
                jcrItems.add(toJcrItemAdapter(getWorkbenchRoot()));
            } else {
                selectedItemIds = new ArrayList<String>(items.size());
                for (Object o : items) {
                    String item = (String) o;
                    // if the selection is done by clicking the checkbox, the root item is added to the set - so it has to be ignored
                    // but only if there is any other item in the set
                    // TODO MGNLUI-1521
                    if (getWorkbenchRoot().getIdentifier().equals(item) && items.size() > 1) {
                        continue;
                    }
                    selectedItemIds.add(item);
                    jcrItems.add(toJcrItemAdapter(JcrItemUtil.getJcrItem(workbenchDefinition.getWorkspace(), item)));
                }
                log.debug("com.vaadin.data.Item at {} was selected. Firing ItemSelectedEvent...", selectedItemIds.toArray());
            }
            eventBus.fireEvent(new ItemsSelectedEvent(workbenchDefinition.getWorkspace(), jcrItems));
        } catch (Exception e) {
            log.error("An error occurred while selecting a row in the data grid", e);
        }
    }

    private Node getWorkbenchRoot() {
        try {
            return MgnlContext.getJCRSession(workbenchDefinition.getWorkspace()).getNode(workbenchDefinition.getPath());
        } catch (RepositoryException e) {
            log.debug("Cannot find workbench root node for workspace=[" + workbenchDefinition.getWorkspace() + "] and path=[" + workbenchDefinition.getPath() + "]. Error: " + e.getMessage());
            return null;
        }
    }

    private JcrItemAdapter toJcrItemAdapter(javax.jcr.Item item) {
        if (item == null) {
            return null;
        }
        JcrItemAdapter adapter = null;
        if (item.isNode()) {
            adapter = new JcrNodeAdapter((Node) item);
        } else {
            adapter = new JcrPropertyAdapter((Property) item);
        }
        return adapter;
    }

    @Override
    public void onDoubleClick(Item item) {
        if (item != null) {
            try {
                setSelectedItemId(((JcrItemAdapter) item).getItemId());
                log.debug("com.vaadin.data.Item at {} was double clicked. Firing ItemDoubleClickedEvent...", getSelectedItemId());
                eventBus.fireEvent(new ItemDoubleClickedEvent(workbenchDefinition.getWorkspace(), getSelectedItemId()));
            } catch (Exception e) {
                log.error("An error occurred while double clicking on a row in the data grid", e);
            }
        } else {
            log.warn("Got null com.vaadin.data.Item. No event will be fired.");
        }
    }

    @Override
    public void onRightClick(Item item, int clickX, int clickY) {
        if (item != null) {
            try {
                // if the right-clicket item is not yet selected
                if (!selectedItemIds.contains(((JcrItemAdapter) item).getItemId())) {
                    setSelectedItemId(((JcrItemAdapter) item).getItemId());
                }
                String clickedItemId = ((JcrItemAdapter) item).getItemId();
                log.debug("com.vaadin.data.Item at {} was right clicked. Firing ItemRightClickedEvent...", clickedItemId);
                eventBus.fireEvent(new ItemRightClickedEvent(workbenchDefinition.getWorkspace(), (JcrItemAdapter) item, clickX, clickY));
            } catch (Exception e) {
                log.error("An error occurred while right clicking on a row in the data grid", e);
            }
        } else {
            log.warn("Got null com.vaadin.data.Item. No event will be fired.");
        }
    }

    protected Iterator<ColumnDefinition> getColumnsIterator() {
        Iterator<ColumnDefinition> it = null;
        Iterator<ContentPresenterDefinition> viewsIterator = workbenchDefinition.getContentViews().iterator();
        while (viewsIterator.hasNext()) {
            ContentPresenterDefinition contentView = viewsIterator.next();
            if (contentView.getViewType().getText().equals(viewTypeName)) {
                it = contentView.getColumns().iterator();
                break;
            }
        }
        return it;
    }

    @Override
    public String getIcon(Item item) {

        try {

            if (item instanceof JcrPropertyAdapter) {
                return ICON_PROPERTY;
            } else if (item instanceof JcrNodeAdapter) {
                Node node = ((AbstractJcrNodeAdapter)item).getJcrItem();
                if (NodeUtil.hasMixin(node, NodeTypes.Deleted.NAME)) {
                    return ICON_TRASH;
                }

                NodeTypeDefinition nodeTypeDefinition = getNodeTypeDefinitionForNode(node);
                if (nodeTypeDefinition != null) {
                    return nodeTypeDefinition.getIcon();
                }
            }

        } catch (RepositoryException e) {
            log.warn("Unable to resolve icon", e);
            return null;
        }
        return null;
    }

    private NodeTypeDefinition getNodeTypeDefinitionForNode(Node node) throws RepositoryException {
        String primaryNodeTypeName = node.getPrimaryNodeType().getName();
        for (NodeTypeDefinition nodeTypeDefinition : workbenchDefinition.getNodeTypes()) {
            if (nodeTypeDefinition.isStrict()) {
                if (primaryNodeTypeName.equals(nodeTypeDefinition.getName())) {
                    return nodeTypeDefinition;
                }
            } else if (NodeUtil.isNodeType(node, nodeTypeDefinition.getName())) {
                return nodeTypeDefinition;
            }
        }
        return null;
    }
}

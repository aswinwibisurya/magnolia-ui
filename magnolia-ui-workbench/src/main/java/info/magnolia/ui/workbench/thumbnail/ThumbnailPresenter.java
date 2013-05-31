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
package info.magnolia.ui.workbench.thumbnail;

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.workbench.AbstractContentPresenter;
import info.magnolia.ui.workbench.ContentView;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.thumbnail.ThumbnailContainer.ThumbnailItem;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * The ThumbnailPresenter is responsible for creating, configuring and updating a thumbnail grid of items according to the workbench definition.
 */
public class ThumbnailPresenter extends AbstractContentPresenter implements ThumbnailView.Listener {

    private static final Logger log = LoggerFactory.getLogger(ThumbnailPresenter.class);

    private final ThumbnailView view;

    private final ImageProvider imageProvider;

    private ThumbnailContainer container;

    @Inject
    public ThumbnailPresenter(ThumbnailView view, ImageProvider imageProvider) {
        this.view = view;
        this.imageProvider = imageProvider;
    }

    @Override
    public ContentView start(WorkbenchDefinition workbench, EventBus eventBus) {
        super.start(workbench, eventBus);

        container = new ThumbnailContainer(workbench, imageProvider);
        container.setWorkspaceName(workbench.getWorkspace());
        container.setThumbnailHeight(73);
        container.setThumbnailWidth(73);
        container.refresh();

        view.setListener(this);
        view.setContainer(container);
        view.setThumbnailSize(73, 73);

        return view;
    }

    @Override
    public void setSelectedItemId(String itemId) {
        super.setSelectedItemId(itemId);
        view.select(itemId);
    }

    @Override
    public void refresh() {
        container.refresh();
        view.refresh();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void onItemSelection(Set items) {
        Set<Item> jcrItems = getJcrItemsByThumbnailItems(items);
        super.onItemSelection(jcrItems);
    }

    @Override
    public void onDoubleClick(Item item) {
        JcrItemAdapter jcrItem = getJcrItemByThumbnailItem(item);
        super.onDoubleClick(jcrItem);
    }

    @Override
    public void onRightClick(Item item, int clickX, int clickY) {
        JcrItemAdapter jcrItem = getJcrItemByThumbnailItem(item);
        super.onRightClick(jcrItem, clickX, clickY);
    }

    /**
     * Thumbnail container uses specific Thumbnail items, so we have to convert those into JcrItemAdapters.
     */
    private JcrItemAdapter getJcrItemByThumbnailItem(final Item item) {
        if (item instanceof ThumbnailItem) {
            String itemId = ((ThumbnailItem) item).getItemId();
            try {
                Session session = MgnlContext.getJCRSession(workbenchDefinition.getWorkspace());
                final Node imageNode = session.getNodeByIdentifier(itemId);
                return new JcrNodeAdapter(imageNode);
            } catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Thumbnail container uses specific Thumbnail items, so we have to convert those into JcrItemAdapters.
     */
    private Set<Item> getJcrItemsByThumbnailItems(final Set<Item> items) {
        Set<Item> jcrItems = new HashSet<Item>();
        for (Item item : items) {
            Item jcrItem = getJcrItemByThumbnailItem(item);
            if (jcrItem != null) {
                jcrItems.add(jcrItem);
            }
        }
        return jcrItems;
    }

}

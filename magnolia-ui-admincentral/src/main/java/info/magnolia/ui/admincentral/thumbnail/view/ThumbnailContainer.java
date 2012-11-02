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
package info.magnolia.ui.admincentral.thumbnail.view;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.ui.admincentral.thumbnail.view.ThumbnailContainer.ThumbnailItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import info.magnolia.ui.model.workbench.definition.WorkbenchDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * Container that provides thumbnails lazily.
 */
public class ThumbnailContainer extends AbstractInMemoryContainer<String, Resource, ThumbnailItem> implements Container.Ordered {

    private static final Logger log = LoggerFactory.getLogger(ThumbnailContainer.class);

    public static final String THUMBNAIL_PROPERTY_ID = "thumbnail";

    private WorkbenchDefinition workbenchDefinition;

    private String workspaceName = "";

    private int thumbnailWidth = 0;

    private int thumbnailHeight = 0;

    public ThumbnailContainer(WorkbenchDefinition workbenchDefinition) {
        super();
        this.workbenchDefinition = workbenchDefinition;
        getAllItemIds().addAll(getAllIdentifiers(workbenchDefinition.getWorkspace()));
    }

    @Override
    public Collection<String> getContainerPropertyIds() {
        return Arrays.asList(THUMBNAIL_PROPERTY_ID);
    }

    @Override
    public ThumbnailContainerProperty getContainerProperty(Object itemId, Object propertyId) {
        if (THUMBNAIL_PROPERTY_ID.equals(propertyId)) {
            return new ThumbnailContainerProperty(String.valueOf(itemId));
        }
        return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        if (THUMBNAIL_PROPERTY_ID.equals(propertyId)) {
            return Resource.class;
        }
        return null;
    }

    private String prepareJcrSQL2Query(){
        final String itemType = workbenchDefinition.getMainItemType().getItemType();
        return "select * from [" + itemType + "] as t order by name(t)";
    }

    /**
     * @return a List of JCR identifiers for all the nodes recursively found under <code>initialPath</code>. This method is called in {@link LazyThumbnailViewImpl#refresh()}.
     * You can override it, if you need a different strategy than the default one to fetch the identifiers of the nodes for which thumbnails need to be displayed.
     * @see info.magnolia.ui.vaadin.layout.LazyThumbnailLayout#refresh()
     */
    protected List<String> getAllIdentifiers(final String workspaceName) {
        List<String> uuids = new ArrayList<String>();
        final String query = prepareJcrSQL2Query();
        try {
            QueryManager qm = MgnlContext.getJCRSession(workspaceName).getWorkspace().getQueryManager();
            Query q = qm.createQuery(prepareJcrSQL2Query(), Query.JCR_SQL2);

            log.debug("Executing query statement [{}] on workspace [{}]", query, workspaceName);
            long start = System.currentTimeMillis();

            QueryResult queryResult = q.execute();
            NodeIterator iter = queryResult.getNodes();

            while(iter.hasNext()) {
                uuids.add(iter.nextNode().getIdentifier());
            }

            log.debug("Done collecting {} nodes in {}ms", uuids.size(), System.currentTimeMillis() - start);

        } catch (RepositoryException e) {
            throw new RuntimeRepositoryException(e);
        }
        return uuids;
    }


    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ThumbnailItem getUnfilteredItem(Object itemId) {
        return new ThumbnailItem(String.valueOf(itemId));
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    /**
     * ThumbnailContainer property.
     */
    public class ThumbnailContainerProperty extends AbstractProperty {

        private String resourcePath;

        public ThumbnailContainerProperty(final String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @Override
        public Resource getValue() {
            final String path = workbenchDefinition.getImageProvider().getThumbnailPathByIdentifier(getWorkspaceName(), resourcePath);
            return path == null ? null: new ExternalResource(path);
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
            this.resourcePath = String.valueOf(newValue);
        }

        @Override
        public Class<Resource> getType() {
            return Resource.class;
        }
    }

    /**
     * Thumbnail Item.
     */
    public class ThumbnailItem implements Item {

        private String id;

        public ThumbnailItem(final String id) {
            this.id = id;
        }

        @Override
        public Property getItemProperty(Object id) {
            if (THUMBNAIL_PROPERTY_ID.equals(id)) {
                return new ThumbnailContainerProperty(this.id);
            }
            return null;
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            return Arrays.asList(THUMBNAIL_PROPERTY_ID);
        }

        @Override
        public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}

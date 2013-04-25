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
package info.magnolia.ui.form.field.builder;

import info.magnolia.cms.core.SystemProperty;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.ui.form.field.definition.BasicUploadFieldDefinition;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.upload.basic.BasicFileItemWrapper;
import info.magnolia.ui.form.field.upload.basic.BasicUploadField;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.io.File;

import javax.inject.Inject;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Field;

/**
 * Creates and configures a Basic UploadField.
 */
public class BasicUploadFieldBuilder extends AbstractFieldBuilder<BasicUploadFieldDefinition, Byte[]> {

    private static final Logger log = LoggerFactory.getLogger(BasicUploadFieldBuilder.class);


    private MagnoliaConfigurationProperties properties;
    private final ImageProvider imageProvider;

    @Inject
    public BasicUploadFieldBuilder(BasicUploadFieldDefinition definition, Item relatedFieldItem, MagnoliaConfigurationProperties properties, ImageProvider imageProvider) {
        super(definition, relatedFieldItem);
        this.properties = properties;
        this.imageProvider = imageProvider;
    }

    @Override
    protected Field<Byte[]> buildField() {
        // Get or create the File Node adapter.
        JcrItemNodeAdapter binaryDataSubNodeItem = getOrCreateSubItemWithBinaryData();
        // Init the tmp upload path
        File tmpDirectory = new File(properties.getProperty(SystemProperty.MAGNOLIA_UPLOAD_TMPDIR));

        // Create the File Wrapper.
        BasicFileItemWrapper fileItem = new BasicFileItemWrapper(binaryDataSubNodeItem, tmpDirectory);

        // Create Upload Filed.
        BasicUploadField<BasicFileItemWrapper> uploadField = new BasicUploadField<BasicFileItemWrapper>(fileItem, tmpDirectory, imageProvider);

        uploadField.setMaxUploadSize(definition.getMaxUploadSize());
        uploadField.setAllowedMimeTypePattern(definition.getAllowedMimeTypePattern());
        setMessages(uploadField);

        return uploadField;
    }

    /**
     * Get or Create the Binary Item. If this Item doesn't exist yet,
     * initialize all fields (as Property).
     */
    public JcrItemNodeAdapter getOrCreateSubItemWithBinaryData() {
        // Get the related Node
        Node node = getRelatedNode(item);
        JcrItemNodeAdapter child = null;
        try {
            if (node.hasNode(definition.getBinaryNodeName()) && !(item instanceof JcrNewNodeAdapter)) {
                child = new JcrNodeAdapter(node.getNode(definition.getBinaryNodeName()));
                child.setParent((JcrItemNodeAdapter) item);
            } else {
                child = new JcrNewNodeAdapter(node, NodeTypes.Resource.NAME, definition.getBinaryNodeName());
                child.setParent((JcrItemNodeAdapter) item);
            }
        } catch (RepositoryException e) {
            log.error("Could get or create item", e);
        }
        return child;
    }

    /**
     * Do not link a DataSource to the Upload Field. Upload Field will handle
     * the creation of the appropriate property.
     */
    @Override
    public void setPropertyDataSource(Property property) {
    }

    @Override
    protected Class<?> getDefaultFieldType(FieldDefinition fieldDefinition) {
        return Binary.class;
    }

    /**
     * Configure Field based on the definition.
     */
    protected void setMessages(BasicUploadField<BasicFileItemWrapper> field) {
        field.setSelectNewCaption(definition.getSelectNewCaption());
        field.setSelectAnotherCaption(definition.getSelectAnotherCaption());
        field.setDropZoneCaption(definition.getDropZoneCaption());
        field.setInProgressCaption(definition.getInProgressCaption());
        field.setInProgressRatioCaption(definition.getInProgressRatioCaption());
        field.setFileDetailHeaderCaption(definition.getFileDetailHeaderCaption());
        field.setFileDetailNameCaption(definition.getFileDetailNameCaption());
        field.setFileDetailSizeCaption(definition.getFileDetailSizeCaption());
        field.setFileDetailFormatCaption(definition.getFileDetailFormatCaption());
        field.setFileDetailSourceCaption(definition.getFileDetailSourceCaption());
        field.setSuccessNoteCaption(definition.getSuccessNoteCaption());
        field.setWarningNoteCaption(definition.getWarningNoteCaption());
        field.setErrorNoteCaption(definition.getErrorNoteCaption());
        field.setDeteteCaption(definition.getDeleteCaption());
        field.setEditFileFormat(definition.isEditFileFormat());
        field.setEditFileName(definition.isEditFileName());
    }

}
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
package info.magnolia.ui.app.pages.field;


import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.assignment.TemplateDefinitionAssignment;
import info.magnolia.ui.admincentral.field.DialogSelectField;
import info.magnolia.ui.model.dialog.definition.FieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import com.vaadin.data.Item;

/**
 * Define a Template selector field.
 * The values displayed in the field are initialized based on the
 * related Item (Image of a JCR node) and {@link TemplateDefinitionAssignment}.
 */
public class TemplateSelectorField extends DialogSelectField {

    public TemplateSelectorField(FieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    /**
     * Get the Available templates based on the current Node.
     */
    @Override
    public Map<String, String>  getOptions() {
        TemplateDefinitionAssignment templateAssignment = Components.getComponent(TemplateDefinitionAssignment.class);
        Map<String, String> res = new HashMap<String, String>();

        Collection<TemplateDefinition> templates = templateAssignment.getAvailableTemplates(getRelatedNode(item));

        for(TemplateDefinition templateDefinition: templates) {
            res.put(templateDefinition.getId(), templateDefinition.getTitle());
        }
        return res;
    }

    /**
     * Return the field related node.
     * If field is of type JcrNewNodeAdapter then return the parent node.
     * Else get the node associated with the vaadim item.
     */
    private Node getRelatedNode(Item fieldRelatedItem) {
        if(fieldRelatedItem instanceof JcrNewNodeAdapter) {
            return ((JcrNewNodeAdapter)fieldRelatedItem).getParentNode();
        } else {
            return ((JcrNodeAdapter)fieldRelatedItem).getNode();
        }
    }

    @Override
    protected Class<?> getDefaultFieldType(FieldDefinition fieldDefinition) {
        return String.class;
    }

}

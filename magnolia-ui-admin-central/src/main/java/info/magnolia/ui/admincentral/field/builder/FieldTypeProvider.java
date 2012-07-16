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
package info.magnolia.ui.admincentral.field.builder;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.admincentral.content.view.builder.DefinitionToImplementationMapping;
import info.magnolia.ui.admincentral.field.DialogField;
import info.magnolia.ui.model.builder.FactoryBase;
import info.magnolia.ui.model.dialog.definition.FieldDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * FieldTypeProvider providing FieldType implementations using content to bean.
 *
 */
public class FieldTypeProvider extends FactoryBase<FieldDefinition, DialogField> implements Serializable {

    private List<DefinitionToImplementationMapping<FieldDefinition, DialogField>> definitionToImplementationMappings = new ArrayList<DefinitionToImplementationMapping<FieldDefinition, DialogField>>();

    @Inject
    public FieldTypeProvider(ComponentProvider componentProvider) {
        super(componentProvider);
    }
    //Looks like this is needed by proxy
    public FieldTypeProvider() {
        super(null);
    }

    public List<DefinitionToImplementationMapping<FieldDefinition, DialogField>> getDefinitionToImplementationMappings() {
        return this.definitionToImplementationMappings;
    }

    public void setDefinitionToImplementationMappings(List<DefinitionToImplementationMapping<FieldDefinition, DialogField>> definitionToImplementationMappings) {
        this.definitionToImplementationMappings = definitionToImplementationMappings;
        for (DefinitionToImplementationMapping<FieldDefinition, DialogField> definitionToImplementationMapping : definitionToImplementationMappings) {
            addDefinitionToImplementationMapping(definitionToImplementationMapping);
        }
    }

    public void addDefinitionToImplementationMapping(DefinitionToImplementationMapping<FieldDefinition, DialogField> mapping) {
        addMapping(mapping.getDefinition(), mapping.getImplementation());
    }

    @Override
    public DialogField create(FieldDefinition fieldTypeDefinition, Object... parameters) {
        return super.create(fieldTypeDefinition, parameters);
    }
}

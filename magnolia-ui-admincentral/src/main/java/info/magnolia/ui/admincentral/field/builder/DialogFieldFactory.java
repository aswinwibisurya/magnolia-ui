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

import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.admincentral.field.FieldBuilder;
import info.magnolia.ui.admincentral.field.validator.builder.ValidatorFieldFactory;
import info.magnolia.ui.model.builder.DefinitionToImplementationMapping;
import info.magnolia.ui.model.builder.FactoryBase;
import info.magnolia.ui.model.field.definition.FieldDefinition;

import java.io.Serializable;

import javax.inject.Inject;

import com.vaadin.data.Item;

/**
 * Factory for creating DialogField instances using an internal set of mappings connecting a {@link FieldDefinition}
 * class with a {@link FieldBuilder} class.
 *
 * @see FieldDefinition
 * @see FieldBuilder
 */
public class DialogFieldFactory extends FactoryBase<FieldDefinition, FieldBuilder> implements Serializable {
    
    private ValidatorFieldFactory validatorFieldFactory;
    private I18nContentSupport i18nContentSupport;

    @Inject
    public DialogFieldFactory(ComponentProvider componentProvider, DialogFieldRegistry dialogFieldRegistery, ValidatorFieldFactory validatorFieldFactory, I18nContentSupport i18nContentSupport) {
        super(componentProvider);
        this.validatorFieldFactory = validatorFieldFactory;
        this.i18nContentSupport = i18nContentSupport;
        for (DefinitionToImplementationMapping<FieldDefinition, FieldBuilder> definitionToImplementationMapping : dialogFieldRegistery.getDefinitionToImplementationMappings()) {
            addMapping(definitionToImplementationMapping.getDefinition(), definitionToImplementationMapping.getImplementation());
        }
    }

    public FieldBuilder create(FieldDefinition definition, Item item, Object... parameters) {
        FieldBuilder fieldBuilder = super.create(definition, item, parameters);
        fieldBuilder.setValidatorFieldFactory(validatorFieldFactory);
        fieldBuilder.setI18nContentSupport(i18nContentSupport);
        return fieldBuilder;
    }
}

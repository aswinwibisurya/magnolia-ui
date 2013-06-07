/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.ui.form.validator.registry;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.MgnlInstantiationException;
import info.magnolia.ui.form.validator.builder.FieldValidatorBuilder;
import info.magnolia.ui.form.validator.definition.FieldValidatorDefinition;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating validator instances using an internal set of mappings connecting a {@link info.magnolia.ui.form.validator.definition.FieldValidatorDefinition} class with a {@link info.magnolia.ui.form.validator.builder.FieldValidatorBuilder} class.
 *
 * @see info.magnolia.ui.form.validator.definition.FieldValidatorDefinition
 */
public class FieldValidatorFactory implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(FieldValidatorFactory.class);

    protected ComponentProvider componentProvider;

    public FieldValidatorFactory(ComponentProvider componentProvider) {
        this.componentProvider = componentProvider;
    }

    public FieldValidatorBuilder createFieldValidatorBuilder(FieldValidatorDefinition definition, Object... args) {

        Class<? extends FieldValidatorBuilder> implementationClass = definition.getBuilder();
        if (implementationClass == null) {
            log.error("No implementation class set for validator: " + definition.getClass().getName());
            return null;
        }

        Object[] combinedParameters = new Object[args.length + 1];
        combinedParameters[0] = definition;
        System.arraycopy(args, 0, combinedParameters, 1, args.length);

        try {
            return componentProvider.newInstance(implementationClass, combinedParameters);
        } catch (MgnlInstantiationException e) {
            log.error("Could not instantiate validator builder class for validator: " + definition.getClass().getName(), e);
            return null;
        }
    }
}

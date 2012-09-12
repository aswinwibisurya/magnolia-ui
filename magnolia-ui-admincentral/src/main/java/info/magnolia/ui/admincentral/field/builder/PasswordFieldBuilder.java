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

import info.magnolia.ui.admincentral.field.PasswordFields;
import info.magnolia.ui.model.field.definition.FieldDefinition;
import info.magnolia.ui.model.field.definition.PasswordFieldDefinition;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;

/**
 * Creates and initializes an password field based on a field definition.
 */
public class PasswordFieldBuilder extends AbstractFieldBuilder<PasswordFieldDefinition> {


    public PasswordFieldBuilder(PasswordFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    @Override
    protected Field buildField() {
        String verificationErrorMessage = StringUtils.EMPTY;
        String verificationMessage = StringUtils.EMPTY;
        if(definition.isVerification()) {
            verificationErrorMessage = getMessage(definition.getVerificationErrorMessage());
            verificationMessage = getMessage(definition.getVerificationMessage());
        }
        // Create Field
        return new PasswordFields(definition.isVerification(), verificationMessage, verificationErrorMessage);
    }


    @Override
    protected Class<?> getDefaultFieldType(FieldDefinition fieldDefinition) {
        return String.class;
    }
}


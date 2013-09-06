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
package info.magnolia.ui.form.field.transformer.basic;

import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Specific TwinField {@link info.magnolia.ui.form.field.transformer.Transformer}.<br>
 * 
 * @param <T>
 */
public class TwinSelectPropertyTransformer<T> extends BasicTransformer<T> {

    public TwinSelectPropertyTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<T> type) {
        super(relatedFormItem, definition, type);
    }

    @Override
    public void writeToItem(T newValue) {
        Property<T> p = getOrCreateProperty(type);
        if (p.getValue() instanceof Set && newValue instanceof List) {
            newValue = (T) new HashSet((List) newValue);
        } else if (p.getValue() instanceof List && newValue instanceof Set) {
            newValue = (T) new LinkedList((Set) newValue);
        }
        p.setValue(newValue);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public T readFromItem() {
        T value = super.readFromItem();

        if (value == null) {
            return (T) new HashSet();
        } else if (value instanceof List) {
            return (T) new HashSet((List) value);
        } else {
            return null;
        }
    }
}

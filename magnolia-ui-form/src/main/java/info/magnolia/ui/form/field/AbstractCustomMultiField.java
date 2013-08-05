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
package info.magnolia.ui.form.field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HasComponents;

/**
 * Abstract implementation of {@link CustomField} used for multi fields components.<br>
 * It expose generic methods allowing to <br>
 * - Retrieve the a list of Fields contained into the main component <br>
 * - Override Validate and get Error Message in order to include these call to the embedded Fields.<br>
 * 
 * @param <T>.
 */
public abstract class AbstractCustomMultiField<T> extends CustomField<T>{


    /**
     * Utility method that return a list of Fields embedded into a root custom field.
     * 
     * @param root
     * @param onlyValid if set to true, return only the isValid() fields.
     */
    @SuppressWarnings("unchecked")
    protected List<AbstractField<T>> getFields(HasComponents root, boolean onlyValid) {
        Iterator<Component> it = root.iterator();
        List<AbstractField<T>> fields = new ArrayList<AbstractField<T>>();
        while (it.hasNext()) {
            Component c = it.next();
            if (c instanceof AbstractField) {
                if (!onlyValid || (onlyValid && ((AbstractField<T>) c).isValid())) {
                    fields.add((AbstractField<T>) c);
                }
            } else if (c instanceof HasComponents) {
                fields.addAll(getFields((HasComponents) c, onlyValid));
            }
        }
        return fields;
    }

    /**
     * Validate all fields from the root container.
     */
    @Override
    public boolean isValid() {
        boolean isValid = true;
        List<AbstractField<T>> fields = getFields(this, false);
        for (AbstractField<T> field : fields) {
            isValid = field.isValid();
            if (!isValid) {
                return isValid;
            }
        }
        return isValid;
    }

    /**
     * Get the error message.
     */
    @Override
    public ErrorMessage getErrorMessage() {
        ErrorMessage errorMessage = null;
        List<AbstractField<T>> fields = getFields(this, false);
        for (AbstractField<T> field : fields) {
            errorMessage = field.getErrorMessage();
            if (errorMessage != null) {
                return errorMessage;
            }
        }
        return errorMessage;
    }

    @Override
    protected boolean isEmpty() {
        boolean isEmpty = false;
        List<AbstractField<T>> fields = getFields(this, false);
        for (AbstractField<T> field : fields) {
            isEmpty = field.getValue() == null;
            if (isEmpty) {
                return isEmpty;
            }
        }
        return isEmpty;
    }

}

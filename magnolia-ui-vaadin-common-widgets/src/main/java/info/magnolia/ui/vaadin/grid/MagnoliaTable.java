/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.ui.vaadin.grid;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

/**
 * MagnoliaTable.
 */
public class MagnoliaTable extends Table {

    public MagnoliaTable(Container dataSource) {
        super(null, dataSource);
        addStyleName("v-magnolia-table");
    }

    public MagnoliaTable() {
        this(new IndexedContainer());
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
        String result = super.formatPropertyValue(rowId, colId, property);
        return StringEscapeUtils.escapeHtml4(result);
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey("selectAll")) {
            boolean selectAll = (Boolean) variables.get("selectAll");
            if (selectAll) {
                Collection<?> ids = getItemIds();
                for (final Object id : ids) {
                    select(id);
                }
            } else {
                setValue(null);
            }
        }

        if (variables.containsKey("toggleSelection")) {
            boolean selected = (Boolean) variables.get("toggleSelection");
            String key = String.valueOf(variables.get("toggledRowId"));
            final Object id = itemIdMapper.get(key);
            if (selected) {
                select(id);
            } else {
                unselect(id);
            }
        }
    }
}

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
package info.magnolia.ui.vaadin.integration.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import org.junit.Test;

public class DefaultPropertyTest {

    @Test
    public void testGetValue() throws Exception {
        // GIVEN
        final String value = "value";
        final DefaultProperty property = new DefaultProperty("propertyName", value);

        // WHEN
        final Object result = property.getValue();

        // THEN
        assertEquals(value, result);
    }

    @Test
    public void testCreateDefaultPropertyNullValueWithType() throws Exception {
        // GIVEN
        final DefaultProperty property = new DefaultProperty("propertyName", null, String.class);

        // WHEN
        final Class<?> result = property.getType();

        //THEN
        assertEquals(property.getType(), result);
        assertNull(property.getValue());
        assertEquals(property.toString(), "");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCreateDefaultPropertyNullValueWithoutType() {
        new DefaultProperty("propertyName", null);
    }

    @Test
    public void testGetType() throws Exception {
        // GIVEN
        final String value = "value";
        final DefaultProperty property = new DefaultProperty("propertyName", value);

        // WHEN
        final Class<?> result = property.getType();

        // THEN
        assertEquals(value.getClass(), result);
    }

    @Test
    public void testSetValue() throws Exception {
        // GIVEN
        final String value = "old";
        final DefaultProperty property = new DefaultProperty("propertyName", value);
        final String newValue = "new";

        // WHEN
        property.setValue(newValue);

        // THEN
        assertEquals(newValue, property.getValue());
    }

    @Test
    public void testSetReadOnlyValue() throws Exception {
        // GIVEN
        final String value = "old";
        final DefaultProperty property = new DefaultProperty("propertyName", value);
        property.setReadOnly(true);

        // WHEN
        try {
            property.setValue("new");
        }
        catch(Property.ReadOnlyException e) {
            // ignore
        }

        // THEN
        assertEquals(true, property.isReadOnly());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testSetNonAssignableValue() throws Exception {
        // GIVEN
        final String value = "old";
        final DefaultProperty property = new DefaultProperty("propertyName", value);

        // WHEN
        try {
            property.setValue(12);
        }
        catch(Converter.ConversionException e) {
            // ignore
        }

        // THEN
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGenericDefaultProperty() throws Exception {
        // GIVEN
        final List<String> value = new ArrayList<String>();

        // WHEN
        final DefaultProperty<List> property = new DefaultProperty<List>("propertyName", value);


        // THEN
        assertEquals(List.class, property.getType());
    }

    @Test(expected = Converter.ConversionException.class)
    public void testSetNonAssignableValueException() {
        // GIVEN
        final String value = "old";
        final DefaultProperty property = new DefaultProperty("propertyName", value, String.class);

        // WHEN
        property.setValue(1);
    }

    @Test(expected = Property.ReadOnlyException.class)
    public void testReadOnlyExceptionOnSet() {
        // GIVEN
        final String value = "old";
        final DefaultProperty property = new DefaultProperty("propertyName", value);
        property.setReadOnly(true);

        // WHEN
        property.setValue("new");
    }

}

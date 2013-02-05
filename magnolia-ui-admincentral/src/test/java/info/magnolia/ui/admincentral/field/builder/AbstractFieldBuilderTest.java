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
package info.magnolia.ui.admincentral.field.builder;

import static org.junit.Assert.*;

import info.magnolia.objectfactory.Components;
import info.magnolia.ui.model.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.model.field.definition.FieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.jcr.Node;
import javax.jcr.PropertyType;

import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * Main testcase for {@link AbstractFieldBuilder}.
 */
public class AbstractFieldBuilderTest extends AbstractBuilderTest<ConfiguredFieldDefinition> {

    private AbstractFieldBuilder<FieldDefinition, Object> abstractDialogField;

    @Test
    public void simpleInitializationTest() {
        // GIVEN
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        // WHEN
        Field<Object> field = abstractDialogField.getField();
        // THEN
        assertTrue(TextField.class.isAssignableFrom(field.getClass()));
        assertEquals(definition, abstractDialogField.getFieldDefinition());
        assertEquals(false, field.isRequired());
        assertEquals("label", field.getCaption());
        assertEquals(false, field.getPropertyDataSource().isReadOnly());
        assertEquals(true, field.getPropertyDataSource() instanceof DefaultProperty);
        assertEquals(propertyName, ((DefaultProperty) field.getPropertyDataSource()).getPropertyName());
    }

    @Test
    public void changePropertyValueTest() throws Exception {
        // GIVEN
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field<Object> field = abstractDialogField.getField();

        // WHEN
        field.setValue("new Value");

        // THEN
        Node res = ((JcrNodeAdapter) baseItem).getNode();
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals("new Value", res.getProperty(propertyName).getString());
        assertEquals(PropertyType.STRING, res.getProperty(propertyName).getType());
        Property p = baseItem.getItemProperty(propertyName);
        assertEquals(field.getPropertyDataSource(), p);
        assertEquals("new Value", p.getValue().toString());
        assertEquals(String.class, p.getValue().getClass());
    }

    @Test
    public void testPropertyValueChangeWithSaveInfoTrue() throws Exception {
        // GIVEN
        baseNode.setProperty(propertyName, "value");
        baseItem = new JcrNodeAdapter(baseNode);
        // Set do not change
        definition.setReadOnly(false);
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field<Object> field = abstractDialogField.getField();

        // WHEN
        field.setValue("new Value");

        // THEN
        Node res = ((JcrNodeAdapter) baseItem).getNode();
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals("new Value", res.getProperty(propertyName).getString());
        Property p = baseItem.getItemProperty(propertyName);
        assertEquals("new Value", p.getValue().toString());

    }

    @Test
    public void testConversionOfPropertyTypeWithDouble() throws Exception {
        // GIVEN
        // Set property Type
        definition.setType("Double");
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field<Object> field = abstractDialogField.getField();

        // WHEN
        field.setValue("21.98");

        // THEN
        Node res = ((JcrNodeAdapter) baseItem).getNode();
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals(PropertyType.DOUBLE, res.getProperty(propertyName).getType());
        assertEquals(Double.parseDouble("21.98"), res.getProperty(propertyName).getDouble(), 0);

        Property p = baseItem.getItemProperty(propertyName);
        assertEquals("21.98", p.getValue().toString());
        assertEquals(Double.class, p.getValue().getClass());
    }

    @Ignore
    @Test
    public void simpleI18NTest() {
        // GIVEN
        definition.setLabel("message.label");
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);

        // WHEN
        Field<Object> field = abstractDialogField.getField();
        // THEN

        assertEquals("label", field.getCaption());

    }

    @Test
    public void requiredFieldIsMarkedByAsteriskTest() {
        // GIVEN
        definition.setRequired(true);
        abstractDialogField = new TestTextFieldBuilder(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);

        // WHEN
        Field<Object> field = abstractDialogField.getField();
        field.setRequired(definition.isRequired());

        // THEN
        assertEquals(true, field.isRequired());
        assertTrue(field.getCaption().contains("*"));
    }

    public static ConfiguredFieldDefinition createConfiguredFieldDefinition(ConfiguredFieldDefinition configureFieldDefinition, String propertyName) {
        configureFieldDefinition.setDescription("description");
        configureFieldDefinition.setI18nBasename("i18nBasename");
        configureFieldDefinition.setLabel("label");
        configureFieldDefinition.setName(propertyName);
        configureFieldDefinition.setRequired(false);
        configureFieldDefinition.setReadOnly(false);
        configureFieldDefinition.setType("String");
        return configureFieldDefinition;
    }

    /**
     * Simplified test implementation of a {@link TextFieldBuilder}.
     */
    public static class TestTextFieldBuilder extends AbstractFieldBuilder<FieldDefinition, Object> {

        public TestTextFieldBuilder(FieldDefinition definition, Item relatedFieldItem) {
            super(definition, relatedFieldItem);
        }

        @Override
        protected Field buildField() {
            return new TestTextField();
        }

        /**
         * {@link com.vaadin.data.util.converter.ConverterFactory} is bound to the {@link VaadinSession}. To get The default converters to work we need to mock the VaadinSession.
         *
         * @see AbstractBuilderTest where we add the {@link com.vaadin.data.util.converter.DefaultConverterFactory} to the {@link info.magnolia.objectfactory.ComponentProvider}.
         */
        private class TestTextField extends TextField {

            @Override
            protected VaadinSession getSession() {
                return Components.getComponentProvider().getComponent(VaadinSession.class);
            }
        }
    }

    @Override
    protected void createConfiguredFieldDefinition() {
        ConfiguredFieldDefinition configureFieldDefinition = new ConfiguredFieldDefinition();
        this.definition = createConfiguredFieldDefinition(configureFieldDefinition, propertyName);
    }

}
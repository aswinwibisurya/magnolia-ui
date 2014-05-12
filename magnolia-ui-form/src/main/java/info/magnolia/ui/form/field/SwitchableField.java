/**
 * This file Copyright (c) 2013-2014 Magnolia International
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

import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;

import java.util.Iterator;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Switchable field.<br>
 * Display a field composed of two main sections <br>
 * - a Select Section (list or checkBox) <br>
 * - a Field Section displaying the field currently selected. <br>
 */
public class SwitchableField extends AbstractCustomMultiField<SwitchableFieldDefinition, PropertysetItem> {
    private static final Logger log = LoggerFactory.getLogger(SwitchableField.class);


    public SwitchableField(SwitchableFieldDefinition definition, FieldFactoryFactory fieldFactoryFactory, I18nContentSupport i18nContentSupport, ComponentProvider componentProvider, Item relatedFieldItem) {
        super(definition, fieldFactoryFactory, i18nContentSupport, componentProvider, relatedFieldItem);
    }

    @Override
    protected Component initContent() {
        // Initialize root
        root = new VerticalLayout();
        setWidth(100, Unit.PERCENTAGE);
        setHeight(-1, Unit.PIXELS);
        addStyleName("switchablefield");
        root.setWidth(100, Unit.PERCENTAGE);
        root.setHeight(-1, Unit.PIXELS);
        root.setSpacing(true);

        // Initialize Existing field
        initFields();
        return root;
    }

    @Override
    protected void initFields(PropertysetItem fieldValues) {
        root.removeAllComponents();

        // Create all Fields including the select Field.
        for (ConfiguredFieldDefinition fieldDefinition : definition.getFields()) {
            String name = fieldDefinition.getName();
            Field<?> field = createLocalField(fieldDefinition, fieldValues.getItemProperty(fieldDefinition.getName()), false);
            if (fieldValues.getItemProperty(fieldDefinition.getName()) == null) {
                fieldValues.addItemProperty(fieldDefinition.getName(), field.getPropertyDataSource());
            }
            field.setWidth(100, Unit.PERCENTAGE);
            field.setId(name);
            // set select field at the first position
            if (StringUtils.equals(fieldDefinition.getName(), definition.getName())) {
                root.addComponentAsFirst(field);
            }else {
                root.addComponent(field);
            }

        }

        // add listener to the select field
        Field<?> selectField = (Field<?>) root.getComponent(0);
        selectField.addValueChangeListener(createSelectValueChangeListener());
        selectField.addValueChangeListener(selectionListener);
        selectField.setCaption("");
        switchField((String) selectField.getValue());
    }


    /**
     * Change Listener bound to the select field. Once a selection is done, <br>
     * the value change listener will switch to the field linked to the current select value.
     */
    private ValueChangeListener createSelectValueChangeListener() {
        ValueChangeListener listener;
        listener = new ValueChangeListener() {

            @Override
            public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                final String valueString = String.valueOf(event.getProperty().getValue());
                switchField(valueString);
            }

        };
        return listener;
    }

    /**
     * Switch to the desired field. It the field is not part of the List, display a warn label.
     */
    private void switchField(String fieldName) {
        // Check
        if (root.getComponentCount() < 2 && StringUtils.equals(((Field<?>) root.getComponent(0)).getId(), definition.getName())) {
            log.warn("{} is not associated to a field. Nothing will be displayed.", fieldName);
            root.addComponent(new Label("No field configured for this switchable field "), 1);
            return;
        }

        Iterator<Component> iterator = root.iterator();
        while (iterator.hasNext()) {
            Field<?> field = (Field<?>) iterator.next();
            // Set the select component visible and the selected field
            if (StringUtils.equals(field.getId(), fieldName) || StringUtils.equals(field.getId(), definition.getName())) {
                field.setVisible(true);
            } else {
                field.setVisible(false);
            }
        }
    }

    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }

}

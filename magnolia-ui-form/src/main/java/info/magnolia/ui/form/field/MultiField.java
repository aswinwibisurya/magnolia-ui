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

import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.MultiFieldDefinition;
import info.magnolia.ui.form.field.factory.FieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;
import info.magnolia.ui.vaadin.integration.NullItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * .
 *
 * @param <T>
 */
public class MultiField<T> extends CustomField<List<T>> {
    private static final Logger log = LoggerFactory.getLogger(MultiField.class);

    private VerticalLayout root;
    private final Button addButton = new NativeButton();

    private final FieldFactoryFactory fieldFactoryFactory;
    private final I18nContentSupport i18nContentSupport;
    private final ComponentProvider componentProvider;
    private final MultiFieldDefinition definition;

    private final ConfiguredFieldDefinition fieldDefinition;
    private String buttonCaptionAdd;
    private String buttonCaptionRemove;

    public MultiField(MultiFieldDefinition definition, FieldFactoryFactory fieldFactoryFactory, I18nContentSupport i18nContentSupport, ComponentProvider componentProvider) {
        this.definition = definition;
        this.fieldDefinition = definition.getField();
        this.fieldFactoryFactory = fieldFactoryFactory;
        this.componentProvider = componentProvider;
        this.i18nContentSupport = i18nContentSupport;
    }

    @Override
    protected Component initContent() {
        // Init root layout
        addStyleName("linkfield");
        root = new VerticalLayout();
        root.setSizeUndefined();

        // Initialize Existing field
        initFields();

        // Add addButton
        addButton.setCaption(buttonCaptionAdd);
        addButton.addStyleName("magnoliabutton");
        addButton.addClickListener(addButtonClickListener());
        root.addComponent(addButton);

        return root;
    }

    /**
     * Create a single element.<br>
     * This single element is composed of:<br>
     * - a configured field <br>
     * - a remove Button<br>
     */
    @SuppressWarnings("unchecked")
    private Component createEntryComponent(T entry) {
        HorizontalLayout layout = new HorizontalLayout();
        Field<T> field = createLocalField();
        layout.addComponent(field);
        if (entry != null) {
            field.getPropertyDataSource().setValue(entry);
        }
        field.addValueChangeListener(selectionListener);

        // Delete Button
        Button deleteButton = new Button();
        deleteButton.setHtmlContentAllowed(true);
        deleteButton.setCaption("<span class=\"" + "icon-trash" + "\"></span>");
        deleteButton.addStyleName("remove");
        deleteButton.setDescription(buttonCaptionRemove);
        deleteButton.addClickListener(removeButtonClickListener(layout));
        layout.addComponent(deleteButton);

        return layout;
    }

    /**
     * Create a button listener bound to the delete Button.
     */
    private Button.ClickListener removeButtonClickListener(final HorizontalLayout layout) {
        return new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                root.removeComponent(layout);
                setValue(getCurrentValues(root));
            };
        };
    }

    /**
     * Create a button listener bound to the add Button.
     */
    private Button.ClickListener addButtonClickListener() {
        return new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                root.addComponent(createEntryComponent(null), root.getComponentCount() - 1);
            };
        };
    }

    /**
     * Listener used to update the Data source property.
     */
    private Property.ValueChangeListener selectionListener = new ValueChangeListener() {
        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
            List<T> currentValues = getCurrentValues(root);
            setValue(currentValues);
        }
    };

    /**
     * Initialize the MultiField. <br>
     * Create as many configured Field as we have related values already stored.
     */
    private void initFields() {
        List<T> newValue = (List<T>) getPropertyDataSource().getValue();
        List<T> currentValues = getCurrentValues(root);
        Iterator<T> it = newValue.iterator();
        while (it.hasNext()) {
            T entry = it.next();
            if (!currentValues.contains(entry)) {
                root.addComponent(createEntryComponent(entry));
            }
        }
    };

    /**
     * Retrieve the Values stored as Field value.
     */
    private List<T> getCurrentValues(HasComponents root) {
        Iterator<Component> it = root.iterator();
        List<T> newValue = new ArrayList<T>();
        while (it.hasNext()) {
            Component c = it.next();
            if (c instanceof AbstractField) {
                newValue.add((T) ((AbstractField<?>) c).getConvertedValue());
            } else if (c instanceof HasComponents) {
                newValue.addAll(getCurrentValues((HasComponents) c));
            }
        }
        return newValue;
    }

    /**
     * Create a Configured Field that has to be added to the multiField.<br>
     * As we do not want that the Configured field is bound to an existing Item but <br>
     * rather to the custom property initialize by the factory, this Configured field is bound to <br>
     * a <b>{@link NullItem}</b><br>
     * .
     */
    private Field<T> createLocalField() {
        NullItem item = new NullItem();
        FieldFactory fieldfactory = fieldFactoryFactory.createFieldFactory(fieldDefinition, item);
        fieldfactory.setComponentProvider(componentProvider);
        fieldfactory.setI18nContentSupport(i18nContentSupport);
        Field<?> field = fieldfactory.createField();
        field.setCaption(null);
        return (Field<T>) field;
    }

    @Override
    public Class getType() {
        return List.class;
    }


    /**
     * Caption section.
     */
    public void setButtonCaptionAdd(String buttonCaptionAdd) {
        this.buttonCaptionAdd = buttonCaptionAdd;
    }

    public void setButtonCaptionRemove(String buttonCaptionRemove) {
        this.buttonCaptionRemove = buttonCaptionRemove;
    }
}

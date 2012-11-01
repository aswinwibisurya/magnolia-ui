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
package info.magnolia.ui.admincentral.field;

import org.apache.commons.lang.StringUtils;
import org.vaadin.addon.customfield.CustomField;
import org.vaadin.addon.propertytranslator.PropertyTranslator;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;


/**
 * A base custom field comprising a text field and a button placed to its immediate right.
 * A {@link PropertyTranslator} can be set in order to have a different display and property stored.
 * For example, display can be the Item path and value stored is the identifier of the Item.
 */
public class TextAndButtonField extends CustomField {

    private final Button selectButton;
    
    private final TextField textField;
    
    private final PropertyTranslator translator;
    
    private final String buttonCaptionNew;
    
    private final String buttonCaptionOther;

    public TextAndButtonField(PropertyTranslator translator, String buttonCaptionNew, String buttonCaptionOther) {
        this.translator = translator;
        this.buttonCaptionNew = buttonCaptionNew;
        this.buttonCaptionOther = buttonCaptionOther;
        
        textField = new TextField();
        textField.setSizeUndefined();
        textField.addStyleName("small-textfield");
        textField.setImmediate(true);
        
        selectButton = new NativeButton();
        selectButton.addStyleName("btn-dialog btn-dialog-select");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.addComponent(textField);
        layout.addComponent(selectButton);
        layout.setComponentAlignment(selectButton, Alignment.MIDDLE_CENTER);
        
        setCompositionRoot(layout);
    }

    public TextField getTextField() {
        return this.textField;
    }

    public Button getSelectButton() {
        return this.selectButton;
    }


    @Override
    public Object getValue() {
        return textField.getValue();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
        textField.setValue(newValue);
        setButtonCaption(newValue.toString());
    }

    /**
     * Set propertyDatasource.
     * If the translator is not null, set it as datasource.
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {
        if(translator!=null) {
            translator.setPropertyDataSource(newDataSource);
            textField.setPropertyDataSource(translator);
        } else {
            textField.setPropertyDataSource(newDataSource);
        }
        setButtonCaption(newDataSource.getValue().toString());
    }

    @Override
    public Property getPropertyDataSource() {
        if(translator!=null) {
            return translator.getPropertyDataSource();
        } else {
            return textField.getPropertyDataSource();
        }
    }

    @Override
    public Class<?> getType() {
        return getPropertyDataSource().getType();
    }

    private void setButtonCaption(String value) {
        if(StringUtils.isNotBlank(value)) {
            selectButton.setCaption(buttonCaptionOther);
            selectButton.setDescription(buttonCaptionOther);
        }else {
            selectButton.setCaption(buttonCaptionNew);
            selectButton.setDescription(buttonCaptionNew);
        }
    }

}

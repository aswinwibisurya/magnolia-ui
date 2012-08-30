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
package info.magnolia.ui.widget.dialog.gwt.client.dialoglayout;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * Wrapper widget that provides help and error indication.
 */
public class DialogFieldWrapper extends FlowPanel implements HasFocusHandlers {

    private Element label = DOM.createDiv();

    private Element fieldWrapper = DOM.createDiv();

    private Element root;

    private Button helpButton = new Button();

    private Button errorAction = new Button();

    private VInlineMessage errorSection = null;

    private VInlineMessage helpSection = null;

    private String helpDescription = null;

    private Widget field = null;

    public DialogFieldWrapper() {
        super();
        addStyleName("v-dialog-field-section");
        root = super.getElement();
        construct();
        setHelpEnabled(false);

        helpButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (helpSection == null) {
                    showHelp();
                } else {
                    hideHelp();
                }
            }
        });
    }

    protected void hideHelp() {
        remove(helpSection);
        helpSection = null;
    }

    protected void showHelp() {
        helpSection = VInlineMessage.createHelpMessage();
        helpSection.setMessage(helpDescription);
        add(helpSection, root);
    }

    private void construct() {
        label.addClassName("v-dialog-field-label");
        fieldWrapper.addClassName("v-dialog-field-container");
        helpButton.addStyleName("action-dialog-help");
        errorAction.addStyleName("action-validation");

        fieldWrapper.appendChild(label);
        root.appendChild(fieldWrapper);
        add(helpButton, fieldWrapper);
        add(errorAction, fieldWrapper);
    }

    public void showError(final String errorDescription) {
        errorAction.setVisible(true);
        fieldWrapper.addClassName("validation-hilight");
        if (errorSection == null) {
            errorSection = VInlineMessage.createErrorMessage();
        }
        errorSection.addMessage(errorDescription);
        add(errorSection, root);
    }

    public void setCaption(String caption) {
        label.setInnerHTML(caption);
    }

    public int getFieldAreaWidth() {
        return fieldWrapper.getOffsetWidth();
    }

    public int getFieldAreaHeight() {
        return fieldWrapper.getOffsetHeight();
    }

    @Override
    public void add(Widget child) {
        add(child, fieldWrapper);
    }

    public void setField(Widget child) {
        if (this.field != null) {
            remove(field);
        }
        this.field = child;
        if (child != null) {
            child.removeFromParent();
            getChildren().add(child);
            fieldWrapper.insertBefore(child.getElement(), helpButton.getElement());
            adopt(child);
        }
    }

    public void clearErrors() {
        if (errorSection != null) {
            remove(errorSection);
            errorSection = null;
        }
        fieldWrapper.removeClassName("validation-hilight");
        errorAction.setVisible(false);
    }

    public void setHelpEnabled(boolean isHelpEnabled) {
        helpButton.setVisible(isHelpEnabled && helpDescription != null);
        if (!isHelpEnabled && helpSection != null) {
            hideHelp();
        }
    }

    public void setHelpDescription(String description) {
        this.helpDescription = description;
        if (helpSection != null && getWidgetIndex(helpSection) >= 0) {
            helpSection.setMessage(helpDescription);
        }
    }

    public boolean hasError() {
        return errorSection != null && errorSection.isVisible();
    }

    public void focusField() {
        if (field != null) {
            FocusImpl.getFocusImplForWidget().focus(field.getElement());
        }
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return field.addDomHandler(handler, FocusEvent.getType());
    }

    public void resetErrorMessage() {
        if (errorSection != null) {
            errorSection.setMessage("");
        }
    }

    protected Element getLabel() {
        return label;
    }

}

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
package info.magnolia.ui.admincentral.shellapp.favorites;

import info.magnolia.ui.api.ModelConstants;
import info.magnolia.ui.framework.AdmincentralNodeTypes;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemNodeAdapter;

import org.apache.commons.lang.StringUtils;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;

/**
 * FavoritesEntry.
 */
public class FavoritesEntry extends CustomComponent {

    private HorizontalLayout root = new HorizontalLayout();
    private String location;
    private String icon;
    private String title;
    private String group;
    private String relPath;
    private TextField titleField;
    private NativeButton editButton;
    private NativeButton removeButton;
    private boolean editable;
    private boolean selected;

    public FavoritesEntry(final JcrItemNodeAdapter favorite, final FavoritesView.Listener listener) {
        super();
        String nodeName = favorite.getItemProperty(ModelConstants.JCR_NAME).getValue().toString();
        this.location = favorite.getItemProperty(AdmincentralNodeTypes.Favorite.URL).getValue().toString();
        this.title = favorite.getItemProperty(AdmincentralNodeTypes.Favorite.TITLE).getValue().toString();
        this.group = "";
        this.relPath = nodeName;

        if (favorite.getItemProperty(AdmincentralNodeTypes.Favorite.GROUP) != null) {
            this.group = favorite.getItemProperty(AdmincentralNodeTypes.Favorite.GROUP).getValue().toString();
        }
        if (StringUtils.isNotBlank(this.group)) {
            this.relPath = this.group + "/" + nodeName;
        }

        String icon = "icon-app";
        if (favorite.getItemProperty(AdmincentralNodeTypes.Favorite.ICON).getValue() != null) {
            icon = favorite.getItemProperty(AdmincentralNodeTypes.Favorite.ICON).getValue().toString();
        }

        this.icon = icon;
        this.root.addStyleName("favorites-entry");

        final Label iconLabel = new Label();
        iconLabel.setValue("<span class=\"" + icon + "\"></span>");
        iconLabel.setStyleName("icon");
        iconLabel.setContentMode(ContentMode.HTML);
        root.addComponent(iconLabel);

        titleField = new TextField();
        titleField.setValue(title);
        titleField.setReadOnly(true);
        root.addComponent(titleField);

        editButton = new NativeButton();
        editButton.setHtmlContentAllowed(true);
        editButton.setCaption("<span class=\"icon-edit\"></span>");
        editButton.addStyleName("icon");
        editButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (isSelected()) {
                    setEditable(true);
                    return;
                }
                boolean titleHasChanged = !getTitleValue().equals(titleField.getValue());
                if (isEditable() && titleHasChanged) {
                    listener.editFavorite(getRelPath(), titleField.getValue());
                }
                setEditable(false);
            }
        });
        editButton.setVisible(false);
        root.addComponent(editButton);

        removeButton = new NativeButton();
        removeButton.setHtmlContentAllowed(true);
        removeButton.setCaption("<span class=\"icon-trash\"></span>");
        removeButton.addStyleName("icon");
        removeButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                listener.removeFavorite(getRelPath());
            }
        });
        removeButton.setVisible(false);
        root.addComponent(removeButton);

        root.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {

                if (event.getClickedComponent() == titleField && !isSelected() && !isEditable()) {
                    if (event.isDoubleClick()) {
                        // setEditable(true);
                    } else {
                        listener.goToLocation(getLocationValue());
                    }
                } else if (event.getClickedComponent() == iconLabel) {
                    setSelected(true);
                }
            }
        });

        setCompositionRoot(root);
    }

    public String getLocationValue() {
        return location;
    }

    public String getIconValue() {
        return icon;
    }

    public String getTitleValue() {
        return title;
    }

    public String getGroupValue() {
        return group;
    }

    public String getRelPath() {
        return relPath;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        this.selected = !editable;
        titleField.setReadOnly(!editable);
        String icon = editable ? "icon-tick" : "icon-edit";
        editButton.setCaption("<span class=\"" + icon + "\"></span>");
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.editable = !selected;
        titleField.setReadOnly(true);
        editButton.setVisible(selected);
        editButton.setCaption("<span class=\"icon-edit\"></span>");
        removeButton.setVisible(selected);
    }

    /**
     * @return true if this favorite is selected, meaning the available actions (edit, remove) are displayed next to the fav title. Bear in mind that selected does not necessarily mean editable.
     * @see #isEditable()
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @return true if this fav is editable, meaning that its title can be changed in line.
     * @see #isSelected()
     */
    public boolean isEditable() {
        return editable;
    }
}
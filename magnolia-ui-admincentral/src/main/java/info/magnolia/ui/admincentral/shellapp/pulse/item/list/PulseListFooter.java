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
package info.magnolia.ui.admincentral.shellapp.pulse.item.list;

import static info.magnolia.ui.admincentral.shellapp.pulse.item.list.AbstractPulseListView.GROUP_PLACEHOLDER_ITEMID;

import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.admincentral.shellapp.pulse.task.TasksListView;
import info.magnolia.ui.vaadin.actionbar.ActionPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TreeTable;

/**
 * Footer view implementation displayed underneath the items list.
 */
public final class PulseListFooter extends CustomComponent {

    private HorizontalLayout footer;
    private NativeButton actionPopupTrigger;
    private TreeTable itemsTable;
    private static SimpleTranslator i18n;
    private Label status;
    private static PulseListView.Listener messagesListener;
    private static TasksListView.Listener tasksListener;
    private final ContextMenu contextMenu;
    // can't get the menu items from ContextMenu
    private static List<ContextMenuItem> menuItems = new ArrayList<ContextMenuItem>();

    private PulseListFooter(final TreeTable itemsTable, final SimpleTranslator i18n, final ContextMenu contextMenu) {
        super();
        this.itemsTable = itemsTable;
        PulseListFooter.i18n = i18n;
        this.contextMenu = contextMenu;
        construct();
        setCompositionRoot(footer);
    }

    public static PulseListFooter createTasksFooter(final TreeTable itemsTable, final SimpleTranslator i18n) {
        ContextMenu cm = buildTaskContextMenu(i18n, itemsTable);
        return new PulseListFooter(itemsTable, i18n, cm);
    }

    public static PulseListFooter createMessagesFooter(final TreeTable itemsTable, final SimpleTranslator i18n) {
        ContextMenu cm = buildMessageContextMenu(i18n, itemsTable);
        return new PulseListFooter(itemsTable, i18n, cm);
    }

    private void construct() {
        footer = new HorizontalLayout();
        footer.setSizeUndefined();
        footer.addStyleName("footer");

        final Label actionLabel = new Label(i18n.translate("pulse.footer.title"));

        actionPopupTrigger = new NativeButton();
        actionPopupTrigger.setHtmlContentAllowed(true);
        actionPopupTrigger.setCaption("<span class=\"icon-arrow2_e\"></span>");
        actionPopupTrigger.addStyleName("action-popup-trigger");

        actionPopupTrigger.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                contextMenu.open(event.getClientX(), event.getClientY());
            }
        });
        footer.addComponent(actionLabel);
        footer.addComponent(actionPopupTrigger);

        status = new Label();
        status.addStyleName("status");
        footer.addComponent(status);

        contextMenu.setAsContextMenuOf(actionPopupTrigger);
    }

    private static ContextMenu buildTaskContextMenu(final SimpleTranslator i18n, final TreeTable itemsTable) {
        final ActionPopup contextMenu = new ActionPopup();
        contextMenu.setOpenAutomatically(false);

        final ExternalResource iconAssignResource = new ExternalResource(ActionPopup.ICON_FONT_CODE + "icon-user-public");

        final ContextMenuItem claim = contextMenu.addItem(i18n.translate("pages.publish.actions.claim"), iconAssignResource);

        claim.addItemClickListener(new ContextMenuItemClickListener() {

            @Override
            public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
                final Set<String> selectedItems = (Set<String>) itemsTable.getValue();
                if (selectedItems == null || selectedItems.isEmpty()) {
                    // nothing to do here
                    return;
                }

                tasksListener.claimTask(selectedItems);
            }
        });
        claim.setEnabled(false);
        menuItems.add(claim);

        User user = MgnlContext.getUser();
        // TODO ideally context menu action availability should use the same mechanism and rules defined in the messageView config
        // but as this is not straightforward, for the time being we hack it like this
        if (user.getAllRoles().contains("superuser")) {
            addRemoveMenuItem(i18n, itemsTable, contextMenu, "pages.publish.actions.archive");
        }

        return contextMenu;
    }

    private static ContextMenu buildMessageContextMenu(final SimpleTranslator i18n, final TreeTable itemsTable) {
        final ActionPopup contextMenu = new ActionPopup();
        contextMenu.setOpenAutomatically(false);

        addRemoveMenuItem(i18n, itemsTable, contextMenu, "pages.publish.actions.delete");

        return contextMenu;
    }

    private static void addRemoveMenuItem(final SimpleTranslator i18n, final TreeTable itemsTable, final ContextMenu contextMenu, final String i18nKey) {
        final ExternalResource iconDeleteResource = new ExternalResource(ActionPopup.ICON_FONT_CODE + "icon-delete");

        final ContextMenuItem remove = contextMenu.addItem(i18n.translate(i18nKey), iconDeleteResource);

        remove.addItemClickListener(new ContextMenuItemClickListener() {

            @Override
            public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
                final Set<String> selectedItems = (Set<String>) itemsTable.getValue();
                if (selectedItems == null || selectedItems.isEmpty()) {
                    // nothing to do here
                    return;
                }
                messagesListener.deleteItems(selectedItems);
            }
        });

        remove.setEnabled(false);

        menuItems.add(remove);
    }

    public void updateStatus() {
        int totalMessages = 0;
        int totalSelected = 0;

        for (Object id : itemsTable.getItemIds()) {
            // skip generated header rows when grouping messages
            if (((String) id).startsWith(GROUP_PLACEHOLDER_ITEMID)) {
                continue;
            }
            totalMessages++;
        }
        for (String id : (Set<String>) itemsTable.getValue()) {
            // skip generated header rows when grouping messages
            if (id.startsWith(GROUP_PLACEHOLDER_ITEMID)) {
                continue;
            }
            totalSelected++;
        }
        // TODO ideally context menu action availability should use the same mechanism and rules defined in the messageView config
        // but as this is not straightforward, for the time being we hack it like this
        if (totalSelected > 0) {
            enableActions(true);
        } else {
            enableActions(false);
        }

        final String totalMessagesAsString = totalMessages > 0 ? Integer.toString(totalMessages) : i18n.translate("pulse.footer.status.no");
        final String selectedMessagesAsString = totalSelected > 0 ? Integer.toString(totalSelected) : i18n.translate("pulse.footer.status.none");
        status.setValue(i18n.translate("pulse.footer.status", totalMessagesAsString, selectedMessagesAsString));
    }

    public void setMessagesListener(final PulseListView.Listener listener) {
        PulseListFooter.messagesListener = listener;
    }

    public void setTasksListener(final TasksListView.Listener listener) {
        PulseListFooter.tasksListener = listener;
    }

    private void enableActions(boolean enable) {
        for (ContextMenuItem item : menuItems) {
            item.setEnabled(enable);
        }
    }

}

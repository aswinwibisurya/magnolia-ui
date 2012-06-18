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
package info.magnolia.ui.admincentral.dialog;

import info.magnolia.ui.admincentral.MagnoliaShell;
import info.magnolia.ui.admincentral.dialog.builder.DialogBuilder;
import info.magnolia.ui.admincentral.workbench.event.ContentChangedEvent;
import info.magnolia.ui.framework.event.EventBus;
import info.magnolia.ui.model.dialog.definition.DialogDefinition;
import info.magnolia.ui.vaadin.intergration.jcr.JcrNodeAdapter;
import info.magnolia.ui.widget.dialog.Dialog;
import info.magnolia.ui.widget.dialog.event.DialogCommitEvent;

import com.vaadin.data.Item;

/**
 * DialogPresenter.
 *
 * @author ejervidalo
 */
public class DialogPresenter extends Dialog {

    private DialogBuilder dialogBuilder;
    private DialogDefinition dialogDefinition;
    private MagnoliaShell shell;
    private EventBus eventBus;

    public DialogPresenter(DialogBuilder dialogBuilder, DialogDefinition dialogDefinition, MagnoliaShell shell, final EventBus eventBus) {
        super(eventBus);
        this.dialogBuilder = dialogBuilder;
        this.dialogDefinition = dialogDefinition;
        this.shell = shell;
        this.eventBus = eventBus;

        this.eventBus.addHandler(DialogCommitEvent.class, new DialogCommitEvent.Handler() {

            @Override
            public void onDialogCommit(DialogCommitEvent event) {
                JcrNodeAdapter itemChanged = (JcrNodeAdapter)event.getItem();
                //itemChanged.getNode().getSession().save();


                eventBus.fireEvent(new ContentChangedEvent(itemChanged.getItemProperty("workspace").toString(), itemChanged.getItemProperty("path").toString()));
            }
        });
    }

    public void showDialog(Item selectedBean) {
        dialogBuilder.build(dialogDefinition, selectedBean, this);
        shell.openDialog(this.asVaadinComponent());
    }

    @Override
    public void closeDialog() {
        shell.removeDialog(this.asVaadinComponent());
    }

}

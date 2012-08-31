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
package info.magnolia.ui.widget.magnoliashell.gwt.client.viewport;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;


/**
 * Dialogs viewport.
 * 
 */
public class VDialogViewport extends VShellViewport {

    public VDialogViewport() {
        setStyleName("v-dialog-viewport");
        getElement().getStyle().setVisibility(Visibility.HIDDEN);
        getModalityCurtain().addClassName("black-modality-curtain");
        setContentShowAnimationDelegate(AnimationDelegate.FADING_DELEGATE);
        setContentHideAnimationDelegate(AnimationDelegate.FADING_DELEGATE);
        setViewportHideAnimationDelegate(AnimationDelegate.FADING_DELEGATE);
        setCurtainVisible(true);
        setCurtainAnimated(true);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        if (getWidgetCount() == 0) {
            setActive(false);
        } else {
            setActive(true);
        }
    }

    @Override
    public void setVisibleWidget(Widget w) {
        if (w != null) {
            setActive(true);
        }
        super.setVisibleWidget(w);
    }
}
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
package info.magnolia.ui.vaadin.integration.widget.client.icon;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;


/**
 * The GwtIcon widget.
 */
public class GwtIcon extends Widget {

    private static final String CLASSNAME = "icon";

    private static final int SIZE_DEFAULT = 24;

    private final Element root = DOM.createSpan();

    private String iconName;

    private boolean innerIcon;

    public GwtIcon() {
        setElement(root);
        setStylePrimaryName(CLASSNAME);
    }

    public void setInnerIcon(boolean innerIcon) {
        this.innerIcon = innerIcon;
    }

    public void updateBaseStyles() {
        Style style = root.getStyle();
        if (!innerIcon) {
            style.setFontSize(SIZE_DEFAULT, Unit.PX);
            style.setProperty("lineHeight", "1");
            style.setVerticalAlign(VerticalAlign.MIDDLE);
        } else {
            style.setMarginLeft(-1, Unit.EM);
            style.setPosition(Position.ABSOLUTE);
        }
        style.setDisplay(Display.INLINE_BLOCK);
    }

    public void updateIconName(String iconName) {
        if (this.iconName != null) {
            removeStyleDependentName(this.iconName);
        }
        addStyleDependentName(iconName);
        this.iconName = iconName;
    }

    public void updateSize(int value) {
        if (!innerIcon) {
            root.getStyle().setFontSize(value, Unit.PX);
        }
    }

    public void updateColor(String value) {
        root.getStyle().setColor(value);
    }

}
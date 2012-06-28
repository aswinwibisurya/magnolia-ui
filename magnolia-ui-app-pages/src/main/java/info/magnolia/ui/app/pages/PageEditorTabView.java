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
package info.magnolia.ui.app.pages;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.framework.app.AppView;
import info.magnolia.ui.vaadin.integration.view.IsVaadinComponent;
import info.magnolia.ui.widget.editor.PageEditor;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * PageEditorTabView.
 * TODO: make this a component with a split layout to accomodate the page editor on the left and its related actions on the right.
 *
* @version $Id$
*/
@SuppressWarnings("serial")
public class PageEditorTabView implements AppView, IsVaadinComponent {

    private final VerticalLayout container = new VerticalLayout();
    private String caption;

    public PageEditorTabView(final Node pageNode) throws RepositoryException {
        final PageEditor pageEditor = new PageEditor(new ExternalResource(MgnlContext.getContextPath() + pageNode.getPath()));
        pageEditor.setSizeFull();

        container.setSizeFull();
        container.addComponent(pageEditor);
        caption = StringUtils.defaultIfEmpty(PropertyUtil.getString(pageNode, "title"), pageNode.getName());

    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public Component asVaadinComponent() {
        return container;
    }

}

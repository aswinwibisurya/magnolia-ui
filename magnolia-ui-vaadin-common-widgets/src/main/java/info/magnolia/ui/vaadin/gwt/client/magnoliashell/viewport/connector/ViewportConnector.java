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
package info.magnolia.ui.vaadin.gwt.client.magnoliashell.viewport.connector;

import info.magnolia.ui.vaadin.gwt.client.magnoliashell.shell.MagnoliaShellConnector.ViewportType;
import info.magnolia.ui.vaadin.gwt.client.magnoliashell.viewport.widget.ViewportWidget;
import info.magnolia.ui.vaadin.magnoliashell.viewport.ShellViewport;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;

/**
 * ViewportConnector.
 */
@Connect(ShellViewport.class)
public class ViewportConnector extends AbstractLayoutConnector {

    private EventBus eventBus;
    
    protected ElementResizeListener childCenterer = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            int width = e.getLayoutManager().getOuterWidth(e.getElement());
            final Style style = e.getElement().getStyle();
            style.setLeft(50, Unit.PCT);
            style.setMarginLeft(-width / 2, Unit.PX);            
        }
    };
    
    @Override
    protected void init() {
        addStateChangeHandler(new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent event) {
                final ComponentConnector candidate = (ComponentConnector)getState().activeComponent; 
                if (getWidget().getVisibleApp() != candidate) {
                    getWidget().setVisibleApp(candidate.getWidget());
                }
            }
        });
    };
    
    @Override
    public void updateCaption(ComponentConnector connector) {}

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        final ViewportWidget viewport = getWidget();
        final List<ComponentConnector> children = getChildComponents();
        final List<ComponentConnector> oldChildren = event.getOldChildren();
        int index = 0;
        for (final ComponentConnector cc : children) {
            final Widget w = cc.getWidget();
            if (w != viewport) {
                viewport.insert(w, index++);
                getLayoutManager().addElementResizeListener(w.getElement(), childCenterer);
            }
        }
        
        
        if (oldChildren.removeAll(children)) {
            for (final ComponentConnector cc : oldChildren) {
                cc.getLayoutManager().removeElementResizeListener(cc.getWidget().getElement(), childCenterer);
                viewport.remove(cc.getWidget());
            }
        }
        
        /*
        if (!client.updateComponent(this, uidl, true)) {

            final Collection<Widget> oldWidgets = new HashSet<Widget>();
            for (final Iterator<Widget> iterator = iterator(); iterator.hasNext();) {
                oldWidgets.add(iterator.next());
            }

            // Widget formerWidget = visibleWidget;
            if (uidl.getChildCount() > 0) {
                Widget app = null;
                for (int i = 0; i < uidl.getChildCount(); i++) {
                    final UIDL childUIdl = uidl.getChildUIDL(i);
                    final Paintable paintable = client.getPaintable(childUIdl);
                    oldWidgets.remove(paintable);
                    final Widget w = (Widget) paintable;
                    if (i == 0) {
                        app = w;
                    }
                    if (w.getParent() != this) {
                        add(w, getElement());
                    }

                    // make sure handling of visibility is left to viewport
                    boolean visible = w.isVisible();
                    paintable.updateFromUIDL(childUIdl, client);
                    if (forceContentAlign) {
                        alignChild(w);
                    }
                    w.setVisible(visible);

                }
                if (app != null) {
                    setVisibleApp(app);
                }
            } else {
                visibleApp = null;
            }

            for (Widget w : oldWidgets) {
                removeWidget(w);
            }
        }

        loadingPane.hide();
        */
    }
    
    @Override
    public ViewportWidget getWidget() {
        return (ViewportWidget)super.getWidget();
    }
    
    @Override
    protected ViewportWidget createWidget() {
        return new ViewportWidget();
    }
    
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ViewportState getState() {
        return (ViewportState)super.getState();
    }
    
    public ViewportType getType() {
        return getState().type;
    }

}

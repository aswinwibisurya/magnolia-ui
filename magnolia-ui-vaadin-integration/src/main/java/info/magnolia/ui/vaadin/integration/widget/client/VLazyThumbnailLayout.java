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
package info.magnolia.ui.vaadin.integration.widget.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.csstools.client.CSSRule;
import org.vaadin.csstools.client.ComputedStyle;
import org.vaadin.rpc.client.ClientSideHandler;
import org.vaadin.rpc.client.ClientSideProxy;
import org.vaadin.rpc.client.Method;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

/**
 * Client side impl of lazy asset thumbnails layout.
 *
 */
public class VLazyThumbnailLayout extends Composite implements Paintable, ClientSideHandler {

    private static final int QUERY_TIMER_DELAY = 250;
    
    private int thumbnailWidth = 0;

    private int thumbnailHeight = 0;

    private int thumbnailAmount = 0;

    private final List<Image> thumbnails = new ArrayList<Image>();

    private final List<Image> thumbnailStubs = new ArrayList<Image>();

    private ScrollPanel scroller = new ScrollPanel();

    private CSSRule thumbnailStyle = CSSRule.create(".thumbnail-image");
    
    private FlowPanel imageContainer = new FlowPanel();

    private ClientSideProxy proxy = new ClientSideProxy(this) {
        {
            register("addThumbnails", new Method() {
                @Override
                public void invoke(String methodName, Object[] params) {
                    final JsArrayString urls = parseStringArray(String.valueOf(params[0]));
                    addImages(urls);
                }
            });

            register("setThumbnailAmount", new Method() {
                @Override
                public void invoke(String methodName, Object[] params) {
                    setThumbnailAmount((Integer) params[0]);
                }
            });
            
            register("setThumbnailSize", new Method() {
                @Override
                public void invoke(String methodName, Object[] params) {
                    setThumbnailSize((Integer) params[0], (Integer) params[1]);
                }
            });
            
            register("clear", new Method() {
                @Override
                public void invoke(String methodName, Object[] params) {
                    imageContainer.clear();
                }
            });            
        }
    };

    public VLazyThumbnailLayout() {
        thumbnailStyle.setProperty("margin", "10px");
        scroller.setWidget(imageContainer);
        initWidget(scroller);
        scroller.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                createStubsAndQueryThumbnails();
            }
        });
    }

    private void createStubsAndQueryThumbnails() {
        int thumbnailsNeeded = calculateThumbnailsNeeded();
        addStubs(thumbnailsNeeded);
        queryTimer.schedule(QUERY_TIMER_DELAY);
    }
    
    private void addStubs(int thumbnailsNeeded) {
        for (int i = 0; i < thumbnailsNeeded; ++i) {
            addStub();
        }
    }
    
    private void addStub() {
        Image image = new Image(LazyThumbnailLayoutImageBundle.INSTANCE.getStubImage().getSafeUri());
        image.setStyleName("thumbnail-image");
        thumbnailStubs.add(image);
        imageContainer.add(image);
    }

    private void setThumbnailSize(int width, int height) {
        this.thumbnailHeight = height;
        this.thumbnailWidth = width;
        thumbnailStyle.setProperty("width", width + "px");
        thumbnailStyle.setProperty("height", height + "px");
        createStubsAndQueryThumbnails();
    }
    
    private Timer queryTimer = new Timer() {
        @Override
        public void run() {
            doQueryThumbnails(thumbnailStubs.size());
        };
    };

    private void addImages(JsArrayString urls) {
        for (int i = 0; i < urls.length() && !thumbnailStubs.isEmpty(); ++i) {
            final Image image = thumbnailStubs.remove(0);
            image.setUrl(urls.get(i));
            thumbnails.add(image);
        }
    }

    private void setThumbnailAmount(int thumbnailAmount) {
        this.thumbnailAmount = thumbnailAmount;
        int width = getOffsetWidth();
        int thumbnailsInRow = (int) (width / (thumbnailWidth + getHorizontalMargin()) * 1d);
        int rows = (int) (thumbnailAmount / thumbnailsInRow * 1d) * (thumbnailHeight + getVerticalMargin());
        imageContainer.getElement().getStyle().setHeight(rows, Unit.PX);
        createStubsAndQueryThumbnails();
    }

    private void doQueryThumbnails(int amount) {
        if (amount > 0) {
            proxy.call("loadThumbnails", amount);   
        }
    }

    @Override
    public void handleCallFromServer(String method, Object[] params) {
        VConsole.error("Unnknown server call: " + method);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        proxy.update(this, uidl, client);
    }

    @Override
    public boolean initWidget(Object[] params) {
        int thumbnailsNeeded = calculateThumbnailsNeeded();
        addStubs(thumbnailsNeeded);
        doQueryThumbnails(thumbnailsNeeded);
        return false;
    }

    private int calculateThumbnailsNeeded() {
        int totalHeight = scroller.getVerticalScrollPosition() + getOffsetHeight();
        int width = getOffsetWidth();
        int thumbnailsInRow = (int) (width / (thumbnailWidth + getHorizontalMargin()) * 1d);
        int rows = (int) Math.ceil(1d * totalHeight / (thumbnailHeight + getVerticalMargin()));
        int totalThumbnailsPossible = Math.min(thumbnailAmount, thumbnailsInRow * rows);
        int thumbnailsNeeded = Math.max(totalThumbnailsPossible  - thumbnailStubs.size() - thumbnails.size(), 0);
        return thumbnailsNeeded;
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        addStubs(calculateThumbnailsNeeded());
        queryTimer.schedule(QUERY_TIMER_DELAY);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        addStubs(calculateThumbnailsNeeded());
        queryTimer.schedule(QUERY_TIMER_DELAY);
    }
    
    public static native JsArrayString parseStringArray(String json) /*-{
        return eval('(' + json + ')');
    }-*/;
    
    private int getHorizontalMargin() {
        return ComputedStyle.parseInt(thumbnailStyle.getProperty("marginTop")) * 2;
    }
    
    private int getVerticalMargin() {
        return ComputedStyle.parseInt(thumbnailStyle.getProperty("marginLeft")) * 2;
    }
}

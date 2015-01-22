/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.ui.vaadin.gwt.client.editor.jsni;

import info.magnolia.ui.vaadin.gwt.client.widget.PageEditorView;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Frame;

/**
 * WebkitFrameEventHandler. Provides separated implementations to overcome different bugs in the
 * handling of iframes on webkit browsers including the iPad.
 */
public class WebkitFrameEventHandler extends AbstractFrameEventHandler {

    private int touchStartY = 0;

    @Override
    public void registerLoadHandler() {
        final Frame frame = getView().getFrame();

        frame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                bindReadyHandler(frame.getElement());
            }
        });
    }

    /**
     * Uses jQuery to detect when the iFrame is ready.
     */
    public native void bindReadyHandler(Element iframe) /*-{
        var that = this;
        $wnd.$(iframe).ready(function() {
            that.@info.magnolia.ui.vaadin.gwt.client.editor.jsni.WebkitFrameEventHandler::onFrameReady()()
        });

    }-*/;


    /**
     * Custom implementation for iPads of the touch end handling. Suppresses the selection, when scrolling.
     */
    @Override
    public native void initNativeTouchSelectionListener(Element element, PageEditorView.Listener listener) /*-{
        if (element != 'undefined') {
            var ref = this;
            var that = listener;
            var wndRef = $wnd;
            element.ontouchend = function(event) {
                var touchEndY = event.changedTouches[0].pageY;
                var touchStartY = ref.@info.magnolia.ui.vaadin.gwt.client.editor.jsni.WebkitFrameEventHandler::touchStartY;
                if (Math.abs(touchEndY - touchStartY) < 5) {
                    that.@info.magnolia.ui.vaadin.gwt.client.widget.PageEditorView.Listener::selectElement(Lcom/google/gwt/dom/client/Element;)(event.target);
                }
            }

            element.ontouchstart = function(event) {
                ref.@info.magnolia.ui.vaadin.gwt.client.editor.jsni.WebkitFrameEventHandler::touchStartY = event.targetTouches[0].pageY;
            }

        }
    }-*/;

    @Override
    public native void initNativeKeyListener(Element element) /*-{
        if (element != 'undefined') {
            element.onkeydown = function(event) {
                var eventObj = document.createEventObject ?
                    document.createEventObject() : document.createEvent("Events");

                if(eventObj.initEvent){
                    eventObj.initEvent("keydown", true, true);
                }

                eventObj.keyCode = event.keyCode;
                eventObj.which = event.keyCode;

                element.dispatchEvent ? element.dispatchEvent(eventObj) : element.fireEvent("onkeydown", eventObj);

                element.dispatchEvent(event);
            }
        }
    }-*/;

}
/**
 * This file Copyright (c) 2010-2013 Magnolia International
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
package info.magnolia.ui.vaadin.dialog;

import info.magnolia.ui.vaadin.dialog.BaseDialog.DialogCloseEvent.Handler;
import info.magnolia.ui.vaadin.editorlike.EditorLike;
import info.magnolia.ui.vaadin.editorlike.EditorLikeActionListener;
import info.magnolia.ui.vaadin.gwt.client.dialog.connector.BaseDialogState;
import info.magnolia.ui.vaadin.gwt.client.dialog.rpc.DialogServerRpc;

import com.vaadin.ui.Component;

/**
 * Basic implementation of dialogs.
 * Provides Action registration and callbacks to the view.
 * Can be closed.
 */
public class BaseDialog extends EditorLike implements DialogView {

    public BaseDialog() {
        super();

        registerRpc(new DialogServerRpc() {
            @Override
            public void fireAction(String actionId) {
                Object[] array = actionCallbackMap.get(actionId).toArray();
                for (Object l : array) {
                    ((EditorLikeActionListener)l).onActionExecuted(actionId);
                }
            }

            @Override
            public void closeSelf() {
                BaseDialog.this.closeSelf();
            }

            @Override
            public void toggleDescription() {
                BaseDialog.this.toggleDescription();
            }
        });
    }

    @Override
    protected BaseDialogState getState() {
        return (BaseDialogState) super.getState();
    }

    @Override
    public BaseDialog asVaadinComponent() {
        return this;
    }

    public void closeSelf() {
        fireEvent(new DialogCloseEvent(this, this));
    }

    public void toggleDescription() {
        fireEvent(new DescriptionVisibilityEvent(this, true));
    }

    @Override
    public void setDialogDescription(String description) {
        getState().componentDescription = description;
    }


    public void addDialogCloseHandler(Handler handler) {
        addListener("dialogCloseEvent", DialogCloseEvent.class, handler, DialogCloseEvent.ON_DIALOG_CLOSE);
    }

    public void removeDialogCloseHandler(Handler handler) {
        removeListener("dialogCloseEvent", DialogCloseEvent.class, handler);
    }

    public void addDescriptionVisibilityHandler(DescriptionVisibilityEvent.Handler handler) {
        addListener("descriptionVisibilityEvent", DescriptionVisibilityEvent.class, handler, DescriptionVisibilityEvent.ON_DESCRIPTION_VISIBILITY_CHANGED);
    }

    public void removeDescriptionVisibilityHandler(DescriptionVisibilityEvent.Handler handler) {
        removeListener("descriptionVisibilityEvent", DescriptionVisibilityEvent.class, handler);
    }

    /**
     * DialogCloseEvent.
     */
    public static class DialogCloseEvent extends com.vaadin.ui.Component.Event {

        /**
         * Handler.
         */
        public interface Handler {
            void onClose(DialogCloseEvent event);
        }

        public static final java.lang.reflect.Method ON_DIALOG_CLOSE;

        public DialogView view;

        static {
            try {
                ON_DIALOG_CLOSE = DialogCloseEvent.Handler.class.getDeclaredMethod("onClose", new Class[]{DialogCloseEvent.class});
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException(e);
            }
        }

        public DialogCloseEvent(Component source, DialogView view) {
            super(source);
            this.view = view;
        }

        public DialogView getView() {
            return view;
        }
    }

    /**
     * DescriptionVisibilityEvent.
     */
    public static class DescriptionVisibilityEvent extends com.vaadin.ui.Component.Event {
        /**
         * Handler.
         */
        public interface Handler {
            void onDescriptionVisibilityChanged(DescriptionVisibilityEvent event);
        }

        public static final java.lang.reflect.Method ON_DESCRIPTION_VISIBILITY_CHANGED;

        private boolean isVisible;

        static {
            try {
                ON_DESCRIPTION_VISIBILITY_CHANGED = DescriptionVisibilityEvent.Handler.class.getDeclaredMethod("onDescriptionVisibilityChanged", new Class[] { DescriptionVisibilityEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException(e);
            }
        }

        public DescriptionVisibilityEvent(Component source, boolean isVisible) {
            super(source);
        }

        public boolean isVisible() {
            return isVisible;
        }

    }
}

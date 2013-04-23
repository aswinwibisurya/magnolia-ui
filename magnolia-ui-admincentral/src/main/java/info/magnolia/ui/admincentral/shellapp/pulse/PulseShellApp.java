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
package info.magnolia.ui.admincentral.shellapp.pulse;

import info.magnolia.ui.admincentral.shellapp.ShellApp;
import info.magnolia.ui.admincentral.shellapp.ShellAppContext;
import info.magnolia.ui.admincentral.shellapp.pulse.message.MessagePresenter;
import info.magnolia.ui.admincentral.shellapp.pulse.message.PulseMessagesPresenter;
import info.magnolia.ui.framework.location.Location;
import info.magnolia.ui.framework.shell.ShellImpl;
import info.magnolia.ui.model.overlay.View;

import javax.inject.Inject;

/**
 * Pulse shell app.
 */
public class PulseShellApp implements ShellApp, PulseMessagesPresenter.Listener, MessagePresenter.Listener {

    private PulseView pulseView;

    private ShellAppContext context;
    private PulseMessagesPresenter messages;
    private MessagePresenter messagePresenter;
    private ShellImpl shell;

    @Inject
    public PulseShellApp(PulseView pulseView, PulseMessagesPresenter messages, MessagePresenter messagePresenter, ShellImpl shell) {
        this.pulseView = pulseView;
        this.messages = messages;
        this.messagePresenter = messagePresenter;
        this.shell = shell;
        messages.setListener(this);
        messagePresenter.setListener(this);
    }

    @Override
    public View start(ShellAppContext context) {
        this.context = context;
        pulseView.setPulseView(messages.start());
        return pulseView;
    }

    @Override
    public void locationChanged(Location location) {
        shell.hideAllMessages();
    }

    @Override
    public void openMessage(String messageId) {
        pulseView.setPulseView(messagePresenter.start(messageId));
    }

    @Override
    public void showList() {
        pulseView.setPulseView(messages.start());
    }
}

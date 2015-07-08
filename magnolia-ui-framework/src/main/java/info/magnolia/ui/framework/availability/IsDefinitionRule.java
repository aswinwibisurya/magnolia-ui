/**
 * This file Copyright (c) 2015 Magnolia International
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
package info.magnolia.ui.framework.availability;

import info.magnolia.config.registry.DefinitionProvider;
import info.magnolia.config.registry.Registry;
import info.magnolia.config.registry.RegistryFacade;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.ui.api.availability.AbstractAvailabilityRule;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemId;
import info.magnolia.ui.vaadin.integration.jcr.JcrPropertyItemId;

import java.util.Collection;

import javax.inject.Inject;
import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This returns {@code true} if the item has {@link DefinitionProvider}.
 */
public class IsDefinitionRule extends AbstractAvailabilityRule {

    private static final Logger log = LoggerFactory.getLogger(IsDefinitionRule.class);

    private final Collection<Registry> registries;

    @Inject
    public IsDefinitionRule(RegistryFacade registryFacade) {
        this.registries = registryFacade.all();
    }

    @Override
    protected boolean isAvailableForItem(Object itemId) {
        if (itemId instanceof JcrItemId && !(itemId instanceof JcrPropertyItemId)) {
            JcrItemId jcrItemId = (JcrItemId) itemId;
            Node node = SessionUtil.getNodeByIdentifier(jcrItemId.getWorkspace(), jcrItemId.getUuid());
            if (node != null) {
                try {
                    String nodePath = node.getPath();
                    return hasDefinitionFor(nodePath);
                } catch (Exception e) {
                    log.warn("Error evaluating availability for node [{}], returning false: {}", NodeUtil.getPathIfPossible(node), e.getMessage());
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean hasDefinitionFor(String nodePath) {
        // We're doing this stuff in a sub-optimal way unless the issue MAGNOLIA-6086 is fixed
        for (Registry registry : registries) {
            Collection<DefinitionProvider> providers = registry.getAllProviders();
            for (DefinitionProvider provider : providers) {
                if (provider.getMetadata().getLocation().equals(nodePath)) {
                    return true;
                }
            }
        }
        return false;
    }
}

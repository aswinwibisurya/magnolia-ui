/**
 * This file Copyright (c) 2011 Magnolia International
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
package info.magnolia.m5vaadin.shell.gwt.client;

import info.magnolia.m5vaadin.shell.gwt.client.VMainLauncher.ShellAppType;

/**
 * Helper class for holding the parsed info from the fragment.
 * @author apchelintcev
 */
public class FragmentDTO {
    
    private static final String FRAGMENT_DELIMITER = ":";
    
    /**
     * Enum for the types of fragments used within MagnoliaShell.
     * @author apchelintcev
     *
     */
    public enum FragmentType {
        APP,
        SHELL_APP;
    }
    
    private FragmentType type = FragmentType.SHELL_APP;
    
    private String id = "";
    
    private String param = "";
    
    protected FragmentDTO() {
        
    }
    
    public static FragmentDTO fromFragment(final String fragment) {
        final FragmentDTO dto = new FragmentDTO();
        final String[] tokens = fragment.split(FRAGMENT_DELIMITER);
        if (tokens.length > 1) {
            if (tokens[0].equals("shell")) {
                dto.type = FragmentType.SHELL_APP;
                dto.id = ShellAppType.getTypeByFragmentId(tokens[1]);
            } else if (tokens[0].equals("app")) {
                dto.type = FragmentType.APP;
                dto.id = tokens[1];
            }
        }
        dto.param = tokens.length > 2 ? tokens[2] : "";
        return dto;
    }
   
    public String getId() {
        return id;
    }
    
    public String getParam() {
        return param;
    }
    
    public FragmentType getType() {
        return type;
    }
    
    public String getPath() {
        return id + FRAGMENT_DELIMITER + param;
    }
}

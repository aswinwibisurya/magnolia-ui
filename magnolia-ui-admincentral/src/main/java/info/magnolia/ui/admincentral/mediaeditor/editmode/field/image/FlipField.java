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
package info.magnolia.ui.admincentral.mediaeditor.editmode.field.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jhlabs.image.FlipFilter;
import com.vaadin.data.Property;

/**
 * Provides the functionality for image flip.
 */
public class FlipField extends ViewImageField {

    private boolean isFlipHorizontal;

    public FlipField(boolean isFlipHorizontal) {
        this.isFlipHorizontal = isFlipHorizontal;
        setBuffered(true);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        execute();
    }

    @Override
    protected BufferedImage executeImageModification() throws IOException {
        final BufferedImage img = ImageIO.read(new ByteArrayInputStream(getValue()));
        final FlipFilter flipFilter = new FlipFilter(isFlipHorizontal ? FlipFilter.FLIP_H : FlipFilter.FLIP_V);
        return flipFilter.filter(img, null);
    }
    
    @Override
    public void execute() {
        super.execute();
        commit();
    }
}

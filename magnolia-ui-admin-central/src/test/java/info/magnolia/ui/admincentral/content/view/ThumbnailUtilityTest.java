/**
 * This file Copyright (c) 2003-2011 Magnolia International
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
package info.magnolia.ui.admincentral.content.view;

import static junit.framework.Assert.assertEquals;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Tests.
 */
public class ThumbnailUtilityTest {
    private static final String TEST_PATH = "src/test/resources/image/";
    private static final String ORIGINAL_IMAGE_NAME = "magnolia.jpg";
    private static final String FILE_FORMAT = "jpg";

    @Test
    public void testCreateThumbnail() throws IOException {
        // GIVEN
        final File file = new File(TEST_PATH + ORIGINAL_IMAGE_NAME);
        System.out.println(file.getAbsolutePath());
        final FileInputStream fis = new FileInputStream(file);
        final InputStream bis = new BufferedInputStream(fis);
        final Image image = (Image) ImageIO.read(bis);

        // WHEN
        final Image result = ThumbnailUtility.createThumbnail(image, FILE_FORMAT, 70, 100, 0.75f);

        // THEN
        assertEquals(70, result.getWidth(null));
        assertEquals(47, result.getHeight(null));
    }
}

/**
 * This file is part of GETARI.
 *
 * GETARI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GETARI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GETARI. If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * JUnit test of {@link PlatformUtil}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class PlatformUtilTest {

    /**
     * Test getCurrentDirectory() with another method.
     */
    @Test
    public void getCurrentDirectory() {
        final String expected = new File("").getAbsolutePath() + File.separator;
        final String actual = PlatformUtil.getCurrentDirectory();
        assertEquals(expected, actual);

        final String expected2 = Paths.get(".").toAbsolutePath().normalize()
                .toString() + File.separator;
        assertEquals(expected2, actual);
    }

    /**
     * Test getHomeDirectory().
     */
    @Test
    public void getHomeDirectory() {
        if (PlatformUtil.isUnix()) {
            final String expected = System.getenv("HOME") + File.separator;
            final String actual = PlatformUtil.getHomeDirectory();
            assertEquals(expected, actual);
        }
    }
}

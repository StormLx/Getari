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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * JUnit test of {@link StringUtils}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class StringUtilsTest {

    /**
     * Test {@link StringUtils#extension()}.
     */
    @Test
    public void extension() {
        final String[] areCSV = new String[]{"a.csv", "1.2.3.CSV", "file.cSv",
        ".a.csv"};
        for (final String name : areCSV) {
            assertEquals("csv", StringUtils.extension(name));
        }
    }

    /**
     * Test {@link StringUtils#extension()}.
     */
    @Test
    public void extensionNoExtension() {
        final String[] areCSV = new String[]{"test", ".test", ".test."};
        for (final String name : areCSV) {
            assertEquals("", StringUtils.extension(name));
        }
    }

    /**
     * Test {@link StringUtils#isNumeric()}.
     */
    @Test
    public void isNumeric() {
        final String[] areNumeric = new String[]{"1", "0", "84", "1995"};
        for (final String value : areNumeric) {
            assertTrue(StringUtils.isNumeric(value));
        }
        final String[] areNotNumeric = new String[]{"", " ", "a1", "0b", "84.",
        "voilà"};
        for (final String value : areNotNumeric) {
            assertFalse(StringUtils.isNumeric(value));
        }
    }

    /**
     * Test {@link StringUtils#join(Object[], String)}.
     */
    @Test
    public void join() {
        String result;

        final String[] array = new String[]{"one", "two", "three"};
        result = StringUtils.join(array, ",");
        assertEquals("one,two,three", result);

        final Object[] empty = new Object[]{};
        result = StringUtils.join(empty, ",");
        assertEquals("", result);
    }

    /**
     * Test {@link StringUtils#join(Double[], String)}.
     */
    @Test
    public void joinDoubles() {
        final Double[] values = new Double[]{1., 2., 0.};
        final String result = StringUtils.join(values, ",");
        assertEquals("1.0,2.0,0.0", result);
    }

    /**
     * Test {@link StringUtils#repeat(String, int)}.
     */
    @Test
    public void repeat() {
        final int multiplier = 3;
        final String input = "a-";
        final String result = StringUtils.repeat(input, multiplier);
        assertEquals("a-a-a-", result);
    }

    /**
     * Test {@link StringUtils#repeat(String, int)}.
     */
    @Test
    public void repeatNegativeMultiplier() {
        final int multiplier = -1;
        final String input = "a-";
        final String result = StringUtils.repeat(input, multiplier);
        assertEquals("", result);
    }

}

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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * JUnit test of {@link StringUtils.validate()}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@RunWith(Parameterized.class)
public final class StringUtilsValidateTest {

    /**
     * @return combinaisons of class, text and expectations
     */
    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {Double.class, "1234.567", true},
            {Double.class, "a1234.567", false},
            {Integer.class, "1234", true},
            {Integer.class, "1234.567", false},
            {String.class, "1234.567", true}
        });
    }
    /**
     * Class of the property to handle (Double, Integer, String).
     */
    private final Class<?> clazz;

    /**
     * Text to validate.
     */
    private final String text;

    /**
     * Expected validation.
     */
    private final boolean expected;

    /**
     * Constructor.
     *
     * @param aClazz Class of the property to handle (Double, Integer, String)
     * @param aText Text to validate
     * @param aExpected expected validation
     */
    public StringUtilsValidateTest(final Class<?> aClazz, final String aText,
            final Boolean aExpected) {
        clazz = aClazz;
        text = aText;
        expected = aExpected;
    }

    /**
     * Test StringUtils.validate().
     */
    @Test
    public void validate() {
        final boolean actual = StringUtils.validate(clazz, text);
        assertEquals("text", expected, actual);
    }
}

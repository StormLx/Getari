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
package fr.inrae.agroclim.getari;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test GetariFx methods.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class GetariFxMethodsTest {

    /**
     * Test CLI arguments parsing.
     */
    @Test
    public final void getEvaluationFileFromParameters() {
        List<String> args = Arrays.asList("--help");
        File actual = GetariFx.getFileFromParameters(args);
        assertNull(actual);

        args = Arrays.asList("--splash", "false");
        actual = GetariFx.getFileFromParameters(args);
        assertNull(actual);

        args = Arrays.asList("--splash", "false", "does-not-exists");
        actual = GetariFx.getFileFromParameters(args);
        assertNull(actual);

        actual = GetariFx.getFileFromParameters(null);
        assertNull(actual);

        args = Arrays.asList("--splash", "false",
                "src/test/resources/adura.gri");
        actual = GetariFx.getFileFromParameters(args);
        assertNotNull(actual);
    }
}

/**
 * This file is part of Getari.
 *
 * Getari is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Getari is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Getari. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.junit.Test;

import fr.inrae.agroclim.getari.component.UpdateChecker.Release;

/**
 * JUnit test of {@link UpdateCheckerTest}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class UpdateCheckerTest {

    /**
     * Instance for tests.
     */
    private final UpdateChecker checker = new UpdateChecker();

    /**
     * Fixed version for tests.
     */
    private final String currentVersion = "1.0.1";

    /**
     * Check when file is missing.
     *
     * @throws MalformedURLException never occurs
     */
    @Test
    public void fileNotFound() throws MalformedURLException {
        final File file = new File("src/test/resources/missing.txt");
        checker.setUrl(file.toURI().toURL().toString());
        final Optional<Release> actual = checker.check(currentVersion);
        assertFalse(actual.isPresent());
    }

    /**
     * Check when new update is in properties file.
     *
     * @throws MalformedURLException never occurs
     */
    @Test
    public void newUpdate() throws MalformedURLException {
        final File file = new File("src/test/resources/new-update.properties");
        checker.setUrl(file.toURI().toURL().toString());
        final Optional<Release> actual = checker.check(currentVersion);
        assertTrue(actual.isPresent());
        final Release expected;
        expected = new Release("1.0.2", "2019-01-28 13:44:06");
        assertEquals(expected, actual.get());
    }

    /**
     * Check when no update is in properties file.
     *
     * @throws MalformedURLException never occurs
     */
    @Test
    public void noNewUpdate() throws MalformedURLException {
        final File file = new File("src/test/resources/no-update.properties");
        checker.setUrl(file.toURI().toURL().toString());
        final Optional<Release> actual = checker.check(currentVersion);
        assertFalse(actual.isPresent());
    }

    /**
     * Check when file contains wrong string for date #6716.
     *
     * @throws java.net.MalformedURLException never occurs
     */
    @Test(expected = DateTimeParseException.class)
    public void wrongDate() throws MalformedURLException {
        final File file = new File("src/test/resources/update-wrong-date.properties");
        checker.setUrl(file.toURI().toURL().toString());
        checker.check(currentVersion);
    }

    /**
     * Check when file is not a properties file.
     *
     * @throws MalformedURLException never occurs
     */
    @Test
    public void wrongFile() throws MalformedURLException {
        final File file = new File("src/test/resources/not-gri.gri");
        checker.setUrl(file.toURI().toURL().toString());
        final Optional<Release> actual = checker.check(currentVersion);
        assertFalse(actual.isPresent());
    }
}

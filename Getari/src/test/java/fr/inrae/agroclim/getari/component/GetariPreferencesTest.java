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
package fr.inrae.agroclim.getari.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inrae.agroclim.getari.util.FileType;

/**
 * JUnit test of {@link GetariPreferences}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class GetariPreferencesTest {

    /**
     * Instance to test.
     */
    private GetariPreferences prefs;

    /**
     * Clear preferences when no key is added.
     */
    @Test
    public void clearNew() {
        assertThat(prefs.getKeys()).as("before clearing").isNotNull().isEmpty();
        prefs.clear();
        assertThat(prefs.getKeys()).as("after clearing").isNotNull().isEmpty();
    }

    /**
     * Clear preferences.
     */
    @After
    public void clearPreferences() {
        prefs.clear();
    }

    /**
     * Check that missing files are not returned.
     */
    @Test
    public void getExistingRecentlyOpenedEvaluations() {
        assertThat(prefs.getExistingRecentlyOpened(FileType.EVALUATION)).isNotNull()
        .isEmpty();

        final String path = "/this/does/not/exist.gri";
        prefs.putRecentlyOpened(FileType.EVALUATION, path);
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isNotNull()
        .contains(path);
        assertThat(prefs.getExistingRecentlyOpened(FileType.EVALUATION)).isNotNull()
        .isEmpty();

        final File eval = new File("src/test/resources/adura.gri");
        assertThat(eval).exists();
        prefs.putRecentlyOpened(FileType.EVALUATION, eval.getAbsolutePath());
        assertThat(prefs.getExistingRecentlyOpened(FileType.EVALUATION))
        .contains(eval.getAbsolutePath());
    }

    /**
     * No recently opened evaluation on new prefs.
     */
    @Test
    public void getRecentlyOpenedEvaluationsNew() {
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isNotNull().isEmpty();
    }

    /**
     * Set up preferences.
     */
    @Before
    public void initPreferences() {
        prefs = new GetariPreferences("getari.tests");
        prefs.clear();
    }

    /**
     * Test get/set locale.
     */
    @Test
    public void locale() {
        Locale locale = prefs.getLocale();
        assertThat(locale).isNull();

        prefs.setLocale(Locale.FRENCH);
        locale = prefs.getLocale();
        assertThat(locale).isEqualTo(Locale.FRENCH);

        prefs.setLocale(Locale.ENGLISH);
        locale = prefs.getLocale();
        assertThat(locale).isEqualTo(Locale.ENGLISH);
    }

    /**
     * Order must be kept.
     */
    @Test
    public void putRecentlyOpenedEvaluation() {
        final List<String> paths = Arrays.asList("a", "B", "3", "4", "5", "6",
                "7", "8", "9",
                "10");
        final List<String> expected = new ArrayList<>();
        for (final String path : paths) {
            prefs.putRecentlyOpened(FileType.EVALUATION, path);
            expected.add(path);
            List<String> openedEvaluations;
            openedEvaluations = prefs.getRecentlyOpened(FileType.EVALUATION);
            assertThat(openedEvaluations).isEqualTo(expected);
        }
        expected.remove("a");
        expected.add("11");
        prefs.putRecentlyOpened(FileType.EVALUATION, "11");
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isEqualTo(expected);

        expected.remove("11");
        prefs.removeRecentlyOpened(FileType.EVALUATION, "11");
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isEqualTo(expected).size().isEqualTo(expected.size());
    }

    /**
     * Path must appear only once.
     */
    @Test
    public void putRecentlyOpenedEvaluationNoDuplicates() {
        prefs.putRecentlyOpened(FileType.EVALUATION, "a");
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isEqualTo(
                Arrays.asList("a"));
        prefs.putRecentlyOpened(FileType.EVALUATION, "b");
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isEqualTo(
                Arrays.asList("a", "b"));
        prefs.putRecentlyOpened(FileType.EVALUATION, "a");
        assertThat(prefs.getRecentlyOpened(FileType.EVALUATION)).isEqualTo(
                Arrays.asList("b", "a"));
    }
}

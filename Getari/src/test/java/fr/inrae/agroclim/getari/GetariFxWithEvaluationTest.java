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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Test Getari UI with TestFX, when launched with an evaluation as CLI argument.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class GetariFxWithEvaluationTest extends GuiTest {

    /**
     * Set system properties to get TestFX works.
     */
    @BeforeClass
    public static void beforeClass() {
        setUpTestFx();
    }

    /**
     * Splash screen is shown in this test then disappears.
     *
     * @throws java.util.concurrent.TimeoutException WaitForAsyncUtils exception
     */
    @Test
    public final void launch() throws TimeoutException {
        Configurator.setLevel("fr.inrae.agroclim.getari.GetariFxWithEvaluationTest", Level.TRACE);
        LOGGER.trace("start");
        // wait splash screen disappears
        assertExists("#splashLayout");
        LOGGER.trace("splash is displayed");
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                isNotVisible("#splashLayout"));
        LOGGER.trace("splash is out");
        assertNotExists("#startLayout");
        // wait GUI ends building
        assertExists("#graphView");
        LOGGER.trace("has graphView");
        assertExists("#phaseCombo");
        LOGGER.trace("has phaseCombo");
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                hasPromptText("#phaseCombo"));
        LOGGER.trace("graph is loaded");
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                isEnabled("#evaluateBtn"));
        LOGGER.trace("launch evaluation");
        clickOn("#evaluateBtn");
        // can't find a Tab with its id, so use a selector
        // https://github.com/TestFX/TestFX/issues/634
        final String resultTab = ".tab-pane > .tab-header-area > .headers-region > .tab.resultTab";
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                isVisible(resultTab));
        LOGGER.trace("has resultTab");
        clickOn(resultTab);
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                isVisible("#copy"));
        LOGGER.trace("has copy");
        clickOn("#copy");
        clickOn("#close");
        Platform.runLater(() -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final String clip = clipboard.getString();
            assertNotNull(clip);
            assertTrue(clip, clip.startsWith(",Fichiers,"));
        });
    }

    @Override
    public final void start(final Stage stage) throws Exception {
        LOGGER.trace("start");
        GetariFx.setSplash(true);
        final GetariFx app = new GetariFx();
        app.setArguments(Arrays.asList("samples/evaluation_daily_sample.gri"));
        app.init();
        app.start(stage);
    }
}

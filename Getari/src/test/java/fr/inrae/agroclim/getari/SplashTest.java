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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Test Getari UI with TestFX.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class SplashTest extends GuiTest {
    /**
     * Set system properties to get TestFX works.
     */
    @BeforeClass
    public static void beforeClass() {
        setUpTestFx();
    }

    /**
     * Click on new button.
     * @throws java.util.concurrent.TimeoutException WaitForAsyncUtils exception
     */
    @Test
    public void clickOnNewShouldShowCreateEvaluation() throws TimeoutException {
        LOGGER.trace("start");
        // given
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                isNotVisible("#splashLayout"));

        // when
        clickOn("#newBtn");

        // then
        final Label filename = lookup("#climaticFileNameLabel").query();
        final String found = filename.getText();
        assertEquals(Messages.getString("evaluation.climatic.label"), found);

        final Button createBtn = lookup("#createBtn").query();
        assertTrue(createBtn.isDisabled());
    }

    /**
     * Splash screen is shown in this test.
     */
    @Test
    public void launchShouldShowSplash() {
        LOGGER.trace("start");
        assertEquals(
                "Splash layout must exist.",
                1, lookup("#splashLayout").queryAll().size()
                );
    }

    @Override
    public void start(final Stage stage) throws Exception {
        LOGGER.trace("start");
        GetariFx.setSplash(true);
        final GetariFx app = new GetariFx();
        app.init();
        app.start(stage);
    }
}

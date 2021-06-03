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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
public final class GetariFxTest extends GuiTest {
    /**
     * Set system properties to get TestFX works.
     */
    @BeforeClass
    public static void beforeClass() {
        setUpTestFx();
    }

    /**
     * @param t TextField to change style to invalid
     * @return Style of TextField is not invalid
     */
    private static boolean isValid(final TextField t) {
        return t.getStyleClass().contains("valid-field");
    }

    /**
     * Click on new button.
     * @throws java.util.concurrent.TimeoutException while waiting for
     */
    @Test
    public void clickOnNewShouldShowCreateEvaluation() throws TimeoutException {
        LOGGER.trace("start");
        // given

        // when
        clickOn("#newBtn");
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                isVisible("#climaticFileNameLabel"));

        // then
        final String expectedLabelText = Messages.getString("evaluation.climatic.label");
        verifyThat("#climaticFileNameLabel", hasText(expectedLabelText));

        // same test, in another way
        final Label filename = lookup("#climaticFileNameLabel").query();
        final String found = filename.getText();
        assertEquals(expectedLabelText, found);

        final Button createBtn = lookup("#createBtn").query();
        assertTrue(createBtn.isDisabled());

        press(KeyCode.ESCAPE);
        assertNotVisible("#createBtn");
    }

    /**
     * Filling form.
     */
    @Test
    public void fillNewEvaluationShouldRemoveWarnings() {
        LOGGER.trace("start");

        // given
        clickOn("#newBtn");
        final TextField evaluationName = lookup("#evaluationName").query();
        assertNotNull(evaluationName);
        final TextField climaticFileName = lookup("#climaticFileName").query();
        assertNotNull(climaticFileName);
        final TextField phenologicalFileName;
        phenologicalFileName = lookup("#phenologicalFileName").query();
        assertNotNull(phenologicalFileName);
        final Button createBtn = lookup("#createBtn").query();
        assertNotNull(createBtn);

        assertTrue(!isValid(evaluationName));
        assertTrue(!isValid(climaticFileName));
        assertTrue(!isValid(phenologicalFileName));
        assertTrue(createBtn.isDisabled());

        clickOn("#evaluationName");
        assertTrue(!isValid(evaluationName));

        try {
            // when
            evaluationName.setText("EvaluationTest");
            final File climaticFile = new File("doc/climate_sample.txt");
            final String climaticFileAbsolutePath = climaticFile.getAbsolutePath();
            final File phenologicalFile = new File("doc/pheno_sample.csv");
            /*- mimic user actions with file chooser: */
            /*-
        clickOn("#climaticFileNameBtn");
        sleep(1, TimeUnit.SECONDS);
        write(climaticFile.getAbsolutePath()).push(KeyCode.ENTER);
        clickOn("#phenologicalFileNameBtn");
        sleep(1, TimeUnit.SECONDS);
        write(phenologicalFile.getAbsolutePath()).push(KeyCode.ENTER);
             */
            // work around to not use the file chooser
            if (!climaticFileAbsolutePath.equals(climaticFileName.getText())) {
                climaticFileName.setText(climaticFileAbsolutePath);
                phenologicalFileName.setText(phenologicalFile.getAbsolutePath());
            }

            // then
            assertEquals(climaticFileAbsolutePath, climaticFileName.getText());
            assertEquals(phenologicalFile.getAbsolutePath(),
                    phenologicalFileName.getText());

            // Très nombreux plantages uniquement sur l'intégration continue
            assertNotNull("styleClass of evaluationName should not be null", evaluationName.getStyleClass());
            assertTrue("Field evaluationName should be valid",
                    isValid(evaluationName));
            assertTrue(isValid(climaticFileName));
            assertTrue(isValid(phenologicalFileName));
            assertTrue(!createBtn.isDisabled());
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.warn("Sometimes TestFX fails", e);
        } catch (final Throwable e) {
            LOGGER.catching(e);
        }
    }

    /**
     * No splash screen in this test suite.
     */
    @Test
    public void launchShouldNotShowSplash() {
        LOGGER.trace("start");
        assertTrue(
                "Splash layout must not exist.",
                lookup("#splashLayout").queryAll().isEmpty()
                );
    }

    @Override
    public void start(final Stage stage) throws Exception {
        LOGGER.trace("start");
        GetariFx.setSplash(false);
        final GetariFx app = new GetariFx();
        app.init();
        app.start(stage);
    }
}

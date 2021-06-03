/*
 * Copyright (C) 2021 INRAE AgroClim
 *
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
package fr.inrae.agroclim.getari;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

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
public class GetariFxWithMultiExecutionTest extends GuiTest {

    /**
     * Expected generated outputs.
     */
    private static final String[] OUTPUTS = new String[]{"samples/output.csv", "samples/output-metadata.txt",
            "samples/output2.csv", "samples/output2-metadata.txt"};

    /**
     * Set system properties to get TestFX works.
     */
    @BeforeClass
    public static void beforeClass() {
        setUpTestFx();
    }

    /**
     * Remove generated outputs.
     */
    @AfterClass
    @BeforeClass
    public static void cleanFiles() {
        for (final String path : OUTPUTS) {
            final File output1 = new File(path);
            if (output1.exists()) {
                try {
                    Files.delete(output1.toPath());
                } catch (final IOException ex) {
                    LOGGER.catching(ex);
                }
            }
        }
    }

    /**
     * Splash screen is shown in this test then disappears.
     *
     * @throws java.util.concurrent.TimeoutException WaitForAsyncUtils exception
     * @throws java.io.IOException while getting file size
     */
    @Test
    public final void launch() throws TimeoutException, IOException {
        Configurator.setLevel("fr.inrae.agroclim.getari.GetariFxWithMultiExecutionTest", Level.TRACE);
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
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                isVisible("#table"));
        LOGGER.trace("table is loaded");
        WaitForAsyncUtils.waitFor(
                5,
                TimeUnit.SECONDS,
                isEnabled("#run"));
        LOGGER.trace("launch multiExecution");
        clickOn("#run");
        // ensure files are generated, waiting for the first -metadata.txt file to appear
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                () -> {
                    final File file = new File(OUTPUTS[1]);
                    return file.exists();
                });
        for (final String fileName : OUTPUTS) {
            final Path path = Paths.get(fileName);
            final File output = path.toFile();
            assertTrue("The output must exist: " + fileName, output.exists());
            final long bytes = Files.size(path);
            assertTrue("The file must not be empty: " + fileName, bytes > 0);
        }
        // click on some buttons
        clickOn("#add");
        clickOn("#close");
    }

    @Override
    public final void start(final Stage stage) throws Exception {
        LOGGER.trace("start");
        GetariFx.setSplash(true);
        final GetariFx app = new GetariFx();
        app.setArguments(Arrays.asList("samples/multiexecution.xml"));
        app.init();
        app.start(stage);
    }
}

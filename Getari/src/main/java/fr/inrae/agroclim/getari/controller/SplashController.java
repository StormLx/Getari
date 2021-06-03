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
package fr.inrae.agroclim.getari.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

/**
 * Handle startup splash screen.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class SplashController implements Initializable {

    /**
     * 500 ms.
     */
    private static final int HALF_SECOND = 500;

    /**
     * Progress bar.
     */
    @FXML
    private ProgressBar progressBar;

    /**
     * Text for progress.
     */
    @FXML
    private Label progressText;

    /**
     * Layout.
     */
    @FXML
    private GridPane splashLayout;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        //
    }

    /**
     * Start animation on progress bar.
     *
     * @param runnable runnable to call after progress bar completion or failure
     */
    public void startProgress(final Runnable runnable) {
        final Task<Integer> initTask = new Task<Integer>() {
            private final List<String> phases = Arrays.asList("startup.processes", "startup.practices",
                    "startup.effects", "startup.indicators");

            @Override
            protected Integer call() throws InterruptedException {

                updateMessage(Messages.getString("startup.initializing"));
                int i = 0;
                for (final String phase : phases) {
                    if (isCancelled()) {
                        break;
                    }
                    try {
                        Thread.sleep(HALF_SECOND);
                    } catch (final InterruptedException interrupted) {
                        LOGGER.warn("InterruptedException!");
                        break;
                    }
                    i++;
                    updateProgress(i, phases.size());
                    updateMessage(Messages.getString("startup.searching", Messages.getString(phase)));
                }
                try {
                    Thread.sleep(HALF_SECOND);
                } catch (final InterruptedException interrupted) {
                    LOGGER.warn("InterruptedException!");
                }
                updateMessage(Messages.getString("startup.finished"));
                return i;
            }
        };
        progressText.textProperty().bind(initTask.messageProperty());
        progressBar.progressProperty().bind(initTask.progressProperty());
        // get a handle to the stage
        final Stage stage = (Stage) progressBar.getScene().getWindow();
        initTask.setOnSucceeded(workerStateEvent -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(1);
            stage.toFront();
            final double seconds = 1.2;
            FadeTransition fadeSplash;
            fadeSplash = new FadeTransition(Duration.seconds(seconds), splashLayout);
            fadeSplash.setFromValue(1.0);
            fadeSplash.setToValue(0.0);
            fadeSplash.setOnFinished(e -> stage.close());
            fadeSplash.play();
            runnable.run();
        });
        initTask.setOnFailed(eh -> {
            LOGGER.error("Splash task failed: {}", eh);
            stage.close();
            runnable.run();
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(initTask);
    }
}

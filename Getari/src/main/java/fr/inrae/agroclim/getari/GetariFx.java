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

import java.awt.Image;
import java.awt.Taskbar;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.UpdateChecker;
import fr.inrae.agroclim.getari.component.UpdateChecker.Release;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.MultiExecutionHelper;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.getari.view.SplashView;
import fr.inrae.agroclim.getari.view.StartView;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Entry point for the JavaFX interface of GETARI.
 *
 * - Affichage d'une fenêtre de chargement
 *
 * - Instanciation de la vue principale
 *
 * Last change $Date$
 *
 * @author ddelannoy
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class GetariFx extends Application {

    /**
     * Display splash screen at start up.
     */
    @Setter
    private static boolean splash = true;

    /**
     * Extract file path from command line arguments and return File if exists.
     *
     * @param args command line arguments
     * @return file or null
     */
    protected static File getFileFromParameters(final List<String> args) {
        if (args == null) {
            return null;
        }
        boolean dash = false;
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            // skip option without value
            if ("--help".equals(arg)) {
                break;
            }
            // skip if value of option
            if (dash) {
                dash = false;
                continue;
            }
            if (arg.startsWith("--")) {
                dash = true;
                continue;
            }
            final File file = new File(arg);
            if (!file.exists()) {
                LOGGER.warn("{} does not exist!", arg);
            } else if (!file.isFile()) {
                LOGGER.warn("{} is not a file!", arg);
            } else {
                return file;

            }
        }
        return null;
    }

    /**
     * Start the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void start(final String[] args) {
        LOGGER.trace("start {}", String.join(",", args));
        GetariApp.setUpLog();
        GetariApp.logAppend("start Getari");
        // Set an icon for Mac OSX and other OS supporting this method
        if (PlatformUtil.isMac()) {
            try {
                final URL iconURL = Getari.class.getResource(
                        "/fr/inrae/agroclim/getari/images/logo_getari_icon.png");
                final Image img = new ImageIcon(iconURL).getImage();
                final Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(img);
            } catch (final UnsupportedOperationException e) {
                LOGGER.error("The os does not support: 'taskbar.setIconImage'");
            } catch (final SecurityException e) {
                LOGGER.error("There was a security exception for: 'taskbar.setIconImage'");
            }
        }
        //-
        final Locale locale = GetariApp.get().getPreferences().getLocale();
        if (locale != null) {
            Messages.setLocale(locale);
        }
        launch(args);
    }

    /**
     * Command line arguments.
     */
    @Setter(AccessLevel.PACKAGE)
    private List<String> arguments;

    /**
     * Stage for mainView.
     */
    private Stage mainStage;

    /**
     * Window with menu and graph.
     */
    private MainView mainView;

    /**
     * Check updates and show alert if new release exists.
     */
    private void checkUpdates() {
        final String getariUrl = "https://w3.avignon.inrae.fr/getari/";
        final Task<Optional<Release>> task = new Task<Optional<Release>>() {
            @Override
            protected Optional<Release> call() throws Exception {
                final UpdateChecker checker = new UpdateChecker();
                checker.setUrl(getariUrl + "update.properties");
                final String currentVersion = Version.getString("version");
                try {
                    return checker.check(currentVersion);
                } catch (final DateTimeParseException e) {
                    LOGGER.fatal("Error while checking new version: ", e);
                    return Optional.empty();
                }
            }
        };
        task.setOnSucceeded(e ->
        task.getValue().ifPresent(release -> {
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            AlertUtils.setStyle(alert);
            alert.setTitle(Messages.getString("application.new.version"));
            DateTimeFormatter df;
            df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            alert.setHeaderText(
                    Messages.getString(
                            "confirm.visit.download",
                            release.getVersion(),
                            df.format(release.getDate())
                            )
                    );
            final Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent()
                    || result.get() == ButtonType.CANCEL) {
                alert.close();
            } else {
                PlatformUtil.openBrowser(getariUrl + "en/download/");
                alert.close();
            }
        })
                );
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    @Override
    public void init() {
        LOGGER.trace("init GetariFX");
        if (getParameters() != null) {
            arguments = getParameters().getRaw();
        }
    }

    /**
     * Build view and display stage.
     */
    private void showMainStage() {
        LOGGER.trace("start");
        mainStage = new Stage(StageStyle.UNDECORATED);
        mainStage.getIcons().add(
                ComponentUtil.loadImage("logo_getari_icon.png"));
        mainStage.setTitle("GETARI");
        // this makes all stages close and
        // the app exit when the main stage is closed
        mainStage.setOnCloseRequest(event -> {
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            AlertUtils.setStyle(alert);
            alert.setTitle(Messages.getString("exit.text"));
            alert.setHeaderText(Messages.getString("confirm.exit"));
            final Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || result.get() == ButtonType.CANCEL) {
                event.consume();
            }
        });
        final HostServices myHostServices = getHostServices();
        if (myHostServices == null) {
            LOGGER.warn(Messages.getString("warning.hostservices.not.found"));
        }
        PlatformUtil.setHostServices(myHostServices);
        mainView = new MainView();
        mainView.build(mainStage);
        final File file = getFileFromParameters(arguments);
        if (file != null) {
            if (MultiExecutionHelper.isMultiExecution(file)) {
                mainView.getController().openMEFile(file);
            } else {
                mainView.getController().openEvaluationFile(file,
                        () -> LOGGER.traceExit("{} open from command line", file));
            }
        } else {
            showStartPane();
        }
        checkUpdates();
    }

    /**
     * Display startup actions to user.
     */
    private void showStartPane() {
        try {
            StartView startView;
            startView = new StartView();
            startView.build(mainStage);
            startView.getController().setMainViewController(mainView.getController());
            ComponentUtil.setUpDragDropHandling(mainView.getController(),
                    startView.getStage().getScene(), true);
            mainView.setDialog(startView.getStage());
        } catch (final IOException ex) {
            LOGGER.warn("Error while loading FXML StartView", ex);
        }
    }

    @Override
    public void start(final Stage initStage) {
        LOGGER.trace("start");
        GetariApp.setGlobalCss(Getari.class.getResource("getari.css").toExternalForm());

        if (!splash) {
            LOGGER.trace(Messages.getString("startup.skipped"));
            showMainStage();
            return;
        }

        final SplashView view = new SplashView();
        try {
            view.build(initStage);
            view.getController().startProgress(this::showMainStage);
        } catch (final IOException e) {
            LOGGER.fatal("Error while loading SplashView. This should never occur!", e);
        }
    }

}

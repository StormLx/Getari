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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.inrae.agroclim.getari.event.ConsistencyEvent;
import fr.inrae.agroclim.getari.event.EvaluationSaveEvent;
import fr.inrae.agroclim.getari.event.EventBus;
import fr.inrae.agroclim.getari.exception.GetariException;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.getari.view.ResultsView;
import fr.inrae.agroclim.indicators.exception.FunctionalException;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.resources.Version;
import fr.inrae.agroclim.indicators.xml.XMLUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Singleton global de l'application Getari.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class GetariApp implements EventHandler<ConsistencyEvent> {

    /**
     * Lambda function which handle graphView to create an evaluation.
     */
    @FunctionalInterface
    public interface EvaluationCreator {

        /**
         * @param graphView graph view returned by {link
         * GetariApp#openEvaluationTab()}
         * @throws GetariException if something is wrong while creating
         * evaluation
         */
        void apply(GraphView graphView) throws GetariException;
    }

    /**
     * Singleton.
     */
    private static final GetariApp SINGLETON;

    /**
     * MainView.
     */
    @Getter
    @Setter
    private static MainView mainView;

    /**
     * CSS used in the whole application.
     */
    @Getter
    @Setter
    private static String globalCss;

    /**
     * Last opened directory.
     */
    @Setter
    private static String lastDirectory = PlatformUtil.getCurrentDirectory();

    static {
        SINGLETON = new GetariApp();
        GetariApp.get().getEventBus().addEventHandler(ConsistencyEvent.TYPE,
                SINGLETON);
    }
    /**
     * Close application.
     */
    public static void close() {
        LOGGER.info("Exit...");
        if (get().currentEval != null && get().currentEval.isTranscient()) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Messages.getString("exit.text"));
            alert.setHeaderText(
                    Messages.getString("confirm.exit.transcient.evaluation"));
            final Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent()
                    || result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        mainView.getStage().close();
        // When Getari exits without errors, do not leave traces
        if (get().getLog() != null) {
            get().getLog().getPath().toFile().delete();
        }
        //-
        Platform.exit();
        System.exit(0);
    }

    /**
     * Build the Evaluation, the GraphView object and link them.
     *
     * @param settings evaluation settings of evaluation to create
     * @param graphView graph of evaluation
     * @throws GetariException if no stages
     * @throws IOException should never occurs when building view
     */
    public static void createEvaluation(final EvaluationSettings settings,
            final GraphView graphView) throws GetariException, IOException {
        final Evaluation evaluation = new Evaluation(settings.getEvaluation());
        evaluation.setSettings(settings);
        evaluation.initializeParent();
        evaluation.addIndicatorListener(graphView.getIndicatorListener());
        if (evaluation.getIndicators().isEmpty()) {
            LOGGER.warn("Evaluation {} does not contain indicators!",
                    evaluation.getName());
        }
        settings.initializeKnowledge();
        // loading climatic resource must be forced as no indicator
        // is set to get needed variables
        try {
            evaluation.initializeResources(true, false);
        } catch (final Exception e) {
            final String msg = Messages.getString("error.evaluation.initialize.resources");
            AlertUtils.showException(msg, e);
            return;
        }
        if (evaluation.getResourceManager().getPhenologicalResource().getData() == null) {
            final String msg = Messages.getString("evaluation.no.stages");
            AlertUtils.showError(msg);
            throw new GetariException(msg);
        }
        GetariApp.get().setCurrentEval(evaluation);
        mainView.addEvaluationTab(evaluation, graphView.build());
        evaluation.addIndicatorListener(mainView);
        evaluation.addIndicatorListener(graphView);
        evaluation.addIndicatorListener(mainView.getMenuController());
        if (GetariApp.get().getCurrentEval().getIndicators().isEmpty()) {
            LOGGER.warn("Evaluation {} does not contain indicators!", GetariApp
                    .get().getCurrentEval().getName());
        }
    }

    /**
     * @return Singleton
     */
    public static GetariApp get() {
        return SINGLETON;
    }

    /**
     * Last opened directory or current directory.
     *
     * @return directory path
     */
    public static String getLastDirectory() {
        if (lastDirectory == null || !new File(lastDirectory).isDirectory()) {
            lastDirectory = PlatformUtil.getCurrentDirectory();
        }
        return lastDirectory;

    }

    /**
     * Last opened directory for the type or current directory.
     *
     * @param type type of directory
     * @return directory path
     */
    public static File getLastDirectory(final FileType type) {
        // lastDir set from Preferences, GetariApp or HomeDirectory.
        final String lastDir = get().getPreferences().getLastDirectory(type.name());
        if (lastDir == null) {
            return new File(getLastDirectory());
        }
        // check if directory was deleted
        final File initialDir = new File(lastDir);
        if (!initialDir.isDirectory()) {
            return new File(getLastDirectory());
        }
        return initialDir;
    }

    /**
     * @return Stage for the MainView.
     */
    public static Stage getMainStage() {
        return mainView.getStage();
    }

    /**
     * Category for context menu.
     *
     * @param category category or null for phase
     * @param usePractices if category == pheno : true : cultural practices,
     * false : ecophysio
     * @return next category
     */
    public static String getNextCategory(final String category,
            final boolean usePractices) {
        String returnValue = "";
        if (category == null) {
            returnValue = IndicatorCategory.PHENO_PHASES.getTag();
        } else if (category.equals(IndicatorCategory.PHENO_PHASES.getTag())) {
            if (usePractices) {
                returnValue = IndicatorCategory.CULTURAL_PRATICES.getTag();
            } else {
                returnValue = IndicatorCategory.ECOPHYSIOLOGICAL_PROCESSES
                        .getTag();
            }
        } else if (category
                .equals(IndicatorCategory.CULTURAL_PRATICES.getTag())) {
            returnValue = IndicatorCategory.CLIMATIC_EFFECTS.getTag();
        } else if (category.equals(IndicatorCategory.ECOPHYSIOLOGICAL_PROCESSES
                .getTag())) {
            returnValue = IndicatorCategory.CLIMATIC_EFFECTS.getTag();
        } else if (category.equals(
                IndicatorCategory.CLIMATIC_EFFECTS.getTag())) {
            returnValue = IndicatorCategory.INDICATORS.getTag();
        } else if (category.equals(IndicatorCategory.INDICATORS.getTag())) {
            returnValue = IndicatorCategory.INDICATORS.getTag();
        }
        return returnValue;
    }

    /**
     * Append a string to the file with additional data.
     *
     * @param clazz class which logs
     * @param stage related stage
     * @param text string to append
     */
    public static void logAppend(final Class<?> clazz, final Stage stage,
            final String... text) {
        if (get().getLog() != null) {
            final String[] text2 = new String[text.length + 2];
            System.arraycopy(text, 0, text2, 2, text.length);
            if (clazz != null) {
                text2[0] = clazz.getCanonicalName();
            }
            if (stage != null) {
                text2[1] = stage.getTitle();
            }
            get().getLog().append(text2);
        }
    }

    /**
     * Append a string to the file.
     *
     * @param text string to append
     */
    public static void logAppend(final String... text) {
        if (get().getLog() != null) {
            get().getLog().append(text);
        }
    }

    /**
     * Open a new tab with the evaluation built by creator.
     *
     * @param creator evaluation creator
     */
    public static void openEvaluationTab(final EvaluationCreator creator) {
        if (mainView == null) {
            LOGGER.fatal("mainView must not be null!");
            return;
        }
        final GraphView graphView;
        try {
            graphView = new GraphView(mainView.getStage(), mainView);
            get().getEventBus().addEventHandler(EvaluationSaveEvent.TYPE, graphView);
        } catch (final IOException e) {
            LOGGER.fatal("This should never occur!", e);
            return;
        }
        try {
            creator.apply(graphView);
        } catch (final GetariException | RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            final String msg = Messages.getString("evaluation.error.loading")
                    + "\n" + e.getLocalizedMessage();
            AlertUtils.showError(msg);
            return;
        }
        final Task<String> waitingTask = new Task<String>() {
            @Override
            public String call() throws Exception {
                final long sleep = 1_000;
                Thread.sleep(sleep);
                return "New Message";
            }
        };
        waitingTask.setOnSucceeded((final WorkerStateEvent event1) -> {
            LOGGER.info("Loading graph...");
            try {
                graphView.loadGraph();
            } catch (final CloneNotSupportedException ex) {
                LOGGER.fatal("This should never occurs as indicator "
                        + "must implement clone().", ex);
                throw new RuntimeException(ex);
            }
            GetariApp.get().getCurrentEval().setTranscient(true);
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(waitingTask);
    }
    /**
     * Set up log for user interaction.
     */
    public static void setUpLog() {
        try {
            final Path path = Files.createTempFile("getari-actions", ".log");
            get().log = new ActionLog(path);
            LOGGER.info("User's traces are stored in {}", path);
        } catch (final IOException e) {
            LOGGER.catching(e);
        }
    }

    /**
     * Event bus.
     */
    @Getter
    private final EventBus eventBus = new EventBus();

    /**
     * Current evaluation open in Getari.
     */
    @Getter
    @Setter
    private Evaluation currentEval;

    /**
     * Current result view open in Getari.
     */
    @Getter
    @Setter
    private ResultsView currentResultView;

    /**
     * Log for user interaction.
     */
    @Getter
    private ActionLog log;

    /**
     * User preferences handling for Getari.
     */
    @Getter
    private final GetariPreferences preferences = new GetariPreferences();

    /**
     * No constructor for singleton.
     */
    private GetariApp() {
        preferences.setEventBus(eventBus);
    }

    @Override
    public void handle(final ConsistencyEvent e) {
        Objects.requireNonNull(e);
        if (e.getErrors() == null) {
            return;
        }
        final StringJoiner sj = new StringJoiner("\n");
        e.getErrors().forEach((k, m) -> {
            LOGGER.info("{} : {}", k, m.toJSON());
            sj.add(m.getMessage());
        });
        AlertUtils.showError(sj.toString());
    }

    /**
     * Run computation on the current evaluation and handle exceptions.
     */
    public void runCurrentEvaluation() {
        LOGGER.traceEntry();
        final Evaluation eval = GetariApp.get().getCurrentEval();
        try {
            eval.initializeResources();
            eval.compute();
        } catch (final FunctionalException | TechnicalException e) {
            LOGGER.catching(e);
            eval.fireIndicatorEvent(
                    IndicatorEvent.Type.COMPUTE_FAILURE.event(eval));
            AlertUtils.showException(e.getMessage(), e);
        }
    }

    /**
     * Save the current evaluation at path.
     *
     * @param path path for the saved evaluation
     * @return success
     */
    public boolean saveEvaluation(final String path) {

        final EvaluationSettings settings = currentEval.getSettings();
        settings.setEvaluation(GetariApp.get().getCurrentEval());
        try {
            String fileName;

            if (path != null) {
                fileName = path;
                if (!path.endsWith(".gri")) {
                    fileName += ".gri";
                }
            } else {
                fileName = settings.getFilePath();
            }
            settings.setFilePath(fileName);
            settings.setTimestamp(LocalDateTime.now());
            settings.setVersion(Version.getVersionAndBuildDate());

            LOGGER.info("Saving to {}", fileName);

            XMLUtil.serialize(settings, fileName, EvaluationSettings.CLASSES_FOR_JAXB);

            if (currentEval.isNew()) {
                throw new RuntimeException();
            }
            LOGGER.info(currentEval.getSettings().getFilePath());

            mainView.getCurrentTab().setText(settings.getName());

            currentEval.setTranscient(false);
            GetariApp.get().getPreferences().putRecentlyOpened(FileType.EVALUATION, fileName);
            getEventBus().fireEvent(new EvaluationSaveEvent());
            return true;
        } catch (final TechnicalException e) {
            AlertUtils.showException(
                    Messages.getString("evaluation.error.saving"),
                    e
                    );
            LOGGER.error("Error while saving evaluation.", e);
        }
        return false;
    }
}

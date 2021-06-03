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

import java.io.File;
import java.io.IOException;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.exception.GetariException;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariFileChooser;
import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.getari.view.CsvView;
import fr.inrae.agroclim.getari.view.FxmlView;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.getari.view.MultiExecutionView;
import fr.inrae.agroclim.getari.view.ResultsView;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.data.DataLoadingListener;
import fr.inrae.agroclim.indicators.xml.XMLUtil;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Contrôleur des évènements déclenchés par le menu principal de l'application.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MainViewController extends AbstractController implements EventHandler<ActionEvent> {

    /**
     * View with menus.
     */
    private final MainView mainView;

    /**
     * Constructor.
     *
     * @param view view with menus
     */
    public MainViewController(final MainView view) {
        this.mainView = view;
    }

    /**
     * @param graphView graph view
     * @param evaluationFile evaluation file
     * @throws GetariException while creating exception
     * @throws IOException should never occurs when building view
     */
    private void createEvaluation(final GraphView graphView,
            final File evaluationFile) throws GetariException, IOException {
        EvaluationSettings settings;
        try {
            settings = (EvaluationSettings) XMLUtil.load(evaluationFile, EvaluationSettings.CLASSES_FOR_JAXB);
        } catch (final TechnicalException e) {
            AlertUtils.showException(
                    Messages.getString("evaluation.wrongxml",
                            evaluationFile.getAbsolutePath()),
                    e
                    );
            throw new GetariException(e);
        }
        DataLoadingListener dataLoadingListener;
        dataLoadingListener = graphView.getDataLoadingListener();
        settings.initializeKnowledge();
        settings.getKnowledge().setI18n(settings.getEvaluation());
        settings.setFilePath(evaluationFile.getAbsolutePath());
        settings.getClimateLoader().getFile().addDataLoadingListener(dataLoadingListener);
        settings.getPhenologyLoader().getFile().addDataLoadingListener(dataLoadingListener);

        GetariApp.createEvaluation(settings, graphView);
    }

    @Override
    protected Stage getStage() {
        return mainView.getStage();
    }

    @Override
    public void handle(final ActionEvent t) {

        String menuItemTitle;
        String menuParentTitle = null;

        if (t.getSource() instanceof MenuItem) {
            final MenuItem menuItem = (MenuItem) t.getSource();
            LOGGER.trace("Action on id={}", menuItem.getId());
            logAppend(menuItem.getId());
            menuItemTitle = menuItem.getText();
            if (menuItem.getId() != null) {
                if (menuItem.getId().startsWith(MenuController.MenuPrefixId.RECENT_EVALUATION.getKey())) {
                    final File file = new File(menuItemTitle);
                    openEvaluationFile(file, null);
                    return;
                }
                if (menuItem.getId().startsWith(MenuController.MenuPrefixId.RECENT_MULTIEXECUTION.getKey())) {
                    final File file = new File(menuItemTitle);
                    openMEFile(file);
                    return;
                }
            }
            menuParentTitle = menuItem.getParentMenu().getText();
        } else {
            menuItemTitle = ((Styleable) t.getSource()).getId();
            logAppend(menuItemTitle);
        }

        LOGGER.trace("menuItemTitle={}", menuItemTitle);
        if (Messages.getString("action.new").equals(menuItemTitle)) {
            logAppend("Menu new");
            onNewEvaluationAction();
        } else if (Messages.getString("action.open").equals(menuItemTitle)) {
            logAppend("Menu open");
            final String resultTitle = Messages.getString("action.result");
            final FileType type;
            if (menuParentTitle != null && menuParentTitle.equals(resultTitle)) {
                type = FileType.RESULTS;
            } else {
                type = FileType.EVALUATION;
            }
            showOpenFileDialog(type);
        } else if (Messages.getString("action.close").equals(menuItemTitle)) {
            logAppend("Menu close");
            mainView.closeCurrentTab();
        }
    }

    /**
     * When user clicks on New evalution menu/button.
     */
    protected void onNewEvaluationAction() {
        logAppend("createEvaluation");
        try {
            new FxmlView("createEvaluation").build(mainView.getStage());
        } catch (final IOException e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * When user clicks on New Multi execution menu.
     */
    public void onNewME() {
        logAppend("createMultiExecution");
        final MultiExecutionView view = new MultiExecutionView();
        final MultiExecution me = new MultiExecution();
        try {
            final Tab tab = mainView.addMETab(view);
            view.getController().setTab(tab);
            view.getController().load(me);
        } catch (final IOException e) {
            LOGGER.catching(e);
        }
    }

    /**
     * Create and add a tab with CSV values from a file.
     *
     * @param file CSV file
     */
    public void openCsvFile(final File file) {
        final CsvView view = new CsvView();
        try {
            final Node node = view.build();
            final String title = file.getName();
            final String tooltip = file.getAbsolutePath();
            mainView.addTab("csvTab", node, title, tooltip,
                    "attached-icon-24x24.png");
            view.getController().loadFile(file);
        } catch (final IOException ex) {
            LOGGER.catching(ex);
        }
    }

    /**
     * @param file evaluation file to open
     * @param cmd to run on success
     */
    public void openEvaluationFile(final File file, final Runnable cmd) {
        LOGGER.trace("start " + file.getAbsolutePath());
        GetariApp.openEvaluationTab(graphView -> {
            try {
                createEvaluation(graphView, file);
            } catch (GetariException | IOException ex) {
                LOGGER.catching(ex);
            }
        });

        final Evaluation evaluation = GetariApp.get().getCurrentEval();
        if (evaluation.getIndicators().isEmpty()) {
            LOGGER.warn(
                    "Evaluation {} does not contain phases!",
                    evaluation.getName());
        } else {
            evaluation.getIndicators().forEach(ind -> LOGGER.trace("Evaluation phase : " + ind.getName()));
        }

        GetariApp.get().getPreferences().putRecentlyOpened(FileType.EVALUATION, file.getAbsolutePath());
        if (cmd != null) {
            cmd.run();
        }
    }

    /**
     * Create and add a tab with Multi-Evaluation from a file.
     *
     * @param file Multi-Evaluation file
     */
    public void openMEFile(final File file) {
        final MultiExecutionView view = new MultiExecutionView();
        try {
            final Tab tab = mainView.addMETab(view);
            view.getController().setTab(tab);
            if (view.getController().loadFile(file)) {
                GetariApp.get().getPreferences().putRecentlyOpened(FileType.MULTIEXECUTION, file.getAbsolutePath());
            } else {
                ComponentUtil.closeTab(tab);
                GetariApp.get().getPreferences().removeRecentlyOpened(FileType.MULTIEXECUTION, file.getAbsolutePath());
            }
        } catch (final IOException e) {
            LOGGER.catching(e);
        }
    }

    /**
     * Load results from file into view.
     *
     * @param resultFile result file
     */
    public void openResultFile(final File resultFile) {
        try {
            final ResultsView view = new ResultsView();
            final Evaluation evaluation = new Evaluation();
            mainView.addResultsTab(evaluation, view, resultFile);
            view.getController().loadResults(resultFile);
        } catch (final IOException ex) {
            LOGGER.catching(ex);
        }
    }

    /**
     * Display the system GetariFileChooser to open files.
     *
     * @param type type of file to open
     */
    protected void showOpenFileDialog(final FileType type) {
        final GetariFileChooser chooser = new GetariFileChooser(type);
        final File file = chooser.showOpenDialog(mainView.getStage());

        if (file != null) {
            logAppend("openDialog", file.getAbsolutePath());
            switch (type) {
            case RESULTS:
                final String ext = StringUtils.extension(file.getName());
                switch (ext) {
                case "csv":
                    openCsvFile(file);
                    break;
                case "out":
                    openResultFile(file);
                    break;
                default:
                    throw new UnsupportedOperationException("Extension not handled: " + ext);
                }
                break;
            case EVALUATION:
                openEvaluationFile(file, () -> mainView.getDialog().close());
                break;
            case MULTIEXECUTION:
                openMEFile(file);
                mainView.getDialog().close();
                break;
            default:
                throw new UnsupportedOperationException("FileType not handled: " + type);
            }
        }
    }

}

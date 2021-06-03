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
package fr.inrae.agroclim.getari.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.GetariPreferences;
import fr.inrae.agroclim.getari.event.EvaluationSaveEvent;
import fr.inrae.agroclim.getari.event.RecentUpdateEvent;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariFileChooser;
import fr.inrae.agroclim.getari.view.FxmlView;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.getari.view.ResultsView;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * View controller for the menu.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MenuController extends AbstractController implements IndicatorListener, Initializable {

    /**
     * Prefixes for built menus.
     */
    @RequiredArgsConstructor
    public enum MenuPrefixId {
        /**
         * Evaluation / recents.
         */
        RECENT_EVALUATION("open.recent.evaluation."),
        /**
         * MultiExecution / recents.
         */
        RECENT_MULTIEXECUTION("open.recent.multiexecution.");
        /**
         * Prefix key for the menu item.
         */
        @Getter
        private final String key;
    }

    /**
     * Help / languages.
     */
    @FXML
    private Menu languages;

    /**
     * Main view.
     */
    @Setter
    private MainView mainView;

    /**
     * The root node.
     */
    @FXML
    private MenuBar menuBar;

    /**
     * Offset X for drag'n drop.
     */
    private double mouseDragOffsetX = 0;

    /**
     * Offset Y for drag'n drop.
     */
    private double mouseDragOffsetY = 0;

    /**
     * MultiExecution / recent.
     */
    @FXML
    private Menu recentlyOpenME;

    /**
     * Evaluation / recent.
     */
    @FXML
    private Menu recentlyOpenedMenu;

    /**
     * Evaluation / run.
     */
    @FXML
    private MenuItem runEvaluation;

    /**
     * Evaluation / save as.
     */
    @FXML
    private MenuItem saveasEvaluation;

    /**
     * Results / save as.
     */
    @FXML
    private MenuItem saveasResults;

    /**
     * Evaluation / save.
     */
    @FXML
    private MenuItem saveEvaluation;

    /**
     * Menu : Recently opened items.
     *
     * @param menu menu to build
     * @param type related file type
     * @param prefix related menu prefix for the file type
     */
    private void buildRecentlyOpenedMenu(final Menu menu, final FileType type, final MenuPrefixId prefix) {
        int i = 0;
        final List<String> paths = GetariApp.get().getPreferences().getExistingRecentlyOpened(type);
        Collections.reverse(paths);
        menu.getItems().clear();
        for (final String path : paths) {
            final MenuItem evalItem = new MenuItem(path);
            evalItem.setMnemonicParsing(false);
            evalItem.setId(prefix.getKey() + i);
            evalItem.setOnAction(mainView.getController());
            menu.getItems().add(evalItem);
            i++;
        }
    }

    /**
     * Recently opened evaluations and Recently opend multiexecutions.
     */
    private void buildRecentlyOpenedMenus() {
        buildRecentlyOpenedMenu(recentlyOpenedMenu, FileType.EVALUATION, MenuPrefixId.RECENT_EVALUATION);
        buildRecentlyOpenedMenu(recentlyOpenME, FileType.MULTIEXECUTION, MenuPrefixId.RECENT_MULTIEXECUTION);
    }

    @Override
    protected Stage getStage() {
        return mainView.getStage();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // add menu items
        // Evaluation / recently open
        GetariApp.get().getEventBus().addEventHandler(
                RecentUpdateEvent.TYPE,
                event -> buildRecentlyOpenedMenus()
                );
        GetariApp.get().getEventBus().addEventHandler(
                EvaluationSaveEvent.TYPE,
                event -> updateEvaluationMenu(GetariApp.get().getCurrentEval())
                );
        // Help / Languages
        Messages.getSupportedLocales().forEach(locale -> {
            final MenuItem lang = new MenuItem(locale.getDisplayName());
            lang.setOnAction((final ActionEvent arg0) -> {
                GetariApp.get().getPreferences().setLocale(locale);
                AlertUtils.showInfo(
                        Messages.getString("action.languages"),
                        Messages.getString("application.restart.languages")
                        );
            });
            languages.getItems().add(lang);
        });

        /*
         * Drag'n drop.
         */
        menuBar.setOnMousePressed((final MouseEvent event) -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });

        menuBar.setOnMouseDragged((final MouseEvent event) -> {
            getStage().setX(event.getScreenX() - mouseDragOffsetX);
            getStage().setY(event.getScreenY() - mouseDragOffsetY);
        });
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onAbout(final ActionEvent event) {
        logAppend("Menu About");
        try {
            new FxmlView("about").build(getStage());
        } catch (final IOException e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCloseEvaluation(final ActionEvent event) {
        logAppend("Menu Close Evaluation");
        mainView.getController().handle(event);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onExit(final ActionEvent event) {
        logAppend("Menu Exit");
        GetariApp.close();
    }

    @Override
    public void onIndicatorEvent(final IndicatorEvent event) {
        Evaluation e;
        if (!(event.getSource() instanceof Evaluation)) {
            e = GetariApp.get().getCurrentEval();
        } else {
            e = (Evaluation) event.getSource();
            if (!e.equals(GetariApp.get().getCurrentEval())) {
                return;
            }
        }
        switch (event.getAssociatedType()) {
        case ADD:
        case CHANGE:
        case REMOVE:
            runEvaluation.setDisable(e.isOnErrorOrIncomplete(false));
            saveEvaluation.setDisable(!e.isTranscient());
            saveasEvaluation.setDisable(!e.isTranscient());
            break;
        case CLIMATIC_MISSING:
            // Do nothing.
            break;
        case COMPUTE_FAILURE:
        case COMPUTE_SUCCESS:
            runEvaluation.setDisable(false);
            break;
        case AGGREGATION_MISSING:
        case COMPUTE_START:
        case NOT_COMPUTABLE:
            runEvaluation.setDisable(true);
            break;
        case UPDATED_VALUE:
            // Do nothing.
            break;
        default:
            LOGGER.info("IndicatorEvent.Type {} not handled!", event.getAssociatedType());
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onNewEvaluation(final ActionEvent event) {
        logAppend("Menu New Evaluation");
        mainView.getController().handle(event);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onNewME(final ActionEvent event) {
        logAppend("Menu New ME");
        mainView.getController().onNewME();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onOpenEvaluation(final ActionEvent event) {
        logAppend("Menu Open Evaluation");
        mainView.getController().handle(event);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onOpenME(final ActionEvent event) {
        logAppend("Menu Open ME");
        final GetariFileChooser chooser = new GetariFileChooser(FileType.MULTIEXECUTION);
        final File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            mainView.getController().openMEFile(file);
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onOpenResults(final ActionEvent event) {
        logAppend("Menu Open Results");
        mainView.getController().handle(event);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onReleaseNotes(final ActionEvent event) {
        logAppend("Menu Release Notes");
        try {
            new FxmlView("releasenotes").build(getStage());
        } catch (final IOException e) {
            LOGGER.fatal("Error while loading Release Notes view.", e);
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onRunEvaluation(final ActionEvent event) {
        logAppend("Menu Run Evaluation");
        GetariApp.get().runCurrentEvaluation();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onRunME(final ActionEvent event) {
        logAppend("Menu Run ME");
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveasEvaluation(final ActionEvent event) {
        logAppend("Menu Save Evaluation As");
        final GetariFileChooser chooser = new GetariFileChooser(FileType.EVALUATION);

        final File file = chooser.showSaveDialog(getStage());
        if (file != null) {
            final String absolutePath = file.getParentFile().getAbsolutePath();
            final GetariPreferences prefs = GetariApp.get().getPreferences();
            prefs.setLastDirectory(FileType.EVALUATION, absolutePath);
            GetariApp.setLastDirectory(absolutePath);
            GetariApp.get().saveEvaluation(file.getAbsolutePath());
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveasME(final ActionEvent event) {
        logAppend("Menu Save ME As");
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveasResults(final ActionEvent event) {
        logAppend("onSaveasResults");
        final ResultsView resultsView;
        resultsView = GetariApp.get().getCurrentResultView();
        if (resultsView == null) {
            LOGGER.fatal("Current EvaluationResultView must not be null!");
            return;
        }
        if (resultsView.getController().getSaveButton() == null) {
            LOGGER.fatal("Save button must not be null!");
            return;
        }
        resultsView.getController().getSaveButton().fire();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveEvaluation(final ActionEvent event) {
        logAppend("Menu Save Evaluation");
        GetariApp.get().saveEvaluation(null);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveME(final ActionEvent event) {
        logAppend("Menu Save ME");
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSupport(final ActionEvent event) {
        logAppend("onSupport");
        try {
            new FxmlView("support").build(getStage());
        } catch (final IOException e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * Disable/enabled "Save as" menu item.
     *
     * @param disabled disabled=true
     */
    public void setSaveResultsItemDisabled(final boolean disabled) {
        saveasResults.setDisable(disabled);
    }

    /**
     * Switching between tabs must refresh the menu.
     *
     * @param e current evaluation
     */
    public void updateEvaluationMenu(final Evaluation e) {
        if (e == null) {
            runEvaluation.setDisable(true);
            saveEvaluation.setDisable(true);
            saveasEvaluation.setDisable(true);
            return;
        }
        runEvaluation.setDisable(e.isOnErrorOrIncomplete(false));
        saveEvaluation.setDisable(!e.isTranscient());
        saveasEvaluation.setDisable(!e.isTranscient());
    }
}

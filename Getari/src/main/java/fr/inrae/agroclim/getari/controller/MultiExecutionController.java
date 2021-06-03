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
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariFileChooser;
import fr.inrae.agroclim.getari.util.MultiExecutionHelper;
import fr.inrae.agroclim.getari.util.MultiExecutionService;
import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.data.FileLoader;
import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Edit and run MultiExecution.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MultiExecutionController extends AbstractController implements Initializable {

    /**
     * Cell with action button (delete).
     */
    private class TableActionCell extends TableCell<Execution, String> {

        @Override
        protected void updateItem(final String value, final boolean empty) {
            super.updateItem(value, empty);
            if (empty) {
                setGraphic(null);
            } else {
                final VBox vbox = new VBox();
                final Optional<Map<FileType, String>> hasErrors;
                hasErrors = MultiExecutionHelper.getErrors(executions.get(getIndex()));
                hasErrors.ifPresent(strings -> {
                    final Text errors = new Text();
                    errors.setText(String.join("\n", strings.values()));
                    errors.wrappingWidthProperty().bind(actionColumn.widthProperty());
                    vbox.getChildren().add(errors);
                });
                final HBox hbox = new HBox();
                vbox.getChildren().add(hbox);
                final Button delete = new Button(Messages.getString("action.delete"));
                hbox.getChildren().add(delete);
                delete.setOnAction(e -> executions.remove(this.getIndex()));
                final Button duplicate = new Button(Messages.getString("action.duplicate.multiexecution"));
                hbox.getChildren().add(duplicate);
                duplicate.setOnAction(e -> {
                    final Execution execution = executions.get(this.getIndex());
                    final Execution newExecution = new Execution();
                    newExecution.setClimate(execution.getClimate());
                    newExecution.setOutput(MultiExecutionHelper.suggestedOutput(executions));
                    newExecution.setPhenology(execution.getPhenology());
                    executions.add(newExecution);
                });
                setGraphic(vbox);
            }
        }

    }

    /**
     * Cell with file chooser for the file type.
     */
    private class TableChooserCell extends TableCell<Execution, String> {

        /**
         * Related file type.
         */
        private final FileType type;

        /**
         * Constructor.
         *
         * @param fileType related file type
         */
        TableChooserCell(final FileType fileType) {
            type = fileType;
            setOnMouseClicked(eh -> chooseFile(this.getIndex()));
        }

        /**
         * Display file chooser and set result in the item.
         *
         * @param index row index
         */
        private void chooseFile(final int index) {
            final GetariFileChooser chooser = new GetariFileChooser(type);
            final File file;
            final Execution execution = executions.get(index);
            final String filename = MultiExecutionHelper.get(type, execution);
            if (StringUtils.isNotBlank(filename)) {
                chooser.setInitialFileName(filename);
            }
            if (type == FileType.RESULTS) {
                file = chooser.showSaveDialog(MultiExecutionController.this.getStage());
            } else {
                file = chooser.showOpenDialog(MultiExecutionController.this.getStage());
            }
            if (file != null) {
                MultiExecutionHelper.set(type, execution, file.getAbsolutePath());
                table.refresh();
                refreshInterface();
            }
        }

        /**
         * Shorten file path with relative file path.
         *
         * @param path path to relativize
         * @return relative file path
         */
        private String relativize(final String path) {
            if (path == null) {
                return null;
            }
            if (StringUtils.isBlank(evaluation.getText())) {
                return path;
            }
            final Path absolutePath = Paths.get(path);
            return loader.relativize(absolutePath);
        }

        @Override
        protected void updateItem(final String value, final boolean empty) {
            super.updateItem(value, empty);
            if (empty) {
                setGraphic(null);
            } else {
                final Execution execution = executions.get(getIndex());
                final String path = MultiExecutionHelper.get(type, execution);
                setGraphic(new Text(relativize(path)));
                setTooltip(new Tooltip(path));
            }
        }

    }

    /**
     * Start time of MultiExecution run.
     */
    private long executionStartTime;

    /**
     * Counter for failed executions.
     */
    private final AtomicInteger failedExecutions = new AtomicInteger();

    /**
     * Counter for succeeded executions.
     */
    private final AtomicInteger succeededExecutions = new AtomicInteger();

    /**
     * Tab containing the view.
     */
    @Setter
    private Tab tab;

    /**
     * I18n messages.
     */
    private I18n msg;

    /**
     * File loader to relativize paths.
     */
    private final FileLoader loader = new FileLoader();

    /**
     * Text log widget.
     */
    @FXML
    private TextArea log;

    /**
     * Thread management.
     */
    private final MultiExecutionService executor = new MultiExecutionService();

    /**
     * Executions details and choose.
     */
    @FXML
    private TableView<Execution> table;

    /**
     * To act on MultiExecution and Execution.
     */
    @FXML
    private TableColumn<Execution, String> actionColumn;

    /**
     * Root node.
     */
    @FXML
    private BorderPane root;

    /**
     * Cancel executions.
     */
    @FXML
    private Button cancel;

    /**
     * Execution.climate.
     */
    @FXML
    private TableColumn<Execution, String> climateColumn;

    /**
     * Evaluation file path.
     */
    @FXML
    private TextField evaluation;

    /**
     * List of execution to display.
     */
    private final ObservableList<Execution> executions = FXCollections.observableArrayList();

    /**
     * Execution.climate.
     */
    @FXML
    private TableColumn<Execution, String> outputColumn;

    /**
     * Execution.pheno.
     */
    @FXML
    private TableColumn<Execution, String> phenoColumn;

    /**
     * Execution progress.
     */
    @FXML
    private ProgressBar progressBar;

    /**
     * To run the MultiExecution.
     */
    @FXML
    private Button run;

    /**
     * To save the MultiExecution.
     */
    @FXML
    private Button save;

    /**
     * Append text to log area with new line and keep sroll at bottom.
     *
     * @param text text to append
     */
    private void appendTextToLog(final String text) {
        Platform.runLater(() -> {
            log.appendText(text + "\n");
            log.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * @return object from the edited tab
     */
    private MultiExecution getMultiExecution() {
        final MultiExecution me = new MultiExecution();
        me.setEvaluation(evaluation.getText());
        me.setVersion(Version.getString("version"));
        me.setTimestamp(LocalDateTime.now());
        me.setExecutions(executions);
        return me;
    }

    @Override
    protected Stage getStage() {
        return (Stage) log.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        msg = new I18n(rb);
        // auto resize columns
        final int nbCols = 4;
        final DoubleBinding colWidth = table.widthProperty().subtract(root.getPadding().getLeft()).divide(nbCols);
        climateColumn.prefWidthProperty().bind(colWidth);
        phenoColumn.prefWidthProperty().bind(colWidth);
        outputColumn.prefWidthProperty().bind(colWidth);
        actionColumn.prefWidthProperty().bind(colWidth);
        // table
        climateColumn.setCellFactory(tableColumn -> new TableChooserCell(FileType.CLIMATE));
        phenoColumn.setCellFactory(tableColumn -> new TableChooserCell(FileType.PHENOLOGY));
        outputColumn.setCellFactory(tableColumn -> new TableChooserCell(FileType.RESULTS));
        actionColumn.setCellFactory(tableColumn -> new TableActionCell());
        climateColumn.setCellValueFactory(new PropertyValueFactory<>("climate"));
        phenoColumn.setCellValueFactory(new PropertyValueFactory<>("phenology"));
        outputColumn.setCellValueFactory(new PropertyValueFactory<>("output"));
        table.setItems(executions);
        executions.addListener((final ListChangeListener.Change<? extends Execution> c) -> refreshInterface());
        executor.setOnFailed((nb, e) -> {
            appendTextToLog(msg.format("execution.failed", nb, e.getMessage()));
            failedExecutions.addAndGet(1);
        });
        executor.setOnSucceeded(nb -> {
            appendTextToLog(msg.format("execution.succeeded", nb));
            succeededExecutions.addAndGet(1);
        });
        // evaluation changes
        evaluation.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) {
                loader.setBaseDirectory(null);
            } else {
                final Path path = Paths.get(newText);
                loader.setBaseDirectory(path.getParent());
            }
            refreshInterface();
            table.refresh();
        });
        // executions
        executor.getProgressProperty().addListener((obs, oldVal, val) -> {
            final double progress = val.doubleValue();
            final int size = executor.getSizeProperty().get();
            if (progress == size) {
                onExecutionsEnd();
            }
            double value;
            if (size == 0) {
                value = 0D;
            } else {
                value = progress / size;
            }
            progressBar.setProgress(value);
        });
        // hide log until run
        root.getBottom().setVisible(false);
    }

    /**
     * Load object into view.
     *
     * @param value object to display
     */
    public void load(final MultiExecution value) {
        evaluation.setText(value.getEvaluation());
        executions.clear();
        if (value.getExecutions() != null && !value.getExecutions().isEmpty()) {
            executions.addAll(value.getExecutions());
        }
        refreshInterface();
    }

    /**
     * @param file XML file with Multi-Evaluation
     * @return success
     */
    public boolean loadFile(final File file) {
        MultiExecution me;
        try {
            LOGGER.info(file.getAbsoluteFile());
            final MultiExecution deserialized = MultiExecutionHelper.deserialize(file);
            me = MultiExecutionHelper.resolve(file.getAbsolutePath(), deserialized);
        } catch (final TechnicalException ex) {
            AlertUtils.showException("", ex);
            return false;
        }
        if (tab != null) {
            final Tooltip tooltip = new Tooltip();
            tooltip.setText(file.getAbsolutePath());
            tab.setTooltip(tooltip);
            tab.setText(file.getName());
        }
        load(me);
        return true;
    }

    /**
     * Add a new row.
     *
     * @param event event not used
     */
    @FXML
    private void onAddAction(final ActionEvent event) {
        logAppend("Button Add Execution");
        final Execution e = new Execution();
        e.setOutput(MultiExecutionHelper.suggestedOutput(executions));
        executions.add(e);
    }

    /**
     * Cancel run of MultiExecution.
     *
     * @param event event not used
     */
    @FXML
    private void onCancelAction(final ActionEvent event) {
        logAppend("Button Cancel MultiExecution");
        appendTextToLog(msg.get("multiexecution.cancelled"));
        cancel.setDisable(true);
        executor.shutdownNow();
        progressBar.setProgress(0);
    }

    /**
     * Choose evaluation file.
     *
     * @param event event not used
     */
    @FXML
    private void onChooseEvaluationAction(final ActionEvent event) {
        logAppend("Button Choose Evaluation");
        final GetariFileChooser chooser = new GetariFileChooser(FileType.EVALUATION);
        final File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            evaluation.setText(file.getAbsolutePath());
        }
    }

    /**
     * Clear log.
     *
     * @param event event not used
     */
    @FXML
    private void onClearAction(final ActionEvent event) {
        logAppend("Button Clear");
        log.setText("");
    }

    /**
     * Close tab.
     *
     * @param event event not used
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("Button Close MultiExecution");
        GetariApp.getMainView().closeCurrentTab();
    }

    /**
     * When MultiExecution ends.
     */
    private void onExecutionsEnd() {
        cancel.setDisable(true);
        final double msInSecond = 1000;
        final double duration = (new Date().getTime() - executionStartTime) / msInSecond;
        appendTextToLog(msg.format((int) Math.round(duration), "execution.duration", duration));
        final int failedNb = failedExecutions.get();
        if (failedNb > 0) {
            appendTextToLog(msg.format(failedNb, "executions.failed", failedNb));
        }
        final int succeededNb = succeededExecutions.get();
        if (succeededNb > 0) {
            appendTextToLog(msg.format(succeededNb, "executions.succeeded", succeededNb));
        }
    }

    /**
     * Run MultiExecution.
     *
     * @param event event not used
     */
    @FXML
    private void onRunAction(final ActionEvent event) {
        logAppend("Button Run MultiExecution");
        executionStartTime = new Date().getTime();
        failedExecutions.set(0);
        succeededExecutions.set(0);
        progressBar.setProgress(0);
        root.getBottom().setVisible(true);
        cancel.setDisable(false);
        Configurator.setLevel("fr.inrae.agroclim", Level.TRACE);
        executor.submit(getMultiExecution());
        appendTextToLog(msg.format(executions.size(), "execution.count", executions.size()));
    }

    /**
     * Save MultiExecution.
     *
     * @param event event not used
     */
    @FXML
    private void onSaveAction(final ActionEvent event) {
        logAppend("Button Save MultiExecustion");
        final GetariFileChooser chooser = new GetariFileChooser(FileType.MULTIEXECUTION);
        final File file = chooser.showSaveDialog(getStage());
        final MultiExecution me = getMultiExecution();
        if (file != null) {
            try {
                final MultiExecution relativized = MultiExecutionHelper.relativize(file.getAbsolutePath(), me);
                MultiExecutionHelper.serialize(relativized, file);
                GetariApp.get().getPreferences().putRecentlyOpened(FileType.MULTIEXECUTION, file.getAbsolutePath());
                final Tooltip tooltip = new Tooltip();
                tooltip.setText(file.getAbsolutePath());
                tab.setTooltip(tooltip);
                tab.setText(file.getName());
            } catch (final TechnicalException e) {
                AlertUtils.showException(Messages.getString("evaluation.error.saving"), e);
                LOGGER.error("Error while saving multiexecution.", e);
            }
        }
    }

    /**
     * Refresh widget states.
     */
    private void refreshInterface() {
        final boolean isNotValid = !MultiExecutionHelper.isValid(getMultiExecution());
        run.setDisable(isNotValid);
        save.setDisable(isNotValid);
    }

}

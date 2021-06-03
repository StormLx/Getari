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
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.component.EvaluationTextField;
import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.exception.GetariException;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.NameableListCell;
import fr.inrae.agroclim.getari.view.SelectDataView;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.EvaluationType;
import fr.inrae.agroclim.indicators.model.TimeScale;
import fr.inrae.agroclim.indicators.model.data.climate.ClimateFileLoader;
import fr.inrae.agroclim.indicators.model.data.climate.ClimateLoaderProxy;
import fr.inrae.agroclim.indicators.model.data.phenology.PhenologyFileLoader;
import fr.inrae.agroclim.indicators.model.data.phenology.PhenologyLoaderProxy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * View controller to create an evaluation.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class CreateEvaluationController extends AbstractController implements Initializable {

    /**
     * Create button.
     */
    @FXML
    private Button create;

    /**
     * File path for climatic data.
     */
    @FXML
    private EvaluationTextField climaticFileName;

    /**
     * Evaluation name.
     */
    @FXML
    private EvaluationTextField evaluationName;

    /**
     * Evaluation timescale.
     */
    @FXML
    private ComboBox<TimeScale> evaluationTimescale;

    /**
     * Evaluation type.
     */
    @FXML
    private ComboBox<EvaluationType> evaluationType;

    /**
     * File path for pheno data.
     */
    @FXML
    private EvaluationTextField phenoFileName;

    /**
     * EvaluationSettings to create.
     */
    private final EvaluationSettings settings = new EvaluationSettings();

    @Override
    protected Stage getStage() {
        return (Stage) create.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        settings.setClimate(new ClimateLoaderProxy());
        settings.getClimateLoader().setFile(new ClimateFileLoader());
        settings.getClimateLoader().getFile().setSeparator("\t");
        settings.setPhenologyLoader(new PhenologyLoaderProxy());
        settings.getPhenologyLoader().setFile(new PhenologyFileLoader());
        settings.getPhenologyLoader().getFile().setSeparator(";");
        evaluationName.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    logAppend("evaluationName", newValue);
                    onNameChanged();
                });
        climaticFileName.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    logAppend("climaticFileName", newValue);
                    if (newValue.isEmpty()) {
                        climaticFileName.setInvalid();
                    } else {
                        climaticFileName.setValid();
                    }
                });
        phenoFileName.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    logAppend("phenoFileName", newValue);
                    if (newValue.isEmpty()) {
                        phenoFileName.setInvalid();
                    } else {
                        phenoFileName.setValid();
                    }
                });
        NameableListCell.setCellFactory(evaluationTimescale);
        evaluationTimescale.getItems().addAll(TimeScale.values());
        evaluationTimescale.setValue(settings.getTimescale());
        NameableListCell.setCellFactory(evaluationType);
        evaluationType.getItems().addAll(EvaluationType.values());
        evaluationType.setValue(settings.getType());
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCancelAction(final ActionEvent event) {
        logAppend("cancel");
        close();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onClimaticfileAction(final ActionEvent event) {
        logAppend("climaticfile");
        final FileType type = FileType.CLIMATE;
        final Optional<File> choice = ComponentUtil.chooseDataFile(type, getStage());
        if (choice.isPresent()) {
            final File file = choice.get();
            try {
                final SelectDataView view = new SelectDataView();
                view.build(getStage());
                final SelectDataController controller = view.getController();
                controller.setFile(file);
                controller.setTimeScale(evaluationTimescale.getValue());
                controller.setType(type);
                controller.setOkCallback((values, midnight) -> {
                    final String separator = values.get(0);
                    values.remove(0);
                    String[] headers = new String[values.size()];
                    headers = values.toArray(headers);
                    ClimateFileLoader loader;
                    loader = settings.getClimateLoader().getFile();
                    loader.setTimeScale(evaluationTimescale.getValue());
                    loader.setMidnight(midnight);
                    loader.setFile(file);
                    loader.setSeparator(separator);
                    loader.setHeaders(headers);
                    climaticFileName.setText(file.getAbsolutePath());
                    climaticFileName.setValid();
                });
                controller.initializeView();
            } catch (final IOException ex) {
                LOGGER.fatal("this should never occur!", ex);
            }
        } else {
            climaticFileName.setInvalid();
        }
    }

    /**
     * Open a new tab with the evaluation just created.
     *
     * @param event onAction of button
     */
    @FXML
    private void onCreateAction(final ActionEvent event) {
        logAppend("create");
        settings.setTimescale(evaluationTimescale.getValue());
        settings.setType(evaluationType.getValue());
        GetariApp.openEvaluationTab(graphView -> {
            try {
                GetariApp.createEvaluation(settings, graphView);
            } catch (GetariException | IOException ex) {
                LOGGER.catching(ex);
            }
        });
        close();
        if (GetariApp.getMainView().getDialog() != null) {
            GetariApp.getMainView().getDialog().close();
        }
    }

    /**
     * @param event onAction of combobox changed
     */
    @FXML
    private void onEvaluationTimescaleAction(final ActionEvent event) {
        logAppend("evaluationType", evaluationTimescale.getValue().getName());
    }

    /**
     * @param event onAction of combobox changed
     */
    @FXML
    private void onEvaluationTypeAction(final ActionEvent event) {
        logAppend("evaluationType", evaluationType.getValue().getName());
    }

    /**
     * Fired when evaluation name is changed.
     */
    private void onNameChanged() {
        Platform.runLater(() -> {
            logAppend("evaluationNameChanged", evaluationName.getText());
            if (evaluationName.getText().isEmpty()) {
                evaluationName.setInvalid();
            } else {
                settings.setName(evaluationName.getText());
                evaluationName.setValid();
            }
        });
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onPhenofileAction(final ActionEvent event) {
        logAppend("phenofile");
        final FileType type = FileType.PHENOLOGY;
        final Optional<File> choice = ComponentUtil.chooseDataFile(type, getStage());
        if (choice.isPresent()) {
            final File file = choice.get();
            logAppend("phenofile", file.getAbsolutePath());
            try {
                final SelectDataView view = new SelectDataView();
                view.build(getStage());
                final SelectDataController controller = view.getController();
                controller.setFile(file);
                controller.setType(type);
                controller.setOkCallback((values, midnight) -> {
                    final String separator = values.get(0);
                    values.remove(0);
                    String[] headers = new String[values.size()];
                    headers = values.toArray(headers);
                    PhenologyFileLoader loader;
                    loader = settings.getPhenologyLoader().getFile();
                    loader.setFile(file);
                    loader.setSeparator(separator);
                    loader.setHeaders(headers);
                    phenoFileName.setText(file.getAbsolutePath());
                    phenoFileName.setValid();
                });
                controller.initializeView();
            } catch (final IOException ex) {
                LOGGER.fatal("this should never occur!", ex);
            }
        } else {
            phenoFileName.setInvalid();
        }
    }
}

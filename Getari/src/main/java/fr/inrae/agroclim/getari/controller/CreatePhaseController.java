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

import static javafx.collections.FXCollections.observableArrayList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.util.NameableListCell;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.data.ResourceManager;
import fr.inrae.agroclim.indicators.model.data.phenology.PhenologicalResource;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import lombok.Setter;

/**
 * View controller to add a phase in an evaluation.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class CreatePhaseController extends AbstractController implements Initializable {

    /**
     * Add button.
     */
    @FXML
    private Button add;

    /**
     * End stage.
     */
    @FXML
    private ComboBox<Indicator> end;

    /**
     * Command to execute after adding the phase to the evaluation.
     */
    @Setter
    private Consumer<CompositeIndicator> onAction;

    /**
     * Start stage.
     */
    @FXML
    private ComboBox<String> start;

    @Override
    protected final Stage getStage() {
        return (Stage) add.getScene().getWindow();
    }

    @Override
    public final void initialize(final URL location,
            final ResourceBundle resources) {
        NameableListCell.setCellFactory(end);
        final Evaluation eval = GetariApp.get().getCurrentEval();
        final ResourceManager mgr = eval.getResourceManager();
        final PhenologicalResource phenoResource = mgr
                .getPhenologicalResource();

        start.setItems(observableArrayList(phenoResource.getStages()
                .subList(0, phenoResource.getStages().size() - 1)));

        start
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
                (final ObservableValue<? extends String> observable,
                        final String oldValue, final String newValue) -> {
                            logAppend("start", newValue);
                            if (newValue != null) {
                                final List<Indicator> phases = phenoResource
                                        .getPhasesByStage(newValue);
                                final List<Indicator> phasesToDel = new ArrayList<>();
                                eval.getPhases().forEach(phase -> {
                                    if (phase.getFirstIndicator().getName()
                                            .equals(newValue)) {
                                        phases.forEach(p -> {
                                            if (Objects.equals(p.getName(),
                                                    phase.getName())) {
                                                phasesToDel.add(p);
                                            }
                                        });
                                    }
                                });
                                phases.removeAll(phasesToDel);
                                end.setItems(observableArrayList(phases));
                                end.disableProperty().set(false);
                            }
                        });

        end.getSelectionModel()
        .selectedItemProperty()
        .addListener(
                (final ObservableValue<? extends Indicator> observable,
                        final Indicator oldValue, final Indicator newValue) -> {
                            logAppend("end", newValue.getId());
                            add.setDisable(false);
                        });
        // Request focus on the field by default.
        Platform.runLater(start::requestFocus);
    }

    /**
     * @param event event not used
     * @throws CloneNotSupportedException should never occurs as indicator must
     * implement clone()
     */
    @FXML
    private void onAddAction(final ActionEvent event)
            throws CloneNotSupportedException {
        logAppend("add", end.getValue().getId());
        final Evaluation e = GetariApp.get().getCurrentEval();

        final Indicator selectedIndicator = end.getValue();
        selectedIndicator.setParent(e);

        final CompositeIndicator phase = (CompositeIndicator) e.add(
                IndicatorCategory.PHENO_PHASES, e, selectedIndicator);
        e.toAggregate(true);

        if (onAction != null) {
            onAction.accept(phase);
        }

        close();
    }

    /**
     * @param event event not used
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("close");
        close();
    }
}

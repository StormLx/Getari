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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import fr.inrae.agroclim.indicators.model.data.Data;
import fr.inrae.agroclim.indicators.model.data.DataLoadingListener;
import fr.inrae.agroclim.indicators.model.data.climate.ClimaticDailyData;
import fr.inrae.agroclim.indicators.model.data.phenology.AnnualStageData;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Display Evaluation logs.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class LogController implements DataLoadingListener, IndicatorListener, Initializable {

    /**
     * Parameters for a tab.
     */
    @RequiredArgsConstructor
    private enum TabParam {
        /**
         * Errors tab.
         */
        ERROR("errorLabel", "log.errors", "tab.errors", Color.RED),
        /**
         * Warnings tab.
         */
        WARNING("warningLabel", "log.warnings", "tab.warnings", Color.ORANGE);
        /**
         * CSS class for the label.
         */
        @Getter
        private final String labelClass;
        /**
         * Key in .properties file.
         */
        @Getter
        private final String labelI18nKey;
        /**
         * Key in .properties file.
         */
        @Getter
        private final String tabI18nKey;
        /**
         * Color of the item in the list widget.
         */
        @Getter
        private final Color itemColor;
    }

    /**
     * Maximum number of warnings to display.
     */
    private static final int MAX_DISPLAYED_WARNINGS = 500;

    /**
     * Prefix of truncate warning.
     */
    private static final String TRUNCATE = "......\t\t";

    /**
     * Remove errors starting with prefix in pane.
     *
     * @param list list to clean
     * @param prefix prefix of messages to onIndicatorRemove
     */
    private static void clear(final ObservableList<String> list,
            final String prefix) {
        final List<String> toRemove = new ArrayList<>();
        list.stream()
        .filter(label -> label.startsWith(prefix))
        .forEachOrdered(label -> {
            LOGGER.trace("Removing {}", label);
            toRemove.add(label);
        });
        list.removeAll(toRemove);
    }

    /**
     * Translations.
     */
    private String climate;

    /**
     * Translations.
     */
    private String phenology;

    /**
     * Translations.
     */
    private String evaluation;

    /**
     * Tab for errors and warnings.
     */
    @FXML
    private TabPane consoleTab;

    /**
     * Handling errors.
     */
    private final ObservableList<String> errors = FXCollections.observableArrayList();

    /**
     * List to display errors.
     */
    @FXML
    private ListView<String> errorsList;

    /**
     * Localization.
     */
    private I18n msg;

    /**
     * Label to sum up errors.
     */
    @FXML
    private Label errorsLabel;

    /**
     * Tab showing the errors.
     */
    @FXML
    private Tab errorsTab;

    /**
     * Binding for number of errors.
     */
    @Getter
    private NumberBinding numberOfErrors;

    /**
     * Binding for number of warnings.
     */
    private NumberBinding numberOfWarnings;

    /**
     * Handling warnings.
     */
    private final ObservableList<String> warnings
    = FXCollections.observableArrayList();

    /**
     * Label to sum up warnings.
     */
    @FXML
    private Label warnLabel;

    /**
     * List to display warnings.
     */
    @FXML
    private ListView<String> warnList;

    /**
     * Tab showing the warnings.
     */
    @FXML
    private Tab warnTab;

    /**
     * Add "truncate" message in the warnings if MAX_DISPLAYED_WARNINGS.
     */
    private void appendTruncatedWarnings() {
        if (numberOfWarnings.intValue() == MAX_DISPLAYED_WARNINGS) {
            warnings.add(TRUNCATE + msg.format("warning.truncated"));
        }
    }

    /**
     * Remove errors and warnings about climate.
     */
    private void clearClimate() {
        LOGGER.traceEntry();
        clear(errors, climate);
        clear(warnings, TRUNCATE);
        clear(warnings, climate);
        appendTruncatedWarnings();
    }

    /**
     * Remove errors and warnings about evaluation.
     */
    private void clearEvaluation() {
        LOGGER.traceEntry();
        clear(errors, evaluation);
        clear(warnings, TRUNCATE);
        clear(warnings, evaluation);
        appendTruncatedWarnings();
    }

    /**
     * Remove errors and warnings about phenology.
     */
    private void clearPhenology() {
        LOGGER.traceEntry();
        clear(errors, phenology);
        clear(warnings, TRUNCATE);
        clear(warnings, phenology);
        appendTruncatedWarnings();
    }

    /**
     * @return validation errors were encountered on evaluation
     */
    public final BooleanBinding existErrors() {
        return numberOfErrors.greaterThan(0);
    }

    /**
     * @return root node of partial view
     */
    public final Parent getContent() {
        return consoleTab;
    }

    @Override
    public final void initialize(final URL url, final ResourceBundle rb) {
        msg = new I18n(rb);
        climate = msg.get("log.climate");
        phenology = msg.get("log.phenology");
        evaluation = msg.get("log.evaluation");
        numberOfErrors = Bindings.size(errors);
        initializeWidgets(errors, errorsList, numberOfErrors, errorsLabel,
                errorsTab, TabParam.ERROR);
        numberOfWarnings = Bindings.size(warnings);
        initializeWidgets(warnings, warnList, numberOfWarnings, warnLabel,
                warnTab, TabParam.WARNING);
    }

    /**
     * Initialize a tab with its widgets.
     *
     * @param obsList the list containing the strings to display
     * @param listView the list widget in the tab
     * @param nbBinding binding with number of strings
     * @param label label widget in the tab
     * @param tab tab widget
     * @param tabParam parameters for the tab and its widgets.
     */
    private void initializeWidgets(final ObservableList<String> obsList,
            final ListView<String> listView,
            final NumberBinding nbBinding, final Label label,
            final Tab tab,
            final TabParam tabParam) {
        listView.setItems(obsList);
        listView.setCellFactory((final ListView<String> p) -> new ListCell<String>() {
            @Override
            protected void updateItem(final String item,
                    final boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(item);
                    // setting -fx-fill-text in CSS does not work.
                    getStyleClass().add(tabParam.getLabelClass());
                    setTextFill(tabParam.getItemColor());
                }
            }

        });

        label.textProperty().bind(Bindings.createStringBinding(()
                -> msg.format(
                        nbBinding.getValue().intValue(),
                        tabParam.getLabelI18nKey(),
                        nbBinding.getValue()
                        ),
                obsList
                ));
        tab.textProperty().bind(Bindings.createStringBinding(()
                -> msg.format(
                        nbBinding.getValue().intValue(),
                        tabParam.getTabI18nKey(),
                        nbBinding.getValue()
                        ),
                obsList));
    }

    @Override
    public final void onDataLoadingAdd(final Data data) {
        if (data == null) {
            LOGGER.warn("Data should not be null!");
            return;
        }
        final String type;
        if (data instanceof ClimaticDailyData) {
            type = climate + "\t\t";
        } else if (data instanceof AnnualStageData) {
            type = phenology + "\t";
        } else {
            LOGGER.fatal("Data not handled: {}", data.getClass().getName());
            return;
        }
        if (numberOfWarnings.intValue() < MAX_DISPLAYED_WARNINGS) {
            data.getWarnings().forEach(warning -> warnings.add(type + warning));
        }

        data.getErrors().forEach(error -> errors.add(type + error));
    }

    @Override
    public final void onDataLoadingEnd(final String header) {
        appendTruncatedWarnings();
    }

    @Override
    public final void onDataLoadingStart(final String header) {
        LOGGER.trace("do nothing");
    }

    @Override
    public final void onFileSet(final DataFile type) {
        switch (type) {
        case CLIMATIC:
            clearClimate();
            break;
        case PHENOLOGICAL:
            clearPhenology();
            break;
        default:
            LOGGER.fatal("DataFile {} not handled!", type);
        }
    }

    @Override
    public final void onIndicatorEvent(final IndicatorEvent event) {
        final Indicator ind = event.getSource();
        switch (event.getAssociatedType()) {
        case ADD:
            LOGGER.traceEntry("IndicatorEvent ADD {}", ind.getId());
            clearEvaluation();
            break;
        case AGGREGATION_MISSING:
            LOGGER.traceEntry("IndicatorEvent AGGREGATION_MISSING {}",
                    ind.getId());
            String error;
            if (ind.getCategory() == null) {
                /* Cas d'une aggregation manquante pour l'evaluation */
                error = msg.format(
                        "evaluation.aggregation.missing", ind.getName());
            } else {
                error = msg.format(
                        "indicator.aggregation.missing", ind.getId());
            }
            errors.add(error);
            break;
        case CHANGE:
            LOGGER.traceEntry("IndicatorEvent CHANGE {}",
                    ind.getId());
            clearEvaluation();
            Indicator parent = ind.getParent();
            Indicator e = null;
            while (parent != null) {
                parent = parent.getParent();
                if (parent != null) {
                    e = parent;
                }
            }
            if (e instanceof CompositeIndicator) {
                ((CompositeIndicator) e).toAggregate(true);
            }
            break;
        case CLIMATIC_MISSING:
            if ("root-evaluation".equals(ind.getId())) {
                break;
            }
            // Cas d'un indicateur climatique non calculable
            LOGGER.traceEntry("IndicatorEvent CLIMATIC_MISSING {}",
                    ind.getId());
            errors.add(msg.format(
                    "phase.indicator.missing", ind.getId()));
            break;
        case COMPUTE_FAILURE:
            // Do nothing.
            break;
        case COMPUTE_START:
            // Do nothing.
            break;
        case COMPUTE_SUCCESS:
            // Do nothing.
            break;
        case NOT_COMPUTABLE:
            // Cas d'une phase
            LOGGER.traceEntry("IndicatorEvent NOT_COMPUTABLE {}",
                    ind.getId());
            errors.add(msg.format(
                    "evaluation.not.computable", ind.getId()));
            break;
        case PHASE_MISSING:
            // Cas d'un évaluation sans phase
            LOGGER.traceEntry("IndicatorEvent PHASE_MISSING {}",
                    ind.getId());
            errors.add(msg.format(
                    "evaluation.phase.missing", ind.getId()));
            break;
        case REMOVE:
            LOGGER.traceEntry("IndicatorEvent REMOVE {}",
                    ind.getId());
            clearEvaluation();
            break;
        case UPDATED_VALUE:
            // Do nothing.
            break;
        default:
            LOGGER.info("IndicatorEvent.Type {} not handled!",
                    event.getAssociatedType());
        }
    }

}

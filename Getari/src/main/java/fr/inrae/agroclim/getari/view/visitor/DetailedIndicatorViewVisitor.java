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
package fr.inrae.agroclim.getari.view.visitor;

import static fr.inrae.agroclim.getari.util.ComponentUtil.bindSpinnerToDouble;
import static fr.inrae.agroclim.getari.util.ComponentUtil.newLabel;
import static fr.inrae.agroclim.getari.util.GetariConstants.INDICATOR;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.inrae.agroclim.getari.component.BorderedTitledPane;
import fr.inrae.agroclim.getari.component.EvaluationTextField;
import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.memento.History;
import fr.inrae.agroclim.getari.memento.HistorySingleton;
import fr.inrae.agroclim.getari.memento.Memento;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationType;
import fr.inrae.agroclim.indicators.model.TimeScale;
import fr.inrae.agroclim.indicators.model.data.Variable;
import fr.inrae.agroclim.indicators.model.function.aggregation.AggregationFunction;
import fr.inrae.agroclim.indicators.model.function.normalization.NormalizationFunction;
import fr.inrae.agroclim.indicators.model.indicator.Average;
import fr.inrae.agroclim.indicators.model.indicator.AverageOfDiff;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.DayOfYear;
import fr.inrae.agroclim.indicators.model.indicator.Detailable;
import fr.inrae.agroclim.indicators.model.indicator.DiffOfSum;
import fr.inrae.agroclim.indicators.model.indicator.Formula;
import fr.inrae.agroclim.indicators.model.indicator.Frequency;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.indicator.MaxWaveLength;
import fr.inrae.agroclim.indicators.model.indicator.NumberOfDays;
import fr.inrae.agroclim.indicators.model.indicator.NumberOfWaves;
import fr.inrae.agroclim.indicators.model.indicator.PhaseLength;
import fr.inrae.agroclim.indicators.model.indicator.PotentialSowingDaysFrequency;
import fr.inrae.agroclim.indicators.model.indicator.Quotient;
import fr.inrae.agroclim.indicators.model.indicator.Sum;
import fr.inrae.agroclim.indicators.model.indicator.Tamm;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Visiteur d'un indicateur. Génère le formulaire d'édition de l'indicateur
 * concerné (Evaluation ou indicateur).
 *
 * @see CompositeIndicator
 *
 *      Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class DetailedIndicatorViewVisitor extends DetailedViewVisitor {
    /**
     * Width of second column of evaluation details (for buttons).
     */
    private static final int COL_EVALUTION_BTN_WIDTH = 100;

    /**
     * Width of scroll bar in ScrollPane.
     */
    private static final int SCROLLBAR_WIDTH = 5;

    /**
     * The pane containing the titledPane.
     */
    private final ScrollPane scrollPane;
    /**
     * The pane with title containing the view.
     */
    @Getter(AccessLevel.PROTECTED)
    private final BorderedTitledPane titledPane;
    /**
     * Parent view.
     */
    private final GraphView view;
    /**
     * Flags if data files were changed.
     */
    private Boolean hasPhenoChanged = false;
    /**
     * Flags if data files were changed.
     */
    private Boolean hasClimateChanged = false;

    /**
     * History.
     */
    @Getter
    private History history = HistorySingleton.INSTANCE.getHistory();

    /**
     * Constructor.
     *
     * @param graphview parent view
     */
    public DetailedIndicatorViewVisitor(final GraphView graphview) {
        super();
        this.view = graphview;
        getGridPane().setPadding(new Insets(5, 5, 5, 5));
        titledPane = new BorderedTitledPane(getGridPane(), "border-detailed-titled-content");
        titledPane.getStyleClass().add("bordered-titled-no-border");
        titledPane.setPadding(new Insets(0, SCROLLBAR_WIDTH, 0, 0));
        scrollPane = new ScrollPane();
        scrollPane.setContent(titledPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefWidthProperty().bind(view.getRightPane().widthProperty());
        titledPane.prefWidthProperty().bind(scrollPane.widthProperty());
        getGridPane().prefWidthProperty().bind(titledPane.widthProperty());
    }

    /**
     * Add a widget to pick color.
     *
     * @param indicator phenological phase
     */
    private void addColorPicker(final CompositeIndicator indicator) {
        final ColorPicker colorPicker = new ColorPicker(Color.web(indicator.getColor()));

        colorPicker.setOnAction((final ActionEvent event) -> {
            GetariApp.logAppend(getClass(), null, "color", colorPicker.getValue().toString());
            indicator.setColor(ComponentUtil.toRGBCode(colorPicker.getValue()));
            setColor(indicator.getIndicators(), indicator.getColor());
            indicator.fireValueUpdated();
            GetariApp.get().getCurrentEval().setTranscient(true);
        });

        final Label colorLabel = newLabel("detail.color.label");

        getGridPane().add(colorLabel, 0, getCpt().nextRow());
        getGridPane().add(colorPicker, 1, getCpt().getRow());
    }

    /**
     * Add Toggle: ecophysiological processes <-> cultural practices.
     *
     * @param indicator phase
     */
    private void addEcophysiologocalProcesses(final Indicator indicator) {
        final ToggleGroup group = new ToggleGroup();

        final boolean hasProcesses = !GetariApp.get().getCurrentEval().getSettings().getKnowledge()
                .getEcophysiologicalProcesses().isEmpty();

        final RadioButton usePractices = new RadioButton(Messages.getString("detail.phase.practices.action"));
        usePractices.setToggleGroup(group);

        final RadioButton useProcesses = new RadioButton(Messages.getString("detail.phase.processes.action"));
        useProcesses.setToggleGroup(group);

        if (indicator.getIndicatorCategory() != null) {
            if (indicator.getIndicatorCategory().equals(IndicatorCategory.CULTURAL_PRATICES)) {
                usePractices.setSelected(true);
            } else {
                useProcesses.setSelected(true);
            }
        } else {
            usePractices.setSelected(true);
        }

        if (!hasProcesses) {
            usePractices.setDisable(true);
            useProcesses.setDisable(true);
        }

        // Toggle phase : ecophysiological processes <-> cultural practices
        final IndicatorCategory procss = IndicatorCategory.ECOPHYSIOLOGICAL_PROCESSES;
        group.selectedToggleProperty().addListener(
                (final ObservableValue<? extends Toggle> ov, final Toggle oldToggle, final Toggle newToggle) -> {
                    if (group.getSelectedToggle() != null) {
                        if (group.getSelectedToggle().equals(useProcesses)) {
                            indicator.setIndicatorCategory(procss);
                        } else {
                            indicator.setIndicatorCategory(IndicatorCategory.CULTURAL_PRATICES);
                        }
                        GetariApp.get().getCurrentEval().setTranscient(true);
                    }
                });

        usePractices.setAlignment(Pos.CENTER);
        getGridPane().add(usePractices, 0, getCpt().nextRow(), 2, 1);
        getGridPane().add(useProcesses, 0, getCpt().nextRow(), 2, 1);
    }

    /**
     * Add a remove button for the indicator into the grid pane.
     *
     * @param indicator indicator
     */
    private void addRemoveButton(final Indicator indicator) {
        final Button remove = new Button(Messages.getString("action.delete"));
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(Messages.getString("tooltip.delete.indicator"));
        remove.setTooltip(tooltip);

        remove.setOnAction((final ActionEvent event) -> {
            final Node node = (Node) event.getSource();
            final Indicator indicator1 = (Indicator) node.getProperties().get(INDICATOR);
            try {
                final CompositeIndicator composite = (CompositeIndicator) indicator1.getParent();
                composite.remove(indicator1);
                GetariApp.get().getCurrentEval().remove(indicator1);
            } catch (final Exception e) {
                AlertUtils.showException(e.getMessage(), e);
            }
        });

        remove.setAlignment(Pos.CENTER);
        remove.getProperties().put(INDICATOR, indicator);
        final HBox box = new HBox(remove);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        box.prefWidthProperty().bind(getGridPane().widthProperty());
        getGridPane().add(box, 0, getCpt().nextRow(), 2, 1);
    }

    /**
     * Set automatic with of first column in a grid pane.
     *
     * @param gridPane the grid of the column to adapt
     */
    private void bindFirstColumnWidth(final GridPane gridPane) {
        gridPane.getColumnConstraints().get(0).prefWidthProperty()
        .bind(titledPane.prefWidthProperty().subtract(ComponentUtil.PREF_WIDTH));
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param a indicator
     */
    private void build(final Average a) {
        titledPane.setTitle(a.getName() + " (" + a.getId() + ")");
        buildNormalizationFunction(a);
        addRemoveButton(a);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param a indicator
     */
    private void build(final AverageOfDiff a) {
        titledPane.setTitle(a.getName() + " (" + a.getId() + ")");
        final Label var1 = newLabel("detail.average.var1.label");
        final ComboBox<Variable> comboVar1 = buildComboBox(a, Messages.getString("detail.average.var1.attribute"));
        final ComboBox<Variable> comboVar2 = buildComboBox(a, Messages.getString("detail.average.var2.attribute"));
        final Label var2 = newLabel("detail.average.var2.label");
        getGridPane().add(var1, 0, getCpt().nextRow());
        getGridPane().add(comboVar1, 1, getCpt().getRow());
        getGridPane().add(var2, 0, getCpt().nextRow());
        getGridPane().add(comboVar2, 1, getCpt().getRow());
        addSeparator();
        buildNormalizationFunction(a);
        addRemoveButton(a);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param compositeIndicator indicator
     */
    private void build(final CompositeIndicator compositeIndicator) {
        if (compositeIndicator == null) {
            LOGGER.info("Strange, visited indicator is null!");
            return;
        }
        if (compositeIndicator instanceof Evaluation) {
            build((Evaluation) compositeIndicator);
            return;
        }
        if (compositeIndicator.isPhase()) {
            CompositeIndicator startStage;
            startStage = (CompositeIndicator) compositeIndicator.getFirstIndicator();
            titledPane.setTitle(Messages.getString("detail.phase.stages", startStage.getName(),
                    compositeIndicator.getName(), compositeIndicator.getId()));
        } else {
            titledPane.setTitle(compositeIndicator.getName() + " (" + compositeIndicator.getId() + ")");
        }

        buildFunctions(compositeIndicator);
        if (compositeIndicator.isPhase()) {
            final Separator mySeparator1 = new Separator(Orientation.HORIZONTAL);
            mySeparator1.setValignment(VPos.TOP);
            GridPane.setConstraints(mySeparator1, 0, getCpt().nextRow());
            GridPane.setColumnSpan(mySeparator1, 2);
            getGridPane().getChildren().add(mySeparator1);
            getCpt().nextRow();
            addEcophysiologocalProcesses(compositeIndicator);
            getCpt().nextRow();

            final Separator mySeparator2 = new Separator(Orientation.HORIZONTAL);
            mySeparator2.setValignment(VPos.TOP);
            GridPane.setConstraints(mySeparator2, 0, getCpt().nextRow());
            GridPane.setColumnSpan(mySeparator2, 2);
            getGridPane().getChildren().add(mySeparator2);
            getCpt().nextRow();
            addColorPicker(compositeIndicator);
            getCpt().nextRow();
        }
        addRemoveButton(compositeIndicator);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param s indicator
     */
    private void build(final DayOfYear s) {
        titledPane.setTitle(s.getName() + " (" + s.getId() + ")");
        buildNormalizationFunction(s);
        addRemoveButton(s);
    }

    /**
     * Build form for the indicator.
     *
     * @param indicator indicator to modify and display
     */
    public final void build(final Detailable indicator) {
        if (indicator == null) {
            LOGGER.info("Strange, visited indicator is null!");
        } else if (indicator instanceof Average) {
            build((Average) indicator);
        } else if (indicator instanceof AverageOfDiff) {
            build((AverageOfDiff) indicator);
        } else if (indicator instanceof DayOfYear) {
            build((DayOfYear) indicator);
        } else if (indicator instanceof DiffOfSum) {
            build((DiffOfSum) indicator);
        } else if (indicator instanceof Formula) {
            build((Formula) indicator);
        } else if (indicator instanceof Frequency) {
            build((Frequency) indicator);
        } else if (indicator instanceof MaxWaveLength) {
            build((MaxWaveLength) indicator);
        } else if (indicator instanceof NumberOfDays) {
            build((NumberOfDays) indicator);
        } else if (indicator instanceof NumberOfWaves) {
            build((NumberOfWaves) indicator);
        } else if (indicator instanceof PhaseLength) {
            build((PhaseLength) indicator);
        } else if (indicator instanceof PotentialSowingDaysFrequency) {
            build((PotentialSowingDaysFrequency) indicator);
        } else if (indicator instanceof Quotient) {
            build((Quotient) indicator);
        } else if (indicator instanceof Sum) {
            build((Sum) indicator);
        } else if (indicator instanceof Tamm) {
            build((Tamm) indicator);
        } else if (indicator instanceof Evaluation) {
            build((Evaluation) indicator);
        } else if (indicator instanceof CompositeIndicator) {
            build((CompositeIndicator) indicator);
        } else {
            LOGGER.error("Not handled: " + indicator.getClass());
        }
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param s indicator
     */
    private void build(final DiffOfSum s) {
        titledPane.setTitle(s.getName() + " (" + s.getId() + ")");
        // Variable 1
        DetailedCriteriaViewVisitor criteriaVisitor1;
        criteriaVisitor1 = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        s.getSumVariable1().getCriteria().accept(criteriaVisitor1);
        bindFirstColumnWidth(criteriaVisitor1.getGridPane());
        getCpt().nextRow();

        // Variable 2
        DetailedCriteriaViewVisitor criteriaVisitor2;
        criteriaVisitor2 = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        s.getSumVariable2().getCriteria().accept(criteriaVisitor1);
        bindFirstColumnWidth(criteriaVisitor2.getGridPane());
        getCpt().nextRow();

        buildNormalizationFunction(s);
        addRemoveButton(s);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param e evaluation
     */
    private void build(final Evaluation e) {

        LOGGER.trace("visit(Evaluation e)");
        titledPane.setTitle(null);
        getGridPane().getColumnConstraints().get(0).minWidthProperty()
        .bind(titledPane.prefWidthProperty().subtract(COL_EVALUTION_BTN_WIDTH));
        final Button updateButton = new Button(Messages.getString("action.update"));
        updateButton.setDefaultButton(true);
        // Evaluation name
        final Label evaluationLabel = newLabel("evaluation.name.label");
        final TextField evaluationField = ComponentUtil.newTextField();
        evaluationField.setText(e.getName());
        evaluationField.setEditable(true);
        evaluationField.setDisable(false);
        ComponentUtil.bindTextToString(evaluationField, e.getSettings(), "name");
        evaluationField.textProperty().addListener((final ObservableValue<? extends String> observable,
                final String oldValue, final String newValue) -> updateButton.setDisable(false));
        // Evaluation timescale
        final Label evaluationTimescaleLabel = new Label(
                Messages.getString("punctuation.colon", Messages.getString("evaluation.timescale.label")) + " "
                        + e.getTimescale().getName());
        // Evaluation type
        final Label evaluationTypeLabel = new Label(
                Messages.getString("punctuation.colon", Messages.getString("evaluation.type.label")) + " "
                        + e.getType().getName());
        // Phenology file
        final Label phaseLabel = newLabel("evaluation.pheno.label");
        final EvaluationTextField phenoField = ComponentUtil.newTextField();
        final Tooltip phaseTooltip = new Tooltip();
        phaseTooltip.setText(e.getSettings().getPhenologyLoader().getFile().getAbsolutePath());
        phenoField.setText(e.getSettings().getPhenologyLoader().getFile().getAbsolutePath());
        phenoField.setTooltip(phaseTooltip);
        phenoField.setEditable(false);
        phenoField.setDisable(false);
        final Button phenoBrowse = new Button(Messages.getString("action.browse"));
        ComponentUtil.chooseDataFileButton(phenoBrowse, phenoField, view.getStage(), FileType.PHENOLOGY, e);
        phenoField.textProperty().addListener(
                (final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
                    hasPhenoChanged = true;
                    updateButton.setDisable(false);
                });

        // Climatic file
        final Label climaticLabel = newLabel("evaluation.climatic.label");
        final EvaluationTextField climaticField = ComponentUtil.newTextField();
        final Tooltip climaticTooltip = new Tooltip();
        if (e.getSettings().getClimateLoader().getFile().getFile() != null) {
            final String path = e.getSettings().getClimateLoader().getFile().getFile().getAbsolutePath();
            climaticTooltip.setText(path);
            climaticField.setText(path);
        } else {
            climaticTooltip.setText("");
            climaticField.setText("");
        }
        climaticField.setTooltip(climaticTooltip);
        climaticField.setEditable(false);
        climaticField.setDisable(false);
        final Button climaticBrowse = new Button(Messages.getString("action.browse"));
        ComponentUtil.chooseDataFileButton(climaticBrowse, climaticField, view.getStage(), FileType.CLIMATE, e);

        climaticField.textProperty().addListener(
                (final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
                    hasClimateChanged = true;
                    updateButton.setDisable(false);
                });

        updateButton.setDisable(true);

        updateButton.setOnAction((final ActionEvent t) -> {

            view.getStage().getScene().setCursor(Cursor.WAIT);
            e.setName(Locale.getDefault().getLanguage(), evaluationField.getText());
            GetariApp.getMainView().getCurrentTab().setText("* " + e.getName());

            if (hasPhenoChanged || hasClimateChanged) {
                e.initializeResources();
                if (e.getResourceManager().getPhenologicalResource().getData() == null) {
                    final String msg = Messages.getString("evaluation.no.stages");
                    AlertUtils.showError(msg);
                    LOGGER.error(msg);
                    return;
                }
                LOGGER.trace("{} climatic data.", e.getResourceManager().getClimaticResource().getData().size());
                LOGGER.trace("Years of climatic data: {}", e.getResourceManager().getClimaticResource().getYears());
            }
            final Task<String> waitingTask = new Task<String>() {
                @Override
                public String call() throws Exception {
                    Thread.sleep(1_000);
                    return "New Message";
                }
            };
            waitingTask.setOnSucceeded((final WorkerStateEvent event) -> {
                e.setTranscient(true);
                updateButton.setDisable(true);
                view.getStage().getScene().setCursor(Cursor.DEFAULT);
            });
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(waitingTask);

            final Evaluation cloned = e.clone();
            int newID = history.getMemento().getId() + 1;
            Memento m = new Memento(cloned, Messages.getString("history.name.change", cloned.getName()), newID);
            history.addMemento(m);
            for (Memento list : history.getUndoHistory()) {
                LOGGER.trace("Evaluation {} , Memento ID {}", list.getEvaluation(), list.getId());


            }
        });

        getGridPane().add(evaluationLabel, 0, getCpt().nextRow());
        getGridPane().add(evaluationField, 0, getCpt().nextRow());
        GridPane.setColumnSpan(evaluationLabel, 2);
        GridPane.setColumnSpan(evaluationField, 2);

        getGridPane().add(phaseLabel, 0, getCpt().nextRow());
        GridPane.setColumnSpan(phaseLabel, 2);
        getGridPane().add(phenoField, 0, getCpt().nextRow());
        getGridPane().add(phenoBrowse, 1, getCpt().getRow());

        getGridPane().add(climaticLabel, 0, getCpt().nextRow());
        GridPane.setColumnSpan(climaticLabel, 2);
        getGridPane().add(climaticField, 0, getCpt().nextRow());
        getGridPane().add(climaticBrowse, 1, getCpt().getRow());

        getGridPane().add(evaluationTimescaleLabel, 0, getCpt().nextRow());
        getGridPane().add(evaluationTypeLabel, 0, getCpt().nextRow());

        /*
         * Generate aggregation function field if the evaluation contains more than one
         * phase
         */
        buildFunctions(e);

        final HBox box = new HBox(updateButton);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        getGridPane().add(box, 0, getCpt().nextRow());
        GridPane.setColumnSpan(box, 2);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param f indicator
     */
    private void build(final Formula f) {
        titledPane.setTitle(f.getName() + " (" + f.getId() + ")");
        final Label expressionLabel = newLabel("detail.formula.expression.label");
        final Label expression = new Label(f.getExpression());
        expression.setWrapText(true);
        final Label variablesLabel = newLabel("detail.formula.variables.label");
        final Label variables = new Label(f.getVariables().toString());
        getGridPane().add(expressionLabel, 0, getCpt().nextRow());
        getGridPane().add(expression, 1, getCpt().getRow());
        getGridPane().add(variablesLabel, 0, getCpt().nextRow());
        getGridPane().add(variables, 1, getCpt().getRow());
        addSeparator();
        buildNormalizationFunction(f);
        addRemoveButton(f);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param f indicator
     */
    private void build(final Frequency f) {
        titledPane.setTitle(f.getName() + " (" + f.getId() + ")");

        buildNumberOfDays(f.getNumberOfDays());
        buildNormalizationFunction(f);
        addRemoveButton(f);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param n indicator
     */
    private void build(final MaxWaveLength n) {
        titledPane.setTitle(n.getName() + " (" + n.getId() + ")");

        // Criteria
        DetailedCriteriaViewVisitor criteriaVisitor;
        criteriaVisitor = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        n.getCriteria().accept(criteriaVisitor);
        bindFirstColumnWidth(criteriaVisitor.getGridPane());
        getCpt().nextRow();

        // Indicator properties
        final TimeScale timeScale = GetariApp.get().getCurrentEval().getTimescale();
        final Label nbDaysLabel = newLabel("detail.maxwavelength.threshold.label." + timeScale.name());
        final Spinner<Integer> nbDaysField = ComponentUtil.newIntegerSpinner(0, Integer.MAX_VALUE, n.getThreshold(), 1);
        ComponentUtil.bindSpinnerToInteger(nbDaysField, n, "threshold");
        getGridPane().add(nbDaysLabel, 0, getCpt().nextRow());
        getGridPane().add(nbDaysField, 1, getCpt().getRow());
        addSeparator();

        buildNormalizationFunction(n);
        addRemoveButton(n);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param n indicator
     */
    private void build(final NumberOfDays n) {
        titledPane.setTitle(n.getName() + " (" + n.getId() + ")");
        buildNumberOfDays(n);
        addRemoveButton(n);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param n indicator
     */
    private void build(final NumberOfWaves n) {
        titledPane.setTitle(n.getName() + " (" + n.getId() + ")");

        // Criteria
        DetailedCriteriaViewVisitor criteriaVisitor;
        criteriaVisitor = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        n.getCriteria().accept(criteriaVisitor);
        bindFirstColumnWidth(criteriaVisitor.getGridPane());
        getCpt().nextRow();

        // Indicator properties
        final Label nbDaysLabel = newLabel("detail.numberofwaves.nbdays.label");
        final Spinner<Integer> nbDaysField = ComponentUtil.newIntegerSpinner(0, Integer.MAX_VALUE, n.getNbDays(), 1);
        ComponentUtil.bindSpinnerToInteger(nbDaysField, n, "nbDays");
        getGridPane().add(nbDaysLabel, 0, getCpt().nextRow());
        getGridPane().add(nbDaysField, 1, getCpt().getRow());
        addSeparator();

        buildNormalizationFunction(n);
        addRemoveButton(n);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param p indicator
     */
    private void build(final PhaseLength p) {
        titledPane.setTitle(p.getName() + " (" + p.getId() + ")");
        buildNormalizationFunction(p);
        addRemoveButton(p);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param p indicator
     */
    private void build(final PotentialSowingDaysFrequency p) {
        titledPane.setTitle(p.getName() + " (" + p.getId() + ")");
        final Label minSoilWaterContent = newLabel("detail.potential.min.label");
        minSoilWaterContent.setTooltip(new Tooltip(minSoilWaterContent.getText()));
        final Spinner<Double> fieldminSoilWaterContent = ComponentUtil.newDoubleSpinner(0., 100.,
                p.getSoilWaterContentThreshold(), 0.1);
        bindSpinnerToDouble(fieldminSoilWaterContent, p, Messages.getString("detail.potential.min.attribute"));

        final Label nbDays = newLabel("detail.potential.days.label");
        final Spinner<Integer> fieldNbDays = ComponentUtil.newIntegerSpinner(0, Integer.MAX_VALUE, p.getNbDays(), 1);
        ComponentUtil.bindSpinnerToInteger(fieldNbDays, p, Messages.getString("detail.potential.days.attribute"));

        final Label raindThreshold = newLabel("detail.potential.rain.label");
        final Spinner<Double> fieldRaindThreshold = ComponentUtil.newDoubleSpinner(0., 100., p.getRainThreshold(), 0.1);
        bindSpinnerToDouble(fieldRaindThreshold, p, Messages.getString("detail.potential.rain.attribute"));

        final Label tminThreshold = newLabel("detail.potential.tmin.label");
        final Spinner<Double> fieldTminThreshold = ComponentUtil.newDoubleSpinner(true);
        bindSpinnerToDouble(fieldTminThreshold, p, Messages.getString("detail.potential.tmin.attribute"));

        final Label tmoyThreshold = newLabel("detail.potential.tmoy.label");
        final Spinner<Double> fieldTmoyThreshold = ComponentUtil.newDoubleSpinner(true);
        bindSpinnerToDouble(fieldTmoyThreshold, p, Messages.getString("detail.potential.tmoy.attribute"));

        getGridPane().add(minSoilWaterContent, 0, getCpt().getRow());
        getGridPane().add(fieldminSoilWaterContent, 1, getCpt().getRow());

        getGridPane().add(nbDays, 0, getCpt().nextRow());
        getGridPane().add(fieldNbDays, 1, getCpt().getRow());

        getGridPane().add(raindThreshold, 0, getCpt().nextRow());
        getGridPane().add(fieldRaindThreshold, 1, getCpt().getRow());

        getGridPane().add(tminThreshold, 0, getCpt().nextRow());
        getGridPane().add(fieldTminThreshold, 1, getCpt().getRow());

        getGridPane().add(tmoyThreshold, 0, getCpt().nextRow());
        getGridPane().add(fieldTmoyThreshold, 1, getCpt().getRow());
        addSeparator();
        buildNormalizationFunction(p);
        addRemoveButton(p);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param s indicator
     */
    private void build(final Quotient s) {
        titledPane.setTitle(s.getName() + " (" + s.getId() + ")");
        buildNormalizationFunction(s);
        addRemoveButton(s);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param s indicator
     */
    private void build(final Sum s) {
        titledPane.setTitle(s.getName() + " (" + s.getId() + ")");

        DetailedCriteriaViewVisitor criteriaVisitor;
        criteriaVisitor = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        s.getCriteria().accept(criteriaVisitor);
        bindFirstColumnWidth(criteriaVisitor.getGridPane());
        buildNormalizationFunction(s);
        addRemoveButton(s);
    }

    /**
     * DetailableVisitor.visit speciation.
     *
     * @param m indicator
     */
    private void build(final Tamm m) {
        titledPane.setTitle(m.getName() + " (" + m.getId() + ")");
        getGridPane().add(newLabel("detail.monilia.tamm.parameters"), 0, getCpt().getRow(), 2, 1);
        Arrays.asList("e", "gamma1", "gamma2", "imax", "m", "ro1", "ro2", "tMax", "tMin").forEach(fieldName -> {
            final Label label = new Label(fieldName);
            final Spinner<Double> spinner = ComponentUtil.newDoubleSpinner(0., 100., 0., 0.1);
            bindSpinnerToDouble(spinner, m, fieldName);
            getGridPane().add(label, 0, getCpt().nextRow());
            getGridPane().add(spinner, 1, getCpt().getRow());
        });
        addSeparator();

        buildNormalizationFunction(m);
        addRemoveButton(m);
    }

    /**
     * Build ComboBox with available variables.
     *
     * @param a        indicator
     * @param property property name (variable1/variable2)
     * @return combobox
     */
    @SuppressWarnings("unchecked")
    private ComboBox<Variable> buildComboBox(final AverageOfDiff a, final String property) {
        JavaBeanObjectPropertyBuilder<Variable> builder;
        builder = JavaBeanObjectPropertyBuilder.create();
        builder.bean(a);
        builder.name(property);
        JavaBeanObjectProperty<Variable> variableProperty;
        try {
            variableProperty = builder.build();
        } catch (final NoSuchMethodException ex) {
            throw new IllegalArgumentException("This should never occur!", ex);
        }
        final Set<Variable> variables = GetariApp.get().getCurrentEval().getVariables();
        final Variable selected = variableProperty.getValue();
        final ComboBox<Variable> combo = new ComboBox<>();
        combo.getItems().setAll(variables);
        combo.setValue(selected);
        combo.getSelectionModel().selectedItemProperty()
        .addListener((final ObservableValue<? extends Variable> observable, final Variable oldValue,
                final Variable newValue) -> {
                    variableProperty.set(newValue);
                    GetariApp.get().getCurrentEval().setTranscient(true);
                });
        combo.setPrefWidth(ComponentUtil.PREF_WIDTH);
        return combo;
    }

    /**
     * @param ind indicator to display in detailed function view
     */
    private void buildFunctions(final CompositeIndicator ind) {
        LOGGER.traceEntry(ind.getName());
        if (ind.getType() == EvaluationType.WITHOUT_AGGREGATION) {
            return;
        }
        DetailedFunctionViewVisitor functionVisitor;
        functionVisitor = new DetailedFunctionViewVisitor(ind, getGridPane(), getCpt(), view);

        if (ind.getCategory() == null) {
            /* The composite Indicator is the evaluation */
            if (ind.getIndicators().size() > 1) {
                final AggregationFunction function = ind.getAggregationFunction();
                if (function == null) {
                    LOGGER.error(
                            "Strange, indicator {} has {} indicators " + "but does not have any aggregation function!",
                            ind.getId(), ind.getIndicators().size());
                    return;
                }
                functionVisitor.buildForm(function);
            }
            return;
        }

        if (ind.getIndicatorCategory().equals(IndicatorCategory.INDICATORS)) {
            final NormalizationFunction norm = ind.getNormalizationFunction();
            functionVisitor.buildForm(norm);
        }

        if (ind.isPhase() && ind.getIndicators().size() > 2 || !ind.isPhase() && ind.getIndicators().size() > 1) {
            /*
             * Ajout de la fonction d'aggrégation au delà de 2 fils pour les indicateurs de
             * type "Phase" car le premier indicateur fils correspond au stade phénologique
             * final de la phase
             */
            AggregationFunction function;
            function = ind.getAggregationFunction();
            if (function == null) {
                LOGGER.fatal("Strange, indicator {} has {} indicators " + "but does not have any aggregation function!",
                        ind.getId(), ind.getIndicators().size());
                return;
            }
            functionVisitor.buildForm(function);
        }

        ind.clearFunctionListener();
        ind.addFunctionListener(view);
    }

    /**
     * Build partial view for the normalization function.
     *
     * @param i indicator which has normalization function to display in partial
     *          view
     */
    private void buildNormalizationFunction(final Indicator i) {
        LOGGER.traceEntry(i.getName());
        if (i.getType() == EvaluationType.WITHOUT_AGGREGATION) {
            return;
        }
        DetailedFunctionViewVisitor functionVisitor;
        functionVisitor = new DetailedFunctionViewVisitor(i, getGridPane(), getCpt(), view);
        bindFirstColumnWidth(functionVisitor.getInnerGridPane());
        bindFirstColumnWidth(functionVisitor.getGridPane());
        final NormalizationFunction norm = i.getNormalizationFunction();
        if (norm != null) {
            functionVisitor.buildForm(norm);
        } else {
            LOGGER.info("missing normalization function for " + i + ".");
        }
        addSeparator();
    }

    /**
     * Build partial view for the NumberOfDays indicators.
     *
     * @param n indicator to display in partial view
     */
    private void buildNumberOfDays(final NumberOfDays n) {
        LOGGER.traceEntry(n.getName());
        DetailedCriteriaViewVisitor criteriaVisitor;
        criteriaVisitor = new DetailedCriteriaViewVisitor(getGridPane(), getCpt());
        n.getCriteria().accept(criteriaVisitor);
        bindFirstColumnWidth(criteriaVisitor.getGridPane());
        getCpt().nextRow();
        if (n.getType() == EvaluationType.WITHOUT_AGGREGATION) {
            return;
        }
        final NormalizationFunction norm = n.getNormalizationFunction();
        if (norm != null) {
            DetailedFunctionViewVisitor functionVisitor;
            functionVisitor = new DetailedFunctionViewVisitor(n);
            functionVisitor.buildForm(norm);
            getGridPane().add(functionVisitor.getGridPane(), 0, getCpt().nextRow(), 2, 1);
            bindFirstColumnWidth(functionVisitor.getGridPane());
            bindFirstColumnWidth(functionVisitor.getInnerGridPane());
            addSeparator();
        }
    }

    /**
     * @return the node containing the detailed view.
     */
    public final Node getNode() {
        return scrollPane;
    }

    /**
     * Update color value for the indicators and all children.
     *
     * @param indicators indicator to update
     * @param color      hexadecimal color code
     */
    private void setColor(final List<Indicator> indicators, final String color) {
        if (indicators == null) {
            return;
        }
        indicators.forEach(indicator -> {
            indicator.setColor(color);
            if (indicator instanceof CompositeIndicator) {
                setColor(((CompositeIndicator) indicator).getIndicators(), color);
            }
        });
    }

}

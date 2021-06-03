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

import static fr.inrae.agroclim.getari.util.ComponentUtil.newLabel;
import static fr.inrae.agroclim.getari.util.GetariConstants.INIT_VALUE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.MultiLinearFormBuilder;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.NameableListCell;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.getari.view.FunctionChartView;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.function.aggregation.AggregationFunction;
import fr.inrae.agroclim.indicators.model.function.aggregation.JEXLFunction;
import fr.inrae.agroclim.indicators.model.function.normalization.Exponential;
import fr.inrae.agroclim.indicators.model.function.normalization.Linear;
import fr.inrae.agroclim.indicators.model.function.normalization.MultiLinear;
import fr.inrae.agroclim.indicators.model.function.normalization.Normal;
import fr.inrae.agroclim.indicators.model.function.normalization.NormalizationFunction;
import fr.inrae.agroclim.indicators.model.function.normalization.Sigmoid;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * Visiteur d'une fonction (aggregation ou normalisation). Génère le formulaire
 * d'édition de la fonction concernée.
 *
 * @see CompositeIndicator
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class DetailedFunctionViewVisitor extends DetailedViewVisitor {

    /**
     * All functions listed in the combobox.
     */
    private static final List<NormalizationFunction> ALL_FUNCTIONS = Arrays.asList(new Exponential(), new Linear(),
            new MultiLinear(), new Normal(), new Sigmoid());

    /**
     * Path for Javadoc of Math package.
     */
    static final String MATH_JAVADOC_BASE_PATH
    = "https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Math.html";

    /**
     * @param functions functions for combobox
     * @param selected selected function
     * @return combobox with normalization functions
     */
    private static ComboBox<NormalizationFunction> buildComboBox(
            final List<NormalizationFunction> functions,
            final NormalizationFunction selected) {
        final ComboBox<NormalizationFunction> comboFunctions = new ComboBox<>();
        GridPane.setHalignment(comboFunctions, HPos.LEFT);
        NameableListCell.setCellFactory(comboFunctions);
        comboFunctions.getItems().addAll(functions);
        comboFunctions.setValue(selected);
        comboFunctions.setPrefWidth(ComponentUtil.PREF_WIDTH);
        comboFunctions.setMaxHeight(10);
        return comboFunctions;
    }
    /**
     * @param label text for label
     * @return label with wrapped text
     */
    private static Label createFunctionLabel(final String label) {
        final Label name = newLabel(label);
        name.setWrapText(true);
        return name;
    }

    /**
     * Get URL for the javadoc of Math mathod.
     *
     * @param jexlFuncName JEXL representation in combobox for the Math method
     * @return Javadoc URL
     */
    public static String jexlFunctionToApiDocUrl(final String jexlFuncName) {
        final String[] parts = jexlFuncName.split(":");
        if (parts == null || parts.length != 2 || !"math".equals(parts[0])) {
            throw new IllegalArgumentException(
                    "JEXL function name not handled: " + jexlFuncName);
        }
        final String[] parts2 = parts[1].split("\\(");
        if (parts2 == null || parts2.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong function name: " + jexlFuncName);
        }
        final String[] parts3 = parts2[1].split(",");
        String anchor = "#" + parts2[0] + "-";
        if (parts3 != null) {
            anchor += StringUtils.repeat("double-", parts3.length);
        }
        return MATH_JAVADOC_BASE_PATH + anchor;
    }

    /**
     * Partial view to display normalization function chart.
     */
    private final FunctionChartView chartView = new FunctionChartView();

    /**
     * Root node of logView.
     */
    private final Parent chartViewParent;

    /**
     * Root container.
     */
    @Getter
    private final GridPane innerGridPane;

    /**
     * Indicator to detail.
     */
    @Getter
    private final Indicator indicator;

    /**
     * Display of formula in embedded HTML navigator.
     */
    private WebView formula;

    /**
     * Current locale.
     */
    private final Locale locale = Locale.getDefault();

    /**
     * Graph view.
     */
    private GraphView view;

    /**
     * Constructor.
     *
     * @param i indicator to detail
     */
    public DetailedFunctionViewVisitor(final Indicator i) {
        super();
        try {
            chartViewParent = chartView.build();
        } catch (final IOException e) {
            throw new RuntimeException("Building chartView should never fails",
                    e);
        }
        indicator = i;
        innerGridPane = new GridPane();
        ComponentUtil.initGridPane(innerGridPane);
    }

    /**
     * Constructor.
     *
     * @param i indicator to detail
     * @param p GridPane to display details.
     * @param c Counter for GridPane building.
     * @param v graph view
     */
    public DetailedFunctionViewVisitor(final Indicator i, final GridPane p,
            final Cpt c, final GraphView v) {
        super(p, c);
        try {
            chartViewParent = chartView.build();
        } catch (final IOException e) {
            throw new RuntimeException("Building chartView should never fails",
                    e);
        }
        indicator = i;
        innerGridPane = new GridPane();
        ComponentUtil.initGridPane(innerGridPane);
        this.view = v;
    }

    /**
     * @param norm normalization function to display
     */
    private void buildChart(final NormalizationFunction norm) {
        LOGGER.traceEntry(norm.getName());
        final NumberAxis yAxis = new NumberAxis(0, 1.1, 0.1);
        buildChart(norm, yAxis);
        LOGGER.traceExit();
    }

    /**
     * @param norm normalization function to display
     * @param yAxis limits of Y axis
     */
    private void buildChart(final NormalizationFunction norm,
            final NumberAxis yAxis) {
        LOGGER.traceEntry("{} : y {}:{}:{}",
                norm.getName(),
                yAxis.getLowerBound(),
                yAxis.getUpperBound(),
                yAxis.getTickUnit());
        chartViewParent.setVisible(true);
        chartView.getController().buildChart(norm, yAxis);
        LOGGER.traceExit();
    }

    /**
     * @param a A in A.exp(B/x).
     * @param b V in A.exp(B/x).
     */
    private void buildExpChart(@NonNull final Number a,
            @NonNull final Number b) {
        LOGGER.traceEntry("a={}, b={}", a, b);
        final Exponential exp = new Exponential(a.doubleValue(),
                b.doubleValue());
        final double exp1 = exp.normalize(1);
        final NumberAxis yAxis = new NumberAxis(0, exp1 + 0.1, exp1 / 10);
        buildChart(exp, yAxis);
    }

    /**
     * Build form for aggregation function.
     *
     * @param func aggregation function to modify and display
     */
    public void buildForm(final AggregationFunction func) {
        if (func == null) {
            LOGGER.warn("No aggregation function to display.");
            return;
        }
        if (func instanceof JEXLFunction) {
            buildForm((JEXLFunction) func);
        } else {
            throw new UnsupportedOperationException("Handling "
                    + func.getClass().getCanonicalName() + " not implemented!");
        }
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param func JEXL function to edit and view
     */
    private void buildForm(final JEXLFunction func) {
        final ComboBox<String> comboFunctions = new ComboBox<>();
        comboFunctions.setPrefWidth(200);
        comboFunctions.setPromptText(Messages.getString("action.function.add"));

        final Button helpBtn = new Button("?");
        helpBtn.setOnAction(e
                -> PlatformUtil.openBrowser(
                        jexlFunctionToApiDocUrl(comboFunctions.getValue())));
        helpBtn.visibleProperty()
        .bind(comboFunctions.valueProperty().isNotNull());
        helpBtn.setTooltip(new Tooltip(
                Messages.getString("action.function.api")));

        final TextArea aggregationFormula = new TextArea(INIT_VALUE);
        aggregationFormula.setPrefSize(view.getRightPane().getWidth(), 50);
        aggregationFormula.prefWidthProperty()
        .bind(view.getRightPane().widthProperty());
        aggregationFormula.setText(func.getExpression());

        final Label name = createFunctionLabel("indicator.aggregation.title");
        final Label varTitle = createFunctionLabel("indicator.aggregation.variables");

        getGridPane().add(name, 0, getCpt().nextRow());
        getGridPane().add(comboFunctions, 0, getCpt().nextRow(), 2, 1);
        getGridPane().add(helpBtn, 1, getCpt().getRow());
        getGridPane().add(aggregationFormula, 0, getCpt().nextRow(), 2, 1);
        getGridPane().add(varTitle, 0, getCpt().nextRow());
        if (indicator instanceof CompositeIndicator) {
            final CompositeIndicator composite = (CompositeIndicator) indicator;
            composite.getIndicators().stream()
            .filter(ind -> indicator.getIndicatorCategory() == null
            || !IndicatorCategory.PHENO_PHASES
            .equals(ind.getIndicatorCategory()))
            .forEach(ind -> {
                final Label var = new Label(ind.getId());
                final Tooltip tltp = new Tooltip(ind.getName(locale.getLanguage()));
                var.setTooltip(tltp);
                var.setCursor(Cursor.HAND);
                var.setOnMouseClicked(e -> {
                    String text = ind.getId();
                    if (StringUtils.isNumeric(text.substring(0, 1))) {
                        text = "$" + text;
                    }
                    final int pos = aggregationFormula.getCaretPosition();
                    aggregationFormula.insertText(pos, text);
                });
                getGridPane().add(var, 0, getCpt().nextRow());
            });
        } else {
            LOGGER.warn("Strange, the indicator is not CompositeIndicator: {}",
                    indicator.getId());
        }

        final Method[] methods = Math.class.getDeclaredMethods();

        for (final Method method : methods) {
            final Class<?>[] parameters = method.getParameterTypes();
            String parameter = StringUtils.repeat("?,", parameters.length);
            if (!parameter.isEmpty()) {
                parameter = parameter.substring(0, parameter.length() - 1);
            }
            final String val = "math:" + method.getName() + "(" + parameter + ")";
            if (!comboFunctions.getItems().contains(val)) {
                comboFunctions.getItems().add(val);
            }
        }

        comboFunctions
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
                (final ObservableValue<? extends String> observable,
                        final String oldValue, final String newValue) -> {
                            if (newValue != null) {
                                final int pos = aggregationFormula.getCaretPosition();
                                aggregationFormula.insertText(pos, newValue);
                            }
                        });

        aggregationFormula.textProperty().addListener(
                (final ObservableValue<? extends String> observable,
                        final String oldValue, final String newValue) -> {
                            LOGGER.trace("{} -> {}", oldValue, newValue);
                            if (!INIT_VALUE.equals(oldValue)) {
                                // TODO : test function before change
                                // how to get variables? which values to use?
                                func.setExpression(newValue);
                                final Evaluation eval = GetariApp.get().getCurrentEval();
                                eval.setTranscient(true);
                                eval.fireIndicatorEvent(
                                        IndicatorEvent.Type.CHANGE.event(eval));
                                GetariApp.get().getCurrentEval().validate();
                            }
                        });
    }

    /**
     * Build combobox with label with change handling.
     *
     * @param selectedFunction current function
     */
    public final void buildForm(final NormalizationFunction selectedFunction) {
        LOGGER.traceEntry(selectedFunction.getName());
        final List<NormalizationFunction> functions = new ArrayList<>();
        functions.add(selectedFunction);
        ALL_FUNCTIONS.stream() //
        .filter(f -> !f.getClass().equals(selectedFunction.getClass()))
        .forEach(functions::add);
        final Label name = createFunctionLabel("indicator.normalization.title");
        formula = new WebView();
        showFormula(selectedFunction);
        formula.setPrefHeight(55);
        formula.prefWidthProperty().bind(getGridPane().widthProperty());
        final ComboBox<NormalizationFunction> comboFunction = buildComboBox(functions, selectedFunction);

        comboFunction.valueProperty().addListener(
                (final ObservableValue<? extends NormalizationFunction> ov,
                        final NormalizationFunction oldValue,
                        final NormalizationFunction newValue) -> {
                            if (Objects.equals(oldValue, newValue)) {
                                return;
                            }
                            LOGGER.trace("combo change");
                            getInnerGridPane().getChildren().clear();
                            // set the new function
                            getIndicator().setNormalizationFunction(newValue.clone());
                            GetariApp.get().getCurrentEval().setTranscient(true);
                            //
                            buildFormPart(getIndicator().getNormalizationFunction());
                            // display formula
                            showFormula(getIndicator().getNormalizationFunction());
                        });

        getGridPane().add(name, 0, getCpt().getRow());
        getGridPane().add(comboFunction, 1, getCpt().getRow());
        getGridPane().add(formula, 0, getCpt().nextRow(), 2, 1);
        getGridPane().add(getInnerGridPane(), 0, getCpt().nextRow(), 2, 1);
        buildFormPart(selectedFunction);
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param exp normalization function to edit and view
     */
    private void buildFormPart(final Exponential exp) {
        LOGGER.traceEntry(exp.getName());
        final Label labelA = newLabel("detail.exp.a.label");
        final Label labelB = newLabel("detail.exp.b.label");
        final Spinner<Double> fieldA = ComponentUtil.newDoubleSpinner(true);
        final Spinner<Double> fieldB = ComponentUtil.newDoubleSpinner(true);

        final ChangeListener<Double> lis = (final ObservableValue<? extends Double> o,
                final Double oldValue, final Double newValue) -> {
                    if (oldValue != null && !oldValue.equals(newValue)) {
                        hideChart();
                        final Number a = fieldA.getValue();
                        final Number b = fieldB.getValue();
                        buildExpChart(a, b);
                    }
                };

                fieldA.valueProperty().addListener(lis);
                fieldB.valueProperty().addListener(lis);

                ComponentUtil.bindSpinnerToDouble(fieldA, exp,
                        Messages.getString("detail.exp.a.attribute"));
                ComponentUtil.bindSpinnerToDouble(fieldB, exp,
                        Messages.getString("detail.exp.b.attribute"));
                getInnerGridPane().add(labelA, 0, 0);
                getInnerGridPane().add(fieldA, 1, 0);
                getInnerGridPane().add(labelB, 0, 1);
                getInnerGridPane().add(fieldB, 1, 1);
                getInnerGridPane().add(chartViewParent, 0, 2, 2, 1);
                lis.changed(null, Double.NaN, null);
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param n normalization function to edit and view
     */
    private void buildFormPart(final Linear n) {
        LOGGER.traceEntry(n.getName());
        final Label labelA = newLabel("detail.linear.a.label");
        final Label labelB = newLabel("detail.linear.b.label");
        final Spinner<Double> fieldA = ComponentUtil.newDoubleSpinner(true);
        final Spinner<Double> fieldB = ComponentUtil.newDoubleSpinner(true);

        final ChangeListener<Double> lis = (final ObservableValue<? extends Double> o,
                final Double oldValue, final Double newValue) -> {
                    if (oldValue != null && !oldValue.equals(newValue)) {
                        hideChart();
                        final Number a = fieldA.getValue();
                        final Number b = fieldB.getValue();
                        final Linear norm = new Linear(a.doubleValue(), b.doubleValue());
                        buildChart(norm);
                    }
                };

                fieldA.valueProperty().addListener(lis);
                fieldB.valueProperty().addListener(lis);

                ComponentUtil.bindSpinnerToDouble(fieldA, n,
                        Messages.getString("detail.linear.a.attribute"));
                ComponentUtil.bindSpinnerToDouble(fieldB, n,
                        Messages.getString("detail.linear.b.attribute"));
                getInnerGridPane().add(labelA, 0, 0);
                getInnerGridPane().add(fieldA, 1, 0);
                getInnerGridPane().add(labelB, 0, 1);
                getInnerGridPane().add(fieldB, 1, 1);
                getInnerGridPane().add(chartViewParent, 0, 2, 2, 1);
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param n normalization function to edit and view
     */
    private void buildFormPart(final MultiLinear n) {
        LOGGER.traceEntry(n.getName());
        final MultiLinearFormBuilder builder = new MultiLinearFormBuilder();
        builder.setNormalizationFunction(n);
        builder.setOnUpdate(norm -> {
            this.buildChart(norm);
            this.showFormula(norm);
        });
        builder.setChartViewParent(chartViewParent);
        builder.setGridPane(getInnerGridPane());
        builder.build();
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param n normalization function to edit and view
     */
    private void buildFormPart(final Normal n) {
        LOGGER.traceEntry(n.getName());
        final Label labelA = newLabel("detail.normal.a.label");
        final Label labelB = newLabel("detail.normal.b.label");
        final Label labelC = newLabel("detail.normal.b.label");
        final Spinner<Double> fieldA = ComponentUtil.newDoubleSpinner(true);
        final Spinner<Double> fieldB = ComponentUtil.newDoubleSpinner(true);
        final Spinner<Double> fieldC = ComponentUtil.newDoubleSpinner(true);

        final ChangeListener<Double> lis = (final ObservableValue<? extends Double> o,
                final Double oldValue, final Double newValue) -> {
                    if (oldValue != null && !oldValue.equals(newValue)) {
                        hideChart();
                        final Number a = fieldA.getValue();
                        final Number b = fieldB.getValue();
                        final Number c = fieldC.getValue();
                        final Normal norm = new Normal(
                                a.doubleValue(),
                                b.doubleValue(),
                                c.doubleValue()
                                );
                        buildChart(norm);
                    }
                };

                fieldA.valueProperty().addListener(lis);
                fieldB.valueProperty().addListener(lis);
                fieldC.valueProperty().addListener(lis);

                ComponentUtil.bindSpinnerToDouble(fieldA, n,
                        Messages.getString("detail.normal.a.attribute"));
                ComponentUtil.bindSpinnerToDouble(fieldB, n,
                        Messages.getString("detail.normal.b.attribute"));
                ComponentUtil.bindSpinnerToDouble(fieldC, n,
                        Messages.getString("detail.normal.c.attribute"));
                getInnerGridPane().add(labelA, 0, 0);
                getInnerGridPane().add(fieldA, 1, 0);
                getInnerGridPane().add(labelB, 0, 1);
                getInnerGridPane().add(fieldB, 1, 1);
                getInnerGridPane().add(labelC, 0, 2);
                getInnerGridPane().add(fieldC, 1, 2);
                getInnerGridPane().add(chartViewParent, 0, 3, 2, 1);
                lis.changed(null, Double.NaN, null);
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param norm normalization function to edit and view
     */
    private void buildFormPart(final NormalizationFunction norm) {
        LOGGER.traceEntry();
        if (norm == null) {
            LOGGER.warn("No normalization function to display.");
            return;
        }
        if (norm instanceof Exponential) {
            buildFormPart((Exponential) norm);
        } else if (norm instanceof Linear) {
            buildFormPart((Linear) norm);
        } else if (norm instanceof MultiLinear) {
            buildFormPart((MultiLinear) norm);
        } else if (norm instanceof Normal) {
            buildFormPart((Normal) norm);
        } else if (norm instanceof Sigmoid) {
            buildFormPart((Sigmoid) norm);
        } else {
            throw new UnsupportedOperationException("Handling "
                    + norm.getClass().getCanonicalName() + " not implemented!");
        }
    }

    /**
     * Build the form to modify and view the function.
     *
     * @param n normalization function to edit and view
     */
    private void buildFormPart(final Sigmoid n) {
        LOGGER.traceEntry(n.getName());
        final double amountToStepBy = 1;
        final double initialValue = 0;
        final double max = 1000;
        final double min = -1000;
        final Label labelA = newLabel("detail.sigmoid.a.label");
        final Label labelB = newLabel("detail.sigmoid.b.label");
        final Spinner<Double> fieldA = ComponentUtil.newDoubleSpinner(min, max, initialValue, amountToStepBy);
        final Spinner<Double> fieldB = ComponentUtil.newDoubleSpinner(min, max, initialValue, amountToStepBy);

        final ChangeListener<Double> lis = (final ObservableValue<? extends Double> o,
                final Double oldValue, final Double newValue) -> {
                    if (oldValue != null && !oldValue.equals(newValue)) {
                        hideChart();
                        final Number a = fieldA.getValue();
                        final Number b = fieldB.getValue();
                        final Sigmoid norm = new Sigmoid(a.doubleValue(), b.doubleValue());
                        buildChart(norm);
                    }
                };

                fieldA.valueProperty().addListener(lis);
                fieldB.valueProperty().addListener(lis);

                ComponentUtil.bindSpinnerToDouble(fieldA, n,
                        Messages.getString("detail.sigmoid.a.attribute"));
                ComponentUtil.bindSpinnerToDouble(fieldB, n,
                        Messages.getString("detail.sigmoid.b.attribute"));
                getInnerGridPane().add(labelA, 0, 0);
                getInnerGridPane().add(fieldA, 1, 0);
                getInnerGridPane().add(labelB, 0, 1);
                getInnerGridPane().add(fieldB, 1, 1);
                getInnerGridPane().add(chartViewParent, 0, 2, 2, 1);
                lis.changed(null, Double.NaN, null);
    }

    /**
     * Hide the chart in the panel.
     */
    private void hideChart() {
        LOGGER.traceEntry();
        chartViewParent.setVisible(false);
    }

    /**
     * Display MathML formula in embedded HTML navigator.
     * @param norm function to display
     */
    private void showFormula(final NormalizationFunction norm) {
        final String mathtagstart = "<math "
                + "xmlns=\"http://www.w3.org/1998/Math/MathML\" "
                + "style=\"float: right\">";
        formula.getEngine().loadContent(mathtagstart
                + norm.getFormulaMathML()
                + "</math>");
    }
}

/*
 * Copyright (C) 2020 INRAE Agroclim
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

import java.net.URL;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.indicators.model.function.normalization.NormalizationFunction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.extern.log4j.Log4j2;

/**
 * Display normalization function chart.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class FunctionChartController implements Initializable {
    /**
     * Initial value of maximum abscissa.
     */
    private static final double INITIAL_X_AXIS_MAX = 100;

    /**
     * Initial value of minimum abscissa.
     */
    private static final double INITIAL_X_AXIS_MIN = 0;

    /**
     * Initial value of minimum abscissa.
     */
    private static final double INITIAL_X_AXIS_STEP = 1;

    /**
     * Representation of normalization function.
     */
    @FXML
    private LineChart<Number, Number> chart;

    /**
     * Upper bound of abscissa.
     */
    @FXML
    private Spinner<Double> max;

    /**
     * Lower bound of abscissa.
     */
    @FXML
    private Spinner<Double> min;

    /**
     * Step of ticks in abscissa.
     */
    @FXML
    private Spinner<Double> step;

    /**
     * Upper bound of X axis.
     */
    private double xAxisMax = INITIAL_X_AXIS_MAX;

    /**
     * Lower bound of X axis.
     */
    private double xAxisMin = INITIAL_X_AXIS_MIN;

    /**
     * Step of X axis.
     */
    private double xAxisStep = INITIAL_X_AXIS_STEP;

    /**
     * Points for the chart.
     */
    private final ObservableList<XYChart.Data<Number, Number>> data
    = FXCollections.observableArrayList();

    /**
     * Displayed function.
     */
    private NormalizationFunction currentNorm;

    /**
     * @param norm normalization function to display
     * @param yAxis limits of Y axis
     */
    public final void buildChart(final NormalizationFunction norm,
            final NumberAxis yAxis) {
        LOGGER.traceEntry();
        currentNorm = norm;
        data.clear();
        if (yAxis.getTickUnit() <= 0) {
            yAxis.setTickUnit(1);
        }
        if (xAxisStep == 0) {
            xAxisStep = 1;
        }

        final NumberAxis ordonate = (NumberAxis) chart.getYAxis();
        ordonate.lowerBoundProperty().set(yAxis.getLowerBound());
        ordonate.upperBoundProperty().set(yAxis.getUpperBound());
        ordonate.tickUnitProperty().set(yAxis.getTickUnit());

        for (double i = xAxisMin + xAxisStep; i <= xAxisMax; i += xAxisStep) {
            data.add(new XYChart.Data<>(i, norm.normalize(i)));
        }

        LOGGER.traceExit();
    }

    @Override
    public final void initialize(final URL url, final ResourceBundle rb) {

        final XYChart.Series<Number, Number> series;
        series = new XYChart.Series<>(data);
        chart.getData().add(series);

        // Add tooltip on chart
        // Mouse location is scene
        final ObjectProperty<Point2D> mouseLoc = new SimpleObjectProperty<>();

        final Tooltip tooltip = new Tooltip();

        chart.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
            if (!tooltip.isShowing()) {
                mouseLoc.set(
                        new Point2D(evt.getSceneX(), evt.getSceneY()));
            }
        });

        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        tooltip.textProperty().bind(Bindings.createStringBinding(() -> {
            if (mouseLoc.isNull().get()) {
                return "";
            }
            final double xInXAxis = xAxis.sceneToLocal(mouseLoc.get()).getX();
            final double x = xAxis.getValueForDisplay(xInXAxis).doubleValue();
            final double yInYAxis = yAxis.sceneToLocal(mouseLoc.get()).getY();
            final double y = yAxis.getValueForDisplay(yInYAxis).doubleValue();
            return String.format("[x=%.3f, y=%.3f]", x, y);
        }, mouseLoc, xAxis.lowerBoundProperty(), xAxis.upperBoundProperty(),
                yAxis.lowerBoundProperty(), yAxis.upperBoundProperty()));

        Tooltip.install(chart, tooltip);

        //
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        //
        ComponentUtil.addKeyReleasedEventHandling(max, Double::parseDouble);
        ComponentUtil.addKeyReleasedEventHandling(min, Double::parseDouble);
        ComponentUtil.addKeyReleasedEventHandling(step, Double::parseDouble);
        max.getValueFactory().setValue(xAxisMax);
        min.getValueFactory().setValue(xAxisMin);
        step.getValueFactory().setValue(xAxisStep);
        max.valueProperty().addListener(observable -> onMaxChanged(null));
        min.valueProperty().addListener(observable -> onMinChanged(null));
        step.valueProperty().addListener(observable -> onStepChanged(null));
    }

    /**
     * @param event event not used
     */
    @FXML
    final void onMaxChanged(final KeyEvent event) {
        LOGGER.traceEntry();
        xAxisMax = max.getValue();
        final NumberAxis abscissa = (NumberAxis) chart.getXAxis();
        abscissa.upperBoundProperty().set(xAxisMax);
        buildChart(currentNorm, (NumberAxis) chart.getYAxis());
    }

    /**
     * @param event event not used
     */
    @FXML
    final void onMinChanged(final KeyEvent event) {
        LOGGER.traceEntry();
        xAxisMin = min.getValue();
        final NumberAxis abscissa = (NumberAxis) chart.getXAxis();
        abscissa.lowerBoundProperty().set(xAxisMin);
        buildChart(currentNorm, (NumberAxis) chart.getYAxis());
    }

    /**
     * @param event event not used
     */
    @FXML
    final void onResetAction(final ActionEvent event) {
        LOGGER.traceEntry();

        max.getValueFactory().setValue(INITIAL_X_AXIS_MAX);
        min.getValueFactory().setValue(INITIAL_X_AXIS_MIN);
        step.getValueFactory().setValue(INITIAL_X_AXIS_STEP);
    }

    /**
     * @param event event not used
     */
    @FXML
    final void onStepChanged(final KeyEvent event) {
        LOGGER.traceEntry();
        xAxisStep = step.getValue();
        final NumberAxis abscissa = (NumberAxis) chart.getXAxis();
        abscissa.tickUnitProperty().set(xAxisStep);
        buildChart(currentNorm, (NumberAxis) chart.getYAxis());
    }
}

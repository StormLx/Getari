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
package fr.inrae.agroclim.getari.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.indicators.model.function.normalization.MultiLinear;
import fr.inrae.agroclim.indicators.model.function.normalization.MultiLinearInterval;
import fr.inrae.agroclim.indicators.model.function.normalization.NormalizationFunction;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;
import lombok.Setter;

/**
 * Build form in a GridPane for MultiLinear..
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class MultiLinearFormBuilder {

    /**
     * Fixed cell height.
     */
    private static final int CELL_SIZE = 25;

    /**
     * Height of table.
     */
    private static final int TABLE_HEIGHT = 100;

    /**
     * To sort rows.
     */
    private final Comparator<MultiLinearInterval> comparator = (o1, o2) -> {
        if (o2 == null) {
            return 1;
        }
        final int comp = Comparator.nullsFirst(Double::compare).compare(o1.getMin(), o2.getMin());
        if (comp != 0) {
            return comp;
        }
        return Comparator.nullsLast(Double::compare).compare(o1.getMax(), o2.getMax());
    };

    /**
     * Normalization function to edit.
     */
    @Setter
    private MultiLinear normalizationFunction;

    /**
     * Actions to call on updated normalization function.
     */
    @Setter
    private Consumer<NormalizationFunction> onUpdate;

    /**
     * The parent node for chart view.
     */
    @Setter
    private Parent chartViewParent;

    /**
     * GridPane in the parent widget.
     */
    @Setter
    private GridPane gridPane;

    /**
     * Built table.
     */
    private TableView<MultiLinearInterval> table;

    /**
     * Build the form in the GridPane.
     */
    public void build() {
        final ObservableList<MultiLinearInterval> intervals = FXCollections.observableArrayList();
        if (normalizationFunction.getIntervals() != null) {
            intervals.addAll(normalizationFunction.getIntervals());
        }

        table = new TableView<>();
        table.setEditable(true);
        table.setPrefHeight(TABLE_HEIGHT);
        table.setItems(intervals);
        table.setFixedCellSize(CELL_SIZE);
        final double headerRatio = 1.01;
        table.prefHeightProperty().bind(
                table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(headerRatio)));

        final TableColumn<MultiLinearInterval, Double> minCol = createColumn("min");
        table.getColumns().add(minCol);
        final TableColumn<MultiLinearInterval, Double> maxCol = createColumn("max");
        table.getColumns().add(maxCol);
        final TableColumn<MultiLinearInterval, Double> aCol = createColumn("linearA");
        table.getColumns().add(aCol);
        final TableColumn<MultiLinearInterval, Double> bCol = createColumn("linearB");
        table.getColumns().add(bCol);

        table.prefWidthProperty().bind(gridPane.widthProperty());

        final Button add = new Button(Messages.getString("detail.multilinearinterval.add"));
        add.setOnAction(e -> intervals.add(new MultiLinearInterval()));
        final Button delete = new Button(Messages.getString("detail.multilinearinterval.delete"));
        delete.setOnAction(e -> {
            table.getItems().remove(table.getSelectionModel().getSelectedIndex());
            update();
        });
        gridPane.add(table, 0, 0, 2, 1);
        gridPane.add(add, 0, 1);
        gridPane.add(delete, 1, 1);
        gridPane.add(chartViewParent, 0, 2, 2, 1);
        update();
    }

    /**
     * @param property The name of the property with which to attempt to reflectively extract a corresponding value for
     * in a given object.
     * @return created column
     */
    private TableColumn<MultiLinearInterval, Double> createColumn(final String property) {
        final int nbCols = 4;
        final int verticalScrollWidth = 15;
        final DoubleBinding colWidth = table.widthProperty()
                .subtract(gridPane.getPadding().getLeft())
                .subtract(gridPane.getPadding().getRight())
                .subtract(verticalScrollWidth)
                .divide(nbCols);

        final TableColumn<MultiLinearInterval, Double> col = new TableColumn<>();
        final String key = "detail.multilinearinterval." + property + ".label";
        col.setText(Messages.getString(key));
        col.setReorderable(false);
        col.setSortable(false);
        col.prefWidthProperty().bind(colWidth);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        col.setOnEditCommit((final CellEditEvent<MultiLinearInterval, Double> t) -> {
            final ObservableList<MultiLinearInterval> intervals = t.getTableView().getItems();
            final MultiLinearInterval interval = intervals.get(t.getTablePosition().getRow());
            final Double val = t.getNewValue();
            switch (property) {
            case "linearA":
                interval.setLinearA(val);
                break;
            case "linearB":
                interval.setLinearB(val);
                break;
            case "max":
                interval.setMax(val);
                break;
            case "min":
                interval.setMin(val);
                break;
            default:
                throw new UnsupportedOperationException("Property not handled: " + property);
            }
            update();
        });
        return col;
    }

    /**
     * Update model and refresh interface.
     */
    private void update() {
        FXCollections.sort(table.getItems(), comparator);
        normalizationFunction.setIntervals(new ArrayList<>());
        normalizationFunction.getIntervals().addAll(table.getItems());
        onUpdate.accept(normalizationFunction);
    }
}

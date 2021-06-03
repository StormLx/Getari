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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.CsvWriter;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariFileChooser;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.getari.view.visitor.Cpt;
import fr.inrae.agroclim.indicators.model.AnnualPhase;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.result.EvaluationResult;
import fr.inrae.agroclim.indicators.model.result.IndicatorResult;
import fr.inrae.agroclim.indicators.model.result.PhaseResult;
import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * View controller to show evaluation results.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class ResultsController extends AbstractController implements Initializable {
    /**
     * Width (px) for all columns, except year.
     */
    private static final double COL_WIDTH = 95;
    /**
     * CSS style for header label.
     */
    private static final String CSS_HEADER_LABEL = "headerLabel";
    /**
     * Number format for CSV (. as decimal).
     */
    private static final NumberFormat CSV_NF = NumberFormat.getNumberInstance(Locale.ENGLISH);
    /**
     * Character between CSV values.
     */
    private static final String CSV_SEPARATOR = ",";
    /**
     * Formatter for phase dates.
     */
    private static final DateFormat DF = DateFormat.getDateInstance(DateFormat.SHORT);
    /**
     * Column header height.
     */
    private static final int HEADER_HEIGHT = 60;
    /**
     * Padding between cells.
     */
    private static final int PADDING = 2;
    /**
     * Width (px) for the year column.
     */
    private static final int YEAR_WIDTH = 60;

    /**
     * @param date date to format
     * @return date representation or NA
     */
    private static String dateFormat(final Date date) {
        if (date == null) {
            return "NA";
        }
        return DF.format(date);
    }

    /**
     * @param category indicator category
     * @return translation for the category
     */
    private static String getStringByCategory(
            final IndicatorCategory category) {
        switch (category) {
        case PHENO_PHASES:
            return Messages.getString("graph.header.phases");
        case ECOPHYSIOLOGICAL_PROCESSES:
            return Messages.getString("graph.header.practices");
        case CULTURAL_PRATICES:
            return Messages.getString("graph.header.practices");
        case CLIMATIC_EFFECTS:
            return Messages.getString("graph.header.effects");
        case INDICATORS:
            return Messages.getString("graph.header.indicators");
        case EVALUATION:
            return Messages.getString("graph.header.feasability");
        default:
            throw new RuntimeException("Unknown category: " + category);
        }
    }

    /**
     * Close button.
     */
    @FXML
    private Button close;
    /**
     * Grid pane to display resuls.
     */
    @FXML
    private GridPane gridPane;
    /**
     * Localization.
     */
    private I18n msg;
    /**
     * Number of values > 1.
     */
    private int nbOfGreaterThanOne = 0;
    /**
     * To format values for grid.
     */
    private final NumberFormat nf = NumberFormat
            .getNumberInstance();
    /**
     * Formated cells CSV and clipboard.
     */
    private final List<List<String>> resultsArray = new ArrayList<>();
    /**
     * Counter for the grid.
     */
    private final Cpt rowCounter = new Cpt();
    /**
     * The row number where to display warnings.
     */
    private int rowNbForWarnings;
    /**
     * Save button.
     */
    @FXML
    private Button saveas;

    /**
     * Draw cell for the indicator value.
     *
     * @param resultsRow list for CSV values in row
     * @param value      indicator value
     */
    private void displayCell(final List<String> resultsRow, final Double value) {
        displayCell(resultsRow, value, false);
    }

    /**
     * Draw cell for the indicator value.
     *
     * @param resultsRow list for CSV values in row
     * @param value      indicator value
     * @param check      highlight and count values > 1
     */
    private void displayCell(final List<String> resultsRow, final Double value, final boolean check) {
        String csvVal;
        String val;
        if (value == null) {
            val = "N/A";
            csvVal = "N/A";
        } else if (value.equals(Double.NaN)) {
            LOGGER.trace("value == Double.NaN");
            val = "NaN";
            csvVal = "NaN";
        } else {
            val = nf.format(value);
            csvVal = CSV_NF.format(value);
        }
        final Label label = new Label(val);
        if (check && value != null && !value.equals(Double.NaN) && value > 1) {
            label.setTextFill(Color.web("#a30038"));
            nbOfGreaterThanOne++;
        }

        gridPane.add(label, rowCounter.getCol(), rowCounter.getRow());
        rowCounter.nextCol();
        GridPane.setMargin(label, new Insets(0, 0, 0, PADDING));
        resultsRow.add(csvVal);
    }

    /**
     * Draw cell for the year.
     *
     * @param resultsRow list for CSV values in row
     * @param year       year
     */
    private void displayCell(final List<String> resultsRow, final int year) {
        String csvVal;
        String val;
        val = "" + year;
        csvVal = "" + year;
        final Label label = new Label(val);
        label.setPrefWidth(YEAR_WIDTH);
        gridPane.add(label, rowCounter.getCol(), rowCounter.getRow());
        rowCounter.nextCol();
        GridPane.setMargin(label, new Insets(0, 0, 0, PADDING));
        resultsRow.add(csvVal);
    }

    /**
     * Draw cell for the indicator value.
     *
     * @param resultsRow list for CSV values in row
     * @param value      string to display
     */
    private void displayCell(final List<String> resultsRow, final String value) {
        displayCell(resultsRow, value, 1);
    }

    /**
     * Draw cell for the year.
     *
     * @param resultsRow list for CSV values in row
     * @param value      value to display
     * @param size       number of column used for the header
     */
    private void displayCell(final List<String> resultsRow, final String value, final int size) {
        String csvVal;
        String val;
        if (value == null) {
            val = "";
            csvVal = "";
        } else {
            val = value;
            csvVal = value;
        }
        final Label label = new Label(val);
        if (size > 1) {
            GridPane.setColumnSpan(label, size);
            final double hgap = gridPane.getHgap();
            label.setPrefWidth((COL_WIDTH + PADDING * 2 + hgap) * size);
        }
        gridPane.add(label, rowCounter.getCol(), rowCounter.getRow());
        rowCounter.nextCol();
        GridPane.setMargin(label, new Insets(0, 0, 0, PADDING));
        resultsRow.add(csvVal);
    }

    /**
     * Draw header of 1st line in table.
     *
     * @param resultsRow list for CSV values in row
     * @param title header title
     * @param size number of columns used for the header
     * @param height cell height
     */
    private void displayHeader(final List<String> resultsRow,
            final String title, final int size, final double height) {
        final Label label = new Label(title);
        label.setPrefHeight(height);
        label.setAlignment(Pos.BASELINE_LEFT);
        label.getStyleClass().add(CSS_HEADER_LABEL);
        label.setWrapText(true);
        if (msg.get("data.year").equals(title)) {
            label.setPrefWidth(YEAR_WIDTH);
        } else if (msg.get("data.phases").equals(title)) {
            GridPane.setColumnSpan(label, size);
            label.setPrefWidth((COL_WIDTH + PADDING * 2) * size);
        } else if (size > 1) {
            GridPane.setColumnSpan(label, size);
            final double hgap = gridPane.getHgap();
            label.setPrefWidth((COL_WIDTH + PADDING * 2 + hgap) * size);
        } else {
            label.setPrefWidth(COL_WIDTH);
        }
        gridPane.add(label, rowCounter.getCol(), rowCounter.getRow());
        rowCounter.nextCol(size);
        resultsRow.add(label.getText());
        // empty CSV cells after
        for (int i = 1; i < size; i++) {
            resultsRow.add("");
        }
    }

    /**
     * Draw header for an indicator in 2nd line in table.
     *
     * @param resultsRow list for CSV values in row
     * @param parent name of climatic effect
     * @param title header title
     * @param height cell height
     */
    private void displayHeader(final List<String> resultsRow,
            final String parent, final String title, final double height) {
        final Label columnLabel = new Label(title);
        columnLabel.setWrapText(true);
        columnLabel.getStyleClass().add(CSS_HEADER_LABEL);
        columnLabel.setPrefWidth(COL_WIDTH);

        final VBox columnHeader = new VBox();
        columnHeader.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        columnHeader.getStyleClass().add("header");

        final Label parentLabel = new Label(msg.format("indicator.parent", parent));
        parentLabel.setWrapText(true);
        parentLabel.setPrefWidth(COL_WIDTH);
        parentLabel.getStyleClass().add("headerParent");
        columnHeader.getChildren().addAll(columnLabel, parentLabel);

        columnLabel.setPrefWidth(COL_WIDTH * 2);
        columnHeader.setPrefHeight(height);

        final Label normalizedDataLabel = new Label(msg.get("data.normalized"));
        normalizedDataLabel.setWrapText(true);
        normalizedDataLabel.setPrefWidth(COL_WIDTH);
        normalizedDataLabel.getStyleClass().add(CSS_HEADER_LABEL);

        final Label rawDataLabel = new Label(msg.get("data.raw"));
        rawDataLabel.setWrapText(true);
        rawDataLabel.setPrefWidth(COL_WIDTH);
        rawDataLabel.getStyleClass().add(CSS_HEADER_LABEL);

        final HBox dataHeader = new HBox();
        dataHeader.setPrefWidth(COL_WIDTH * 2);
        dataHeader.getChildren().addAll(normalizedDataLabel, rawDataLabel);
        columnHeader.getChildren().add(dataHeader);
        gridPane.add(columnHeader, rowCounter.getCol(), rowCounter.getRow());
        GridPane.setColumnSpan(columnHeader, 2);
        rowCounter.nextCol(2);

        final StringJoiner sj = new StringJoiner("~");
        // add twice header in CSV text for data label
        boolean doubleHeader = false;
        for (final Node node : columnHeader.getChildren()) {
            if (node instanceof Label) {
                sj.add(((Labeled) node).getText());
            } else {
                for (final Node labelNode : ((Pane) node).getChildren()) {
                    final String text = ((Labeled) labelNode).getText();
                    if (msg.get("data.normalized").equals(text)
                            || msg.get("data.raw").equals(text)) {
                        doubleHeader = true;
                    } else {
                        sj.add(text);
                    }
                }
            }
        }
        if (doubleHeader) {
            resultsRow.add(sj.toString() + "~" + msg.get("data.normalized"));
            resultsRow.add(sj.toString() + "~" + msg.get("data.raw"));
        } else {
            resultsRow.add(sj.toString());
        }
    }

    /**
     * Draw header of 1st line in table.
     *
     * @param resultsRow list for CSV values in row
     * @param title header title
     */
    private void displayHeaderPhase(final List<String> resultsRow,
            final String title) {
        final Label label = new Label(title);
        label.setPrefHeight(HEADER_HEIGHT);
        label.setAlignment(Pos.BASELINE_LEFT);
        label.getStyleClass().add(CSS_HEADER_LABEL);
        label.setWrapText(true);
        // number of column used for the header
        final int size = 2;
        GridPane.setColumnSpan(label, size);
        label.setPrefWidth((COL_WIDTH + PADDING * 2) * size);
        gridPane.add(label, rowCounter.getCol(), rowCounter.getRow());
        rowCounter.nextCol(size);
        resultsRow.add(label.getText());
        // empty CSV cells after
        for (int i = 1; i < size; i++) {
            resultsRow.add("");
        }
    }

    /**
     * Add metadata to display.
     */
    private void displayMetadata() {
        final Evaluation evaluation = GetariApp.get().getCurrentEval();
        final EvaluationSettings settings = evaluation.getSettings();
        final List<CompositeIndicator> phases = evaluation.getPhases();
        final int span = 3;
        List<String> resultsRow;
        resultsRow = new ArrayList<>();
        resultsRow.add("");
        rowCounter.setCol(1);
        displayHeader(resultsRow, msg.get("data.files"), span, HEADER_HEIGHT);
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("data.climate"), 1, HEADER_HEIGHT);
        displayCell(resultsRow, settings.getClimateLoader().getFile().getPath(), 1 + phases.size());
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("data.phenology"), 1, HEADER_HEIGHT);
        displayCell(resultsRow, settings.getPhenologyLoader().getFile().getPath(), 1 + phases.size());
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("evaluation"), 1, HEADER_HEIGHT);
        displayCell(resultsRow, settings.getFilePath(), 1 + phases.size());
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        resultsRow.add("");
        rowCounter.setCol(1);
        displayHeader(resultsRow, msg.get("run"), span, HEADER_HEIGHT);
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, "GETARI", 1, HEADER_HEIGHT);
        displayCell(resultsRow, Version.getString("version") + " " + Version.getString("build.number") + " "
                + Version.getString("build.date"), 1 + phases.size());
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("run.datetime"), 1, HEADER_HEIGHT);
        displayCell(resultsRow, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), 1 + phases.size());
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        // empty row to display warnings
        rowNbForWarnings = rowCounter.getRow();
        resultsRow = new ArrayList<>();
        displayCell(resultsRow, "");
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(0);
        //
        final List<String> phaseIds = evaluation.getComputedPhases().stream().map(AnnualPhase::getUid).distinct()
                .collect(Collectors.toList());
        Collections.sort(phases);
        final Map<Integer, Map<String, List<String>>> data = new HashMap<>();
        evaluation.getComputedPhases().forEach(phase -> {
            final Integer year = phase.getHarvestYear();
            if (!data.containsKey(year)) {
                data.put(year, new HashMap<>());
            }
            final String pid = phase.getUid();
            data.get(year).put(pid, Arrays.asList(dateFormat(phase.getStart()), dateFormat(phase.getEnd())));
        });

        resultsRow = new ArrayList<>();
        resultsRow.add("");
        rowCounter.setCol(1);
        displayHeader(resultsRow, msg.get("data.phases"), 2 * phases.size(), HEADER_HEIGHT);
        resultsArray.add(resultsRow);
        rowCounter.nextRow();
        rowCounter.setCol(1);
        resultsRow = new ArrayList<>();
        resultsRow.add("");
        for (final String phaseId : phaseIds) {
            displayHeaderPhase(resultsRow, phaseId);
        }
        resultsArray.add(resultsRow);

        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("data.phase.year"), 1, HEADER_HEIGHT);
        for (int i = 0; i < phaseIds.size(); i++) {
            displayHeader(resultsRow, msg.get("data.phase.start"), 1, HEADER_HEIGHT);
            displayHeader(resultsRow, msg.get("data.phase.end"), 1, HEADER_HEIGHT);
        }
        resultsArray.add(resultsRow);
        final List<Integer> years = new ArrayList<>(data.keySet());
        Collections.sort(years);
        for (final Integer year : years) {
            rowCounter.nextRow();
            rowCounter.setCol(0);
            resultsRow = new ArrayList<>();
            displayCell(resultsRow, year);
            for (final String phaseId : phaseIds) {
                for (final String val : data.get(year).get(phaseId)) {
                    displayCell(resultsRow, val);
                }
            }
            resultsArray.add(resultsRow);
        }
        rowCounter.nextRow();
        rowCounter.setCol(0);
    }

    /**
     * @param indicatorId id of indicator to find
     * @return localized name of indicator
     */
    private String getIndicatorName(final String indicatorId) {
        final Indicator ind = GetariApp.get().getCurrentEval().getSettings().getKnowledge().getIndicator(indicatorId);
        if (ind == null) {
            LOGGER.warn("No indicator found for {}", indicatorId);
            return indicatorId;
        }
        String langCode;
        if (GetariApp.get().getPreferences().getLocale() == null) {
            langCode = Locale.getDefault().getLanguage();
        } else {
            langCode = GetariApp.get().getPreferences().getLocale().getLanguage();
        }
        return ind.getName(langCode);
    }

    /**
     * @return Save button.
     */
    public final Button getSaveButton() {
        return saveas;
    }

    @Override
    protected final Stage getStage() {
        return (Stage) close.getScene().getWindow();
    }

    @Override
    public final void initialize(final URL url, final ResourceBundle rb) {
        msg = new I18n(rb);
    }

    /**
     * Draw results into grid pane.
     */
    public final void loadResults() {
        final long startTime = System.currentTimeMillis();
        final Evaluation evaluation = GetariApp.get().getCurrentEval();
        final Map<Integer, EvaluationResult> results = evaluation.getResults();

        final List<Integer> years = results.keySet().stream().sorted()
                .collect(Collectors.toList());

        // phases
        final List<String> phases = evaluation.getPhases().stream()
                .map(CompositeIndicator::getId).collect(Collectors.toList());
        Collections.sort(phases);
        List<String> resultsRow;

        // Metadata
        displayMetadata();

        /*
         * First table: values for evaluation and phases
         */

        /* 1st line: categories */
        resultsRow = new ArrayList<>();
        resultsRow.add("");
        rowCounter.setCol(1);
        displayHeader(resultsRow,
                getStringByCategory(IndicatorCategory.EVALUATION), 1,
                HEADER_HEIGHT);
        displayHeader(resultsRow,
                getStringByCategory(IndicatorCategory.PHENO_PHASES),
                phases.size(), HEADER_HEIGHT);
        resultsArray.add(resultsRow);

        /* 2nd line: details */
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        displayHeader(resultsRow, msg.get("data.year"), 1,
                HEADER_HEIGHT);
        displayHeader(resultsRow, msg.get("feasability"), 1,
                HEADER_HEIGHT);
        for (final String phase : phases) {
            displayHeader(resultsRow, msg.format("data.phase", phase), 1,
                    HEADER_HEIGHT);
        }
        resultsArray.add(resultsRow);
        int nbCols = rowCounter.getCol() + 1;

        /* next : results for faisability and phases */
        rowCounter.nextRow();
        for (final Integer year : years) {
            final EvaluationResult evaluationResult = results.get(year);
            rowCounter.nextRow();
            rowCounter.setCol(0);
            resultsRow = new ArrayList<>();
            displayCell(resultsRow, year);
            displayCell(resultsRow, evaluationResult.getNormalizedValue(),
                    true);
            for (final PhaseResult phaseResult : evaluationResult.getPhaseResults()) {
                displayCell(resultsRow, phaseResult.getNormalizedValue(), true);
            }
            resultsArray.add(resultsRow);
        }

        /*
         * Next tables: values for indicators for each phase.
         */
        for (final String phase : phases) {
            nbCols = loadResultsPhase(results, years, phase, nbCols);
        }
        final ColumnConstraints constraint = gridPane.getColumnConstraints().get(1);
        final int start = gridPane.getColumnConstraints().size() + 1;
        for (int i = start; i < nbCols; i++) {
            gridPane.getColumnConstraints().add(constraint);
        }

        // display warning
        if (nbOfGreaterThanOne > 0) {
            final int span = 4;
            final String text = msg.format(nbOfGreaterThanOne,
                    "warning.normalized.greather.than.one",
                    nbOfGreaterThanOne);
            final Label label = new Label();
            label.setPrefWidth(COL_WIDTH * span);
            label.setTextFill(Color.web("#a30038"));
            label.setText(text);
            label.setWrapText(true);
            resultsArray.get(rowNbForWarnings).set(0, text);
            gridPane.add(label, 0, rowNbForWarnings);
            GridPane.setColumnSpan(label, span);
        }
        final long duration = System.currentTimeMillis() - startTime;
        final double msToS = 1000.;
        LOGGER.traceExit("Results rendered in {} seconds", duration / msToS);
    }

    /**
     * Load CSV file.
     *
     * @param dataFile result file
     */
    public final void loadResults(final File dataFile) {
        resultsArray.clear();
        saveas.setDisable(true);

        int nbCols = 0;
        try (
                BufferedReader br = new BufferedReader(new FileReader(dataFile));
                ) {
            String line = br.readLine();

            while (line != null) {
                final String[] splittedLine = line.split(CSV_SEPARATOR);
                final List<String> listLine = new ArrayList<>();
                for (final String splittedLine1 : splittedLine) {
                    listLine.add(splittedLine1.replace("\n", ""));
                }
                if (splittedLine.length > nbCols) {
                    nbCols = splittedLine.length;
                }

                resultsArray.add(listLine);

                line = br.readLine();
            }
        } catch (final IOException e) {
            LOGGER.error(e);
        }

        boolean isFirstHeader;
        boolean isHeader;

        final ColumnConstraints constraint = gridPane.getColumnConstraints().get(1);
        final int start = gridPane.getColumnConstraints().size() + 1;
        for (int i = start; i < nbCols; i++) {
            gridPane.getColumnConstraints().add(constraint);
        }

        rowCounter.setCol(0);
        rowCounter.setRow(0);

        for (final List<String> row : resultsArray) {

            final String firstCell = row.get(0);
            isFirstHeader = firstCell.isEmpty();
            isHeader = firstCell.toLowerCase().matches(".*[A-Za-z].*");
            rowCounter.setCol(0);

            // fill cells according to next number of cells
            if (isFirstHeader) {
                final int rowIndex = resultsArray.indexOf(row);
                if (resultsArray.size() > rowIndex + 1) {
                    final List<String> nextRow = resultsArray.get(rowIndex + 1);
                    for (int i = row.size(); i <= nextRow.size(); i++) {
                        row.add("");
                    }
                }
            }

            for (final String result : row) {
                Label resultLabel = new Label(result);
                if (isFirstHeader) {
                    if (result.isEmpty() && rowCounter.getCol() != 0) {
                        // cell is already in previous colspan
                        continue;
                    }
                    int colspan = 1;
                    final int index = rowCounter.getCol();
                    if (row.size() > colspan + index + 1) {
                        String next = row.get(colspan + index);
                        while (next.isEmpty()) {
                            if (row.size() == colspan + index + 1) {
                                break;
                            }
                            colspan++;
                            next = row.get(colspan + index);
                        }
                    }
                    resultLabel.setPrefWidth(COL_WIDTH * colspan
                            + PADDING * colspan - 1);
                    resultLabel.setPrefHeight(HEADER_HEIGHT);
                    resultLabel.setAlignment(Pos.BASELINE_LEFT);
                    resultLabel.setWrapText(true);
                    resultLabel.getStyleClass().add("cheader");
                    gridPane.add(resultLabel, rowCounter.getCol(),
                            rowCounter.getRow());
                    GridPane.setColumnSpan(resultLabel, colspan);
                    rowCounter.nextCol(colspan - 1);
                } else if (isHeader) {
                    final VBox columnHeader = new VBox();
                    columnHeader.setPrefHeight(HEADER_HEIGHT);
                    columnHeader.setPadding(new Insets(PADDING, PADDING,
                            PADDING, PADDING));
                    columnHeader.getStyleClass().add("header");
                    final String[] header = result.split("~");

                    resultLabel = new Label(header[0]);
                    resultLabel.setWrapText(true);
                    resultLabel.getStyleClass().add(CSS_HEADER_LABEL);
                    resultLabel.setPrefWidth(COL_WIDTH);
                    columnHeader.getChildren().add(resultLabel);

                    if (header.length > 1) {
                        final Label resultParent = new Label(header[1]);
                        resultParent.setWrapText(true);
                        resultParent.getStyleClass().add("headerParent");
                        resultParent.setPrefWidth(COL_WIDTH);
                        columnHeader.getChildren().add(resultParent);
                        if (header.length > 2) {
                            final Label dataLabel = new Label(header[2]);
                            dataLabel.setWrapText(true);
                            dataLabel.setPrefWidth(COL_WIDTH);
                            dataLabel.getStyleClass().add(CSS_HEADER_LABEL);
                            columnHeader.getChildren().add(dataLabel);
                        }
                    }
                    gridPane.add(columnHeader, rowCounter.getCol(),
                            rowCounter.getRow());
                } else {
                    gridPane.add(resultLabel, rowCounter.getCol(),
                            rowCounter.getRow());
                    GridPane.setMargin(resultLabel,
                            new Insets(0, 0, 0, PADDING));
                }
                rowCounter.nextCol();
            }
            rowCounter.nextRow();
            rowCounter.setCol(0);
        }

    }

    /**
     * Draw results into the grid pane for the phase.
     *
     * @param results results of computation by year
     * @param years sorted list of years
     * @param phase phase id
     * @param nbCols found number of columns in the table
     * @return number of columns in the table, after drawing results for the
     * phase
     */
    private int loadResultsPhase(final Map<Integer, EvaluationResult> results,
            final List<Integer> years, final String phase, final int nbCols) {
        if (years.isEmpty()) {
            LOGGER.warn("No years! No results? Years in results: {}",
                    results.keySet());
            return 0;
        }
        rowCounter.nextRow();
        final Integer firstYear = years.get(0);
        final Optional<PhaseResult> optional = results.get(firstYear)
                .getPhaseResults()
                .stream()
                .filter(phaseR -> phaseR.getPhaseId().equals(phase))
                .findFirst();
        if (!optional.isPresent()) {
            LOGGER.warn("No results for phase {}", phase);
            return nbCols;
        }
        int newNbCols = nbCols;
        final PhaseResult phaseResult = optional.get();
        final List<IndicatorResult> processes = phaseResult.getIndicatorResults();
        int nbOfIndicators = 0;
        for (final IndicatorResult process : processes) {
            for (final IndicatorResult effect : process.getIndicatorResults()) {
                nbOfIndicators += effect.getIndicatorResults().size();
            }
        }
        /* 1st line: categories */
        List<String> resultsRow = new ArrayList<>();
        resultsRow.add("");
        rowCounter.setCol(1);
        displayHeader(resultsRow,
                getStringByCategory(IndicatorCategory.PHENO_PHASES), 1,
                HEADER_HEIGHT);
        for (final IndicatorResult process : processes) {
            displayHeader(resultsRow,
                    getStringByCategory(
                            IndicatorCategory.CULTURAL_PRATICES), 1,
                    HEADER_HEIGHT);
            displayHeader(resultsRow,
                    getStringByCategory(IndicatorCategory.CLIMATIC_EFFECTS),
                    process.getIndicatorResults().size(), HEADER_HEIGHT);
        }
        displayHeader(resultsRow,
                getStringByCategory(IndicatorCategory.INDICATORS),
                nbOfIndicators * 2, HEADER_HEIGHT);
        resultsArray.add(resultsRow);
        /* 2nd line: details */
        rowCounter.nextRow();
        rowCounter.setCol(0);
        resultsRow = new ArrayList<>();
        final double sndHeight = HEADER_HEIGHT * 1.5;
        displayHeader(resultsRow, msg.get("data.year"),
                1, sndHeight);
        displayHeader(resultsRow, msg.format("data.phase", phase),
                1, sndHeight);
        for (final IndicatorResult process : processes) {
            displayHeader(resultsRow, getIndicatorName(
                    process.getIndicatorId()), 1, sndHeight);
            for (final IndicatorResult effect : process.getIndicatorResults()) {
                displayHeader(resultsRow,
                        getIndicatorName(effect.getIndicatorId()), 1,
                        sndHeight);
            }
        }
        for (final IndicatorResult process : processes) {
            for (final IndicatorResult effect : process.getIndicatorResults()) {
                for (final IndicatorResult ind : effect.getIndicatorResults()) {
                    displayHeader(resultsRow, getIndicatorName(
                            effect.getIndicatorId()),
                            getIndicatorName(ind.getIndicatorId()),
                            sndHeight);
                }
            }
        }
        resultsArray.add(resultsRow);
        /* Data for indicators */
        for (final Integer year : years) {
            rowCounter.nextRow();
            rowCounter.setCol(0);
            resultsRow = new ArrayList<>();
            final EvaluationResult evaluationResult = results.get(year);
            displayCell(resultsRow, year);
            final Optional<PhaseResult> optional2 = evaluationResult
                    .getPhaseResults()
                    .stream()
                    .filter(phaseR -> phaseR.getEncodedPhaseId().equals(phase))
                    .findFirst();

            if (!optional2.isPresent()) {
                LOGGER.warn("No results for phase {} in year {}", phase,
                        year);
                continue;
            }
            displayCell(resultsRow, optional2.get().getNormalizedValue(), true);
            for (final IndicatorResult process
                    : optional2.get().getIndicatorResults()) {
                displayCell(resultsRow, process.getNormalizedValue(), true);
                for (final IndicatorResult effect
                        : process.getIndicatorResults()) {
                    displayCell(resultsRow, effect.getNormalizedValue(), true);
                }
            }
            for (final IndicatorResult process
                    : optional2.get().getIndicatorResults()) {
                for (final IndicatorResult effect
                        : process.getIndicatorResults()) {
                    for (final IndicatorResult ind
                            : effect.getIndicatorResults()) {
                        displayCell(resultsRow, ind.getNormalizedValue(), true);
                        displayCell(resultsRow, ind.getRawValue());
                        if (newNbCols < rowCounter.getCol() + 1) {
                            newNbCols = rowCounter.getCol() + 1;
                        }
                    }
                }
            }
            resultsArray.add(resultsRow);
        }
        return newNbCols;
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("close");
        final MainView mainView = GetariApp.getMainView();
        ComponentUtil.closeTab(mainView.getCurrentTab());
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCopyAction(final ActionEvent event) {
        LOGGER.traceEntry();
        logAppend("copyToClipboard");
        final StringBuilder sb = new StringBuilder();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        resultsArray.forEach(cells -> {
            cells.forEach(cell -> sb.append(cell).append(CSV_SEPARATOR));
            sb.append("\n");
        });
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onSaveasAction(final ActionEvent event) {
        logAppend("saveas");
        final GetariFileChooser chooser = new GetariFileChooser(FileType.RESULTS);

        final File file = chooser.showSaveDialog(getStage());
        if (file != null) {
            GetariApp.logAppend(getClass(), getStage(), "saveas",
                    file.getAbsolutePath());
            final String ext = chooser.selectedExtensionFilterProperty()
                    .get().getExtensions().get(0).substring(2);
            switch (ext) {
            case "csv":
                logAppend("saveascsv");
                final Evaluation eval = GetariApp.get().getCurrentEval();
                final CsvWriter writer = new CsvWriter();
                writer.setMsg(msg);
                writer.write(eval.getResults(), file.getAbsolutePath());
                writer.writeMetadata(eval);
                break;
            case "out":
                save(file.getAbsolutePath());
                break;
            default:
                throw new RuntimeException("Extension not handled: " + ext);
            }
            final MainView mainView = GetariApp.getMainView();
            mainView.getCurrentTab().setText(
                    mainView.getCurrentTab().getText().substring(2));
        }
    }

    /**
     * Write results into UTF-8 file.
     *
     * @param fileName result file name
     */
    private void save(final String fileName) {
        String theFileName;

        if (!fileName.endsWith("out")) {
            theFileName = fileName + ".out";
        } else {
            theFileName = fileName;
        }

        try (BufferedWriter myOutputWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(theFileName),
                        StandardCharsets.UTF_8));
                ) {
            for (final List<String> row : resultsArray) {
                myOutputWriter.write(String.join(CSV_SEPARATOR, row));
                myOutputWriter.newLine();
            }
        } catch (final IOException e) {
            LOGGER.fatal(e);
        }
    }

}

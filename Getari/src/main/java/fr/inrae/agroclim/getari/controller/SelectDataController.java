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

import static fr.inrae.agroclim.getari.util.GetariConstants.DEFAULT_CURSOR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import fr.inrae.agroclim.getari.component.EvaluationTextField;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariConstants;
import fr.inrae.agroclim.getari.util.OneEmptyBinding;
import fr.inrae.agroclim.indicators.model.TimeScale;
import fr.inrae.agroclim.indicators.model.data.climate.ClimaticDailyData;
import fr.inrae.agroclim.indicators.model.data.phenology.AnnualStageData;
import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Controller for SelectDataView.
 *
 * Last change $Date$
 *
 * @author Olivier Maury <Olivier.Maury@inrae.fr>
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class SelectDataController extends AbstractController implements Initializable {

    /**
     * Preferred column height.
     */
    private static final int COL_HEIGHT = 30;

    /**
     * Preferred column width.
     */
    private static final int COL_WIDTH = 60;

    /**
     * Properties of emptyness for each TextField (above column for phenological
     * files).
     */
    private final ObservableList<BooleanProperty> areEmpty = FXCollections.observableList(new ArrayList<>());

    /**
     * Columns from file.
     */
    private final List<String> columns = new ArrayList<>();

    /**
     * File to display.
     */
    @Setter
    private File file;

    /**
     * RadioButton group for the separators.
     */
    @FXML
    private ToggleGroup group;

    /**
     * Import button.
     */
    @FXML
    private Button importButton;

    /**
     * Property to check that more than 1 column from file exist.
     */
    private final SimpleBooleanProperty isLessThanOneColumn = new SimpleBooleanProperty(false);

    /**
     * Property to check that no required column is missing.
     */
    private final SimpleBooleanProperty isMissing = new SimpleBooleanProperty();

    /**
     * Binding on areEmpty.
     */
    private final OneEmptyBinding isOneEmpty = new OneEmptyBinding(areEmpty);

    /**
     * Text strings from .properties file.
     */
    private I18n i18n;

    /**
     * Box for midnight option.
     */
    @FXML
    private HBox midnightBox;

    /**
     * RadioButton group for the midnight option.
     */
    @FXML
    private ToggleGroup midnightGroup;

    /**
     * Horizontal pane for the selected columns.
     */
    @FXML
    private HBox targetPane;

    /**
     * When data selection is OK, return separator + headers and the hour of midnight for climatic daily data.
     */
    @Setter
    private BiConsumer<List<String>, Integer> okCallback;

    /**
     * Pane with required columns.
     */
    @FXML
    private TilePane sourcePane;

    /**
     * Preview of file.
     */
    @FXML
    private TableView<ObservableList<String>> tableView;

    /**
     * Timescale of current evaluation.
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter
    @NonNull
    private TimeScale timeScale;

    /**
     * File type (CLIMATIC, PHENOLOGICAL).
     */
    @Setter
    private FileType type;

    /**
     * Add context menu to column header label.
     *
     * @param label column header label
     * @param target node to show the context menu
     */
    private void buildContextMenu(final Label label, final Control target) {
        final EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
            private ContextMenu contextMenu = new ContextMenu();

            @Override
            public void handle(final MouseEvent event) {
                contextMenu.hide();
                contextMenu = new ContextMenu();
                if (!event.getButton().toString().equals("SECONDARY")) {
                    return;
                }
                contextMenu
                .styleProperty()
                .bind(Bindings
                        .when(contextMenu
                                .showingProperty())
                        .then(new SimpleStringProperty(
                                GetariConstants.HAND_CURSOR))
                        .otherwise(
                                new SimpleStringProperty(
                                        DEFAULT_CURSOR)));

                final MenuItem clearItem = new MenuItem(
                        Messages.getString("action.clear"));
                clearItem.setOnAction(e -> {
                    final String lc = label.getText().toLowerCase();
                    logAppend("clear", lc);
                    if (type.equals(FileType.CLIMATE)) {
                        isMissing.set(
                                ClimaticDailyData.getRequiredColumnNames(getTimeScale())
                                .contains(lc));
                    } else {
                        isMissing.set(AnnualStageData.REQUIRED.contains(lc));
                    }

                    label.setText("?");
                    label.getStyleClass().add("inactiv-column");
                });

                contextMenu.getItems().add(clearItem);
                contextMenu.show(target,
                        event.getScreenX(),
                        event.getScreenY());
            }
        };
        label.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
    }

    /**
     * Build the pane with required columns.
     */
    private void buildSourcePane() {
        List<String> varColumns;

        switch (type) {
        case CLIMATE:
            varColumns = ClimaticDailyData.getAllColumnNames(getTimeScale());
            break;
        case PHENOLOGY:
            varColumns = AnnualStageData.VAR_NAMES;
            break;
        default:
            throw new IllegalArgumentException(type + " not handled!");
        }

        varColumns.forEach(column -> {
            final Label source = new Label(column);
            source.setPrefSize(COL_WIDTH, COL_HEIGHT);
            source.setAlignment(Pos.CENTER);
            source.getStyleClass().add("climatic-column");
            final Tooltip sourcetoolTip = new Tooltip(source.getText());
            Tooltip.install(source, sourcetoolTip);

            source.setOnDragDetected((final MouseEvent event) -> {
                /* the drag-and-drop gesture ended */
                /* drag was detected, start a drag-and-drop gesture */
                /* allow any transfer mode */
                final Dragboard db = source
                        .startDragAndDrop(TransferMode.COPY_OR_MOVE);

                /* Put a string on a dragboard */
                final ClipboardContent content = new ClipboardContent();
                content.putString(source.getText());
                db.setContent(content);

                event.consume();
            });

            source.setOnDragDone((final DragEvent event) -> {
                /* the drag-and-drop gesture ended */
                logAppend("dragDone");
                event.consume();
            });

            ComponentUtil.setCursorHoverStyleProperty(source);

            sourcePane.getChildren().add(source);
        });
    }

    /**
     * Build the pane with chosen columns.
     */
    private void buildTargets() {
        boolean isFirstColumn = true;
        isMissing.setValue(false);

        final List<String> labels1 = columns.stream()
                .map(String::toLowerCase).collect(Collectors.toList());
        checkMissing(labels1);

        targetPane.getChildren().clear();
        final double colWidth = targetPane.getWidth() / columns.size();
        for (final String column : columns) {
            final String lcColumn = column.toLowerCase();
            final Control target;
            boolean isLabel = true;

            if (type.equals(FileType.CLIMATE)) {
                if (ClimaticDailyData.getAllColumnNames(getTimeScale()).contains(lcColumn)) {
                    target = new Label(lcColumn);
                } else {
                    target = new Label("?");

                }
            } else {
                if (AnnualStageData.VAR_NAMES.contains(lcColumn)) {
                    target = new Label(lcColumn);
                    isFirstColumn = false;
                } else if (isFirstColumn) {
                    target = new Label("?");
                    isFirstColumn = false;
                } else {
                    target = new EvaluationTextField(lcColumn) {
                        @Override
                        public void replaceSelection(final String text) {
                            if (text.matches("[a-z]*[0-9]*")) {
                                super.replaceSelection(text);
                            }
                        }
                        @Override
                        public void replaceText(final int start, final int end,
                                final String text) {
                            // If the replaced text would end up being
                            // invalid, then simply
                            // ignore this call!
                            if (text.matches("[a-z]*[0-9]*")) {
                                super.replaceText(start, end, text);
                            }
                        }
                    };

                    final SimpleBooleanProperty isEmpty
                    = new SimpleBooleanProperty();
                    ((TextInputControl) target).textProperty()
                    .addListener(
                            (final ObservableValue<? extends String> o,
                                    final String oldValue,
                                    final String newValue) -> {
                                        isEmpty.setValue(
                                                newValue.isEmpty());
                                        ((EvaluationTextField) target)
                                        .setValid(!isEmpty.getValue());
                                    });
                    areEmpty.add(isEmpty);
                    isLabel = false;
                }
            }

            target.setPrefSize(colWidth - 1, COL_HEIGHT);

            if (isLabel) {
                final Label label = (Label) target;
                label.setAlignment(Pos.CENTER);
                if (label.getText().equals("?")) {
                    label.getStyleClass().add("inactiv-column");
                } else {
                    label.getStyleClass().add("activ-column");
                }
                target.setOnDragOver((final DragEvent event) -> {
                    /* data is dragged over the target.
                     * accept it only if it is not dragged from the same node
                     * and if it has a string data. */
                    if (event.getGestureSource() != target
                            && event.getDragboard().hasString()) {
                        /* allow for both copying and moving, whatever user
                         * chooses */
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                });

                target.setOnDragEntered((final DragEvent event) -> {
                    /* the drag-and-drop gesture entered the target */
                    /* show to the user that it is an actual gesture target */
                    if (event.getGestureSource() != target
                            && event.getDragboard().hasString()) {
                        label.getStyleClass().add("column-choosen");
                    }

                    event.consume();
                });
                // mouse moved away, remove the graphical cues
                target.setOnDragExited(Event::consume);

                target.setOnDragDropped((final DragEvent event) -> {
                    /* data dropped.
                     * if there is a string data on dragboard, read it and use
                     * it. */
                    logAppend("dropped");
                    final Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        label.setText(db.getString());

                        final List<String> labels = new ArrayList<>();

                        targetPane.getChildren().forEach(control -> {
                            String labelText;
                            if (control instanceof Label) {
                                labelText = ((Labeled) control).getText();
                            } else {
                                labelText = ((TextInputControl) control)
                                        .getText();
                            }
                            labels.add(labelText.toLowerCase());
                        });

                        checkMissing(labels);
                        detectMidnight(labels);

                        label.getStyleClass().clear();
                        label.getStyleClass().add("activ-column");
                        success = true;
                    }
                    /* let the source know whether the string was successfully
                     * transferred and used. */
                    event.setDropCompleted(success);

                    event.consume();
                });
                buildContextMenu(label, target);
            }
            targetPane.getChildren().add(target);
        }
    }

    /**
     * @param labels lowercase columns names to check
     */
    private void checkMissing(final List<String> labels) {
        final List<String> requiredColumns;
        switch (type) {
        case CLIMATE:
            requiredColumns = ClimaticDailyData.getRequiredColumnNames(getTimeScale());
            break;
        case PHENOLOGY:
            requiredColumns = AnnualStageData.REQUIRED;
            break;
        default:
            throw new IllegalArgumentException("Type not handled: " + type);
        }
        for (final String required : requiredColumns) {
            if (!labels.contains(required)) {
                isMissing.set(true);
                break;
            } else {
                isMissing.set(false);
            }
        }
    }

    /**
     * @param headers file header
     * @return false if header contains "-"
     */
    private boolean checkPhenoHeaders(final List<String> headers) {
        if (!FileType.PHENOLOGY.equals(type)) {
            return true;
        }
        final List<String> invalidHeaders = headers.stream().filter(s -> s.contains("-")).collect(Collectors.toList());
        if (invalidHeaders.isEmpty()) {
            return true;
        }
        final String text = i18n.format(invalidHeaders.size(), "invalid.header", String.join(", ", invalidHeaders))
                + i18n.get("invalid.header.tip");
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(i18n.get("selectData.view.title"));
        alert.setContentText(text);
        alert.show();
        return false;
    }

    /**
     * Detect the midnight option from displayed data.
     *
     * @param labels dropped columns
     */
    private void detectMidnight(final List<String> labels) {
        if (!FileType.CLIMATE.equals(type) || TimeScale.HOURLY != timeScale) {
            return;
        }
        final int index = labels.indexOf("hour");
        if (index == -1) {
            return;
        }
        final boolean isMidnight0 = tableView.getItems().get(0).get(index).equals("0");
        midnightGroup.getToggles().forEach(t -> {
            if (isMidnight0) {
                t.setSelected(t.getUserData().toString().equals("0"));
            } else {
                t.setSelected(t.getUserData().toString().equals("24"));
            }
        });
    }

    /**
     * @param separators available separator
     * @param header the header line
     * @return detected separator or null
     */
    private String detectSeparator(final List<String> separators, final String header) {
        for (final String sep : separators) {
            if (header.split(sep).length > 1) {
                return sep;
            }
        }
        return null;
    }

    /**
     * Read file to display first lines into tableView.
     *
     * @param f text file to read
     * @param separator separator to use, or null to guess one
     */
    private void getContent(@NonNull final File f, final String separator) {
        LOGGER.traceEntry();
        columns.clear();
        final ObservableList<ObservableList<String>> datas = FXCollections.observableArrayList();
        tableView.setItems(datas);
        // 1. read lines
        final List<String> lines = new ArrayList<>();
        String header = null;
        try (BufferedReader b = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {
            final int nbRows = 5;
            for (int i = 0; i < nbRows; i++) {
                if (i == 0) {
                    header = b.readLine();
                } else {
                    lines.add(b.readLine());
                }
            }
        } catch (final IOException e) {
            LOGGER.error(e);
        }
        // 2. detect separator from first line
        Objects.requireNonNull(header);
        final String currentSeparator;
        if (separator == null) {
            final List<String> separators = group.getToggles().stream()
                    .map(t -> t.getUserData().toString()).collect(Collectors.toList());
            currentSeparator = detectSeparator(separators, header);
            group.getToggles().forEach(t -> {
                if (t.getUserData().toString().equals(currentSeparator)) {
                    t.setSelected(true);
                }
            });
        } else {
            currentSeparator = separator;
        }
        Objects.requireNonNull(currentSeparator);
        // 3. parse lines
        boolean hasTrailingSep = false;
        final List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(header.split(currentSeparator)));
        // add column for trailing separators
        if (header.endsWith(currentSeparator)) {
            hasTrailingSep = true;
        }
        if (hasTrailingSep) {
            headers.add("");
        }
        // 4. check if "-" is in a column title
        if (!checkPhenoHeaders(headers)) {
            getStage().close();
            return;
        }
        // 5. display table
        columns.addAll(headers);
        tableView.setVisible(true);
        for (final String line: lines) {
            if (line == null) {
                continue;
            }
            final ObservableList<String> row = FXCollections.observableArrayList();
            final String[] content = line.split(currentSeparator);
            row.addAll(Arrays.asList(content));
            // fill with blank
            while (row.size() < headers.size()) {
                row.add("");
            }
            datas.add(row);
        }
        setTableViewColumns(headers, tableView);
        tableView.setItems(datas);
    }

    /**
     * @return a handle to the stage.
     */
    @Override
    protected Stage getStage() {
        return (Stage) targetPane.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        i18n = new I18n(rb);
    }

    /**
     * Build view according to EvaluationSettings and type.
     */
    public void initializeView() {
        getContent(file, null);
        group.selectedToggleProperty().addListener(
                (final ObservableValue<? extends Toggle> ov, final Toggle oldToggle,
                        final Toggle newToggle) -> {
                            if (group.getSelectedToggle() != null) {
                                final String separator = group.getSelectedToggle()
                                        .getUserData().toString();
                                targetPane.getChildren().clear();
                                getContent(file, separator);
                                buildTargets();
                            }
                        });

        buildSourcePane();
        buildTargets();
        midnightBox.setVisible(timeScale == TimeScale.HOURLY);

        importButton.disableProperty().bind(
                isLessThanOneColumn.or(isMissing).or(isOneEmpty));

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
    private void onImportAction(final ActionEvent event) {
        LOGGER.trace("action.import clicked!");
        logAppend("import");
        final List<String> res = new ArrayList<>();
        final String separator = group.getSelectedToggle()
                .getUserData().toString();
        res.add(separator);

        for (final Node target : targetPane.getChildren()) {
            if (target == null) {
                throw new IllegalStateException("this should never occur!");
            }
            String text;
            if (target instanceof Label) {
                text = ((Labeled) target).getText();
            } else {
                text = ((TextInputControl) target).getText();
            }
            if (text.equals("?")) {
                res.add("");
            } else {
                res.add(text);
            }
        }
        close();
        if (okCallback != null) {
            Integer firstHour = null;
            if (timeScale == TimeScale.HOURLY) {
                firstHour = Integer.valueOf(midnightGroup.getSelectedToggle().getUserData().toString());
            }
            okCallback.accept(res, firstHour);
        }
    }

    /**
     * Define column headers.
     *
     * @param vars column headers
     * @param view table view
     */
    private void setTableViewColumns(final List<String> vars,
            final TableView<ObservableList<String>> view) {
        view.getColumns().clear();
        // auto resize columns
        final int margin = 7;
        final DoubleBinding colWidth = targetPane.widthProperty()
                .subtract(targetPane.getPadding().getLeft())
                .subtract(targetPane.getPadding().getRight())
                .subtract(margin)
                .divide(vars.size());

        for (int i = 0; i < vars.size(); i++) {
            final int j = i;
            final TableColumn<ObservableList<String>, String> col = new TableColumn<>(vars.get(i));
            col.setResizable(false);
            col.setPrefWidth(COL_WIDTH);
            col.setReorderable(false);
            col.setSortable(false);
            col.setCellValueFactory((
                    final CellDataFeatures<ObservableList<String>, String> param)
                    -> new SimpleStringProperty(param.getValue().get(j)));
            col.setEditable(false);
            col.prefWidthProperty().bind(colWidth);
            view.getColumns().add(col);
        }

        isLessThanOneColumn.set(view.getColumns().size() <= 1);
    }
}

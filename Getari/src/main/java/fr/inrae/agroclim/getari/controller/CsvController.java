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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.view.MainView;
import fr.inrae.agroclim.indicators.model.data.FileLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Display values from CSV file.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class CsvController extends AbstractController implements Initializable {

    /**
     * Close button.
     */
    @FXML
    private Button close;

    /**
     * Table widget.
     */
    @FXML
    private TableView<List<StringProperty>> tableView;

    @Override
    protected Stage getStage() {
        return (Stage) close.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // do nothing
    }

    /**
     * @param file CSV file to load into tableView
     */
    public void loadFile(final File file) {
        logAppend("load", file.getAbsolutePath());
        try {
            final String[] headers = FileLoader.getHeaders(file, ',');
            int i = 0;
            for (final String col : headers) {
                TableColumn<List<StringProperty>, String> column;
                column = new TableColumn<>(col);
                final int j = i;
                column.setCellValueFactory(data -> {
                    if (data.getValue().size() <= j) {
                        return null;
                    }
                    return data.getValue().get(j);
                });
                tableView.getColumns().add(column);
                i++;
            }
            // auto resize columns
            for (final TableColumn<List<StringProperty>, ?> column: tableView.getColumns()) {
                column.prefWidthProperty().bind(tableView.widthProperty().divide(i));
            }
            //
            final ObservableList<List<StringProperty>> data;
            data = FXCollections.observableArrayList();
            final CsvSchema schema = CsvSchema.emptySchema()
                    .withSkipFirstDataRow(true)//
                    .withColumnSeparator(',');
            final CsvMapper mapper = new CsvMapper();
            // important: we need "array wrapping" (see next section) here:
            mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
            final ObjectReader objReader = mapper.readerFor(String[].class)
                    .with(schema);
            final MappingIterator<String[]> it = objReader.readValues(file);
            while (it.hasNext()) {
                final List<String> values = Arrays.asList(it.next());
                data.add(values.stream()
                        .map(SimpleStringProperty::new)
                        .collect(Collectors.toList()));
            }
            tableView.setItems(data);

        } catch (final FileNotFoundException ex) {
            LOGGER.error("Strange, file {} not found!", file);
        } catch (final IOException ex) {
            LOGGER.error("Error while reading {}", file);
        }
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

}

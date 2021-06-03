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
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.GetariPreferences;
import fr.inrae.agroclim.getari.util.FileType;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Display first dialog to tell user what to do.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class StartController extends AbstractController implements Initializable {

    /**
     * List of recently opened evaluations.
     */
    @FXML
    private ListView<String> list;

    /**
     * Controller the controller for MainView has the logic.
     */
    @Setter
    private MainViewController mainViewController;

    /**
     * The button with embedded menu to open MultiExecution.
     */
    @FXML
    private SplitMenuButton meButton;

    @Override
    protected Stage getStage() {
        return (Stage) list.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        final GetariPreferences prefs = GetariApp.get().getPreferences();
        final List<String> paths = prefs.getExistingRecentlyOpened(FileType.EVALUATION);
        final ObservableList<String> value = FXCollections.observableArrayList(paths);
        list.setItems(value);
        list.getSelectionModel().selectedItemProperty().addListener(
                (final ObservableValue<? extends String> ov, final String oldValue, final String newValue) -> {
                    final File file = new File(newValue);
                    mainViewController.openEvaluationFile(file, () -> {
                        LOGGER.traceEntry();
                        logAppend("open from recent", file.getAbsolutePath());
                        close();
                        LOGGER.traceExit();
                    });
                });
        final List<String> mePaths = prefs.getExistingRecentlyOpened(FileType.MULTIEXECUTION);
        mePaths.forEach(path -> {
            final MenuItem item = new MenuItem(path);
            item.setOnAction(eh -> {
                mainViewController.openMEFile(new File(path));
                close();
            });
            meButton.getItems().add(item);
        });
    }

    /**
     * @param event not used
     */
    @FXML
    private void onNewAction(final ActionEvent event) {
        logAppend("new evaluation");
        mainViewController.onNewEvaluationAction();
    }

    /**
     * @param event not used
     */
    @FXML
    private void onNewMultiExecutionAction(final ActionEvent event) {
        logAppend("new multiexecution");
        mainViewController.onNewME();
        getStage().close();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onOpenAction(final ActionEvent event) {
        logAppend("open evaluation");
        mainViewController.showOpenFileDialog(FileType.EVALUATION);
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onOpenMultiExecutionAction(final ActionEvent event) {
        logAppend("open multiexecution");
        mainViewController.showOpenFileDialog(FileType.MULTIEXECUTION);
    }
}

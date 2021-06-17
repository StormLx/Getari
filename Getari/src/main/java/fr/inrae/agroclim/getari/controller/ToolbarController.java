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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.memento.History;
import fr.inrae.agroclim.getari.memento.HistorySingleton;
import fr.inrae.agroclim.getari.memento.Memento;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Controller for partial view Toolbar used in GraphView.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class ToolbarController extends AbstractController implements Initializable {

    /**
     * Run on action on Clear button.
     */
    @Setter
    private Runnable clearCmd;
    /**
     * Run on action on Close button.
     */
    @Setter
    private Runnable closeCmd;
    /**
     * Evaluate button.
     */
    @FXML
    private Button evaluateBtn;
    /**
     * Undo button.
     */
    @FXML
    private SplitMenuButton undoBtn;
    /**
     * Redo button.
     */
    @FXML
    private SplitMenuButton redoBtn;
    /**
     * Run on action on Evaluate button.
     */
    @Setter
    private Runnable evaluateCmd;
    /**
     * Run on action on New phase button.
     */
    @Setter
    private Runnable newCmd;
    /**
     * Run reloadbis on graph.
     */
    @Setter
    private Runnable reloadBISCmd;
    /**
     * Combo listing the phases.
     */
    @FXML
    @Getter
    private ComboBox<Indicator> phaseCombo;
    /**
     * Save button.
     */
    @FXML
    @Getter
    private Button saveBtn;

    /**
     * Display btn.
     */
    @FXML
    @Getter
    private Button displayBtn;

    /**
     * Root node.
     */
    @FXML
    private HBox root;

    /**
     * History of actions.
     */
    @Getter
    private History history = HistorySingleton.INSTANCE.getHistory();
    /**
     * undo list.
     */
    private List<MenuItem> undoList = new ArrayList<>();
    /**
     * Observable redo list.
     */
    private List<MenuItem> redoList = new ArrayList<>();

    /**
     * @return the "disable" property of evaluate button.
     */
    public final BooleanProperty getEvaluateBtnDisableProperty() {
        return evaluateBtn.disableProperty();
    }

    @Override
    protected final Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    /**
     * init.
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {

        Tooltip undoTooltip = new Tooltip("Annuler la dernière opération.");
        undoBtn.setTooltip(undoTooltip);

        Tooltip redoTooltip = new Tooltip("Récupérer la dernière opération.");
        redoBtn.setTooltip(redoTooltip);
        undoBtn.setDisable(true);
        redoBtn.setDisable(true);

        undoBtn.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (newValue) {
                undoBtn.show();
                redoBtn.hide();

            }
        });
        redoBtn.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (newValue) {
                redoBtn.show();
                undoBtn.hide();
            }
        });

    }


    /**
     * @param event onAction of button
     */

    @FXML
    private void onDisplayAction(final ActionEvent event) {
        for (Memento undo : history.getUndoHistory()) {
            LOGGER.trace("From UNDO LIST: Eval-> {} , Memento ID-> {}", undo.getEvaluation(), undo.getId());
        }
        for (Memento redo : history.getRedoHistory()) {
            LOGGER.trace("From REDO LIST: Eval-> {} , Memento ID-> {}", redo.getEvaluation(), redo.getId());
        }

    }

    /**
     * @param event onAction of button
     */

    @FXML
    private void onUndoAction(final ActionEvent event) {
        history.undo();
        reloadBISCmd.run();
    }

    /**
     * @param event onAction of button
     */

    @FXML
    private void onRedoAction(final ActionEvent event) {
        history.redo();
        reloadBISCmd.run();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onClearAction(final ActionEvent event) {
        logAppend("clear evaluation");
        clearCmd.run();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("close evaluation");
        closeCmd.run();

    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onEvaluateAction(final ActionEvent event) {
        LOGGER.traceEntry();
        logAppend("run evaluation");
        evaluateCmd.run();
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onNewAction(final ActionEvent event) {
        logAppend("new phase");
        newCmd.run();
        final Evaluation e = GetariApp.get().getCurrentEval().clone();

        int newID = history.getMemento().getId() + 1;
        Memento m = new Memento(e, Messages.getString("history.add.evaluation", "Ajout Phase"), newID);
        history.addMemento(m);
    }

    /**
     * History.
     *
     */
    public void setListener() {
        history.addChangeListenerUndo(change -> {
            while (change.next()) {

                int currentMementoID = history.getMemento().getId();

                if (change.wasAdded() && history.getSize() > 1 && redoList.isEmpty()) {
                    LOGGER.trace("inside undo added");
                    MenuItem item = new MenuItem(history.getUndoMemento().getState());

                    item.setOnAction((e) -> {
                        history.undoById(currentMementoID);
                        reloadBISCmd.run();
                    });
                    undoList.add(0, item);
                    undoBtn.getItems().clear();
                    undoBtn.getItems().addAll(undoList);
                    redoBtn.getItems().clear();
                    redoBtn.setDisable(true);
                    undoBtn.setDisable(false);

                }
                if (change.wasRemoved()) {

                    int currentRedoMementoID = history.getRedoMemento().getId();
                    LOGGER.trace("inside undo removed listen");

                    history.getRedoMemento().setState(
                            Messages.getString("history.name.restore",
                                    history.getRedoMemento().getEvaluation()));
                    MenuItem item = new MenuItem(history.getRedoMemento().getState());

                    item.setOnAction((e) -> {
                        history.redoById(currentRedoMementoID);
                        reloadBISCmd.run();
                    });
                    redoList.add(0, item);
                    undoList.remove(0);
                    undoBtn.getItems().clear();
                    undoBtn.getItems().addAll(undoList);
                    redoBtn.getItems().clear();
                    redoBtn.getItems().addAll(redoList);
                    redoBtn.setDisable(false);
                    if (undoList.isEmpty()) {
                        undoBtn.setDisable(true);
                    }
                }
            }
        });

        history.addChangeListenerRedo(change -> {
            while (change.next()) {

                int currentMementoID = history.getMemento().getId();
                if (change.wasRemoved() && !redoList.isEmpty()) {
                    LOGGER.trace("from redo listener");
                    history.getMemento().setState(
                            Messages.getString("history.name.change",
                                    history.getMemento().getEvaluation()));
                    MenuItem item = new MenuItem(history.getMemento().getState());
                    item.setOnAction((e) -> {
                        history.undoById(currentMementoID);
                        reloadBISCmd.run();
                    });
                    undoList.add(0, item);
                    redoList.remove(0);
                    if (history.getRedoHistory().isEmpty()) {
                        redoList.clear();
                    }
                    undoBtn.getItems().clear();
                    undoBtn.getItems().addAll(undoList);
                    redoBtn.getItems().clear();
                    redoBtn.getItems().addAll(redoList);
                }
                if (redoList.isEmpty()) {
                    redoBtn.setDisable(true);
                } else if (!undoList.isEmpty()) {
                    undoBtn.setDisable(false);
                }
            }
        });

    }

}

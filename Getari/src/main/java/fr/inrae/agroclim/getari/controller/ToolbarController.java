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

import fr.inrae.agroclim.getari.memento.FakeData;
import fr.inrae.agroclim.getari.memento.FakeSquare;
import fr.inrae.agroclim.getari.memento.History;
import fr.inrae.agroclim.getari.memento.Memento;
import fr.inrae.agroclim.getari.memento.Origin;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.adapter.JavaBeanIntegerProperty;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
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
	 * Root node.
	 */
	@FXML
	private HBox root;

	/**
	 * Current state.
	 */
	private Origin origin = new Origin();
	/**
	 * History of actions.
	 */
	private History history;
	/**
	 * undo list.
	 */
	private List<MenuItem> undoList = new ArrayList<>();
	/**
	 * Observable redo list.
	 */
	private List<MenuItem> redoList = new ArrayList<>();
	/**
	 * bean
	 */
	private JavaBeanIntegerProperty historyPos;

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

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {

		// Undo SplitButton

		Tooltip undoTooltip = new Tooltip("Annuler la dernière opération.");
		undoBtn.setTooltip(undoTooltip);

		// Redo SplitButton

		Tooltip redoTooltip = new Tooltip("Récupérer la dernière opération.");
		redoBtn.setTooltip(redoTooltip); // TODO mettre dans le FXML

	}

	/**
	 * @param event onAction of button
	 */

	@FXML
	private void onUndoAction(final ActionEvent event) {
		LOGGER.trace("Size from undo: {}", history.getSize());
		/*
		 * origin.restore(history.undo()); redoList.add(undoList.get(undoList.size() -
		 * 1)); undoList.remove(undoList.size() - 1); undoBtn.getItems().clear();
		 * undoBtn.getItems().addAll(undoList); redoBtn.getItems().clear();
		 * redoBtn.getItems().addAll(redoList);
		 */
	}

	/**
	 * @param event onAction of button
	 */

	@FXML
	private void onRedoAction(final ActionEvent event) {
		origin.restore(history.redo());
		undoList.add(redoList.get(redoList.size() - 1));
		redoList.remove(redoList.size() - 1);
		undoBtn.getItems().clear();
		undoBtn.getItems().addAll(undoList);
		redoBtn.getItems().clear();
		redoBtn.getItems().addAll(redoList);
	}

	/**
	 * @param event onAction of button
	 */
	/*
	 * @FXML private void onAddObject(final ActionEvent event) {
	 * 
	 * if (history.getCurrentState() < history.getSize() - 1) { for (int i =
	 * history.getCurrentState(); i < history.getSize(); i++) {
	 * history.removeMemento(history.getSize() - 1); } redoList.clear(); } int n =
	 * Integer.parseInt(name); FakeData newData = new FakeData();
	 * newData.setName(name); origin.setFakeData(newData);
	 * history.addMemento(origin.save()); MenuItem item1 = new
	 * MenuItem(newData.getName() + " action"); undoList.add(item1);
	 * myLabel.setText(FakeSquare.squareA(n) + ""); undoBtn.getItems().clear();
	 * undoBtn.getItems().addAll(undoList); redoBtn.getItems().clear(); }
	 */
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
	}

	public void setHistory(History history) {
		this.history = history;
		history.addChangeListener(change -> { // écoute un changement de taille de la liste
			LOGGER.trace();
			change.getAddedSize();
		});
		history.currentStateProperty().addListener((obs, oldValue, newValue) -> {

			if (oldValue.intValue() < newValue.intValue()) {
				LOGGER.trace("New memento object added to the list");
				//TODO menuItem
			} else {
				LOGGER.trace("Last memento object has been removed from the list");
			}
		});
	}
}

package fr.inrae.agroclim.getari.memento;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class History {
	/**
	 * List de memento.
	 */
	private List<Memento> history;
	/**
	 * IntegerProperty to add listener.
	 */
	private final IntegerProperty currentState;

	public History() {
		this.history = new ArrayList<>();
		currentState = new SimpleIntegerProperty();
	}

	public void addMemento(Memento m) {
		this.history.add(m);
		currentState.set(this.history.size() - 1);
	}
	public void removeMemento(int index) {
		this.history.remove(index);
	}

	public Memento getMemento(int index) {
		return history.get(index);
	}

	public Memento undo() {
		LOGGER.trace("Undoing state..");
		if (currentState.get() <= 0) {
			currentState.set(0);
			return getMemento(0);
		}
		currentState.set(currentState.get() - 1);
		return getMemento(currentState.get());
	}

	public Memento redo() {
		LOGGER.trace("Redoing state..");
		if (currentState.get() >= history.size() - 1) {
			currentState.set(history.size() - 1);
			return getMemento(currentState.get());
		}
		currentState.set(currentState.get() + 1);
		return getMemento(currentState.get());
	}

	public final IntegerProperty currentStateProperty() {
		return currentState;
	}

	public final int getCurrentState() {
		return currentState.get();
	}

	public void setCurrentState(int currentState) {
		this.currentState.set(currentState);
	}
	public int getSize() {
		return history.size();
	}

}

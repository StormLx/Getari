package fr.inrae.agroclim.getari.memento;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class History {
    /**
     * List de memento.
     */
    @Getter
    @Setter
    private ObservableList<Memento> undoHistory;
    /**
     * List de memento.
     */
    @Getter
    @Setter
    private ObservableList<Memento> redoHistory;

    public History() {
        this.undoHistory = FXCollections.observableArrayList();
        this.redoHistory = FXCollections.observableArrayList();
    }

    /**
     * add memento.
     *
     * @param m
     */
    public void addMemento(final Memento m) {
        this.undoHistory.add(0, m);
        redoHistory.clear();
    }

    /**
     * Undo method.
     */
    public void undo() {
        if (undoHistory.size() > 1) {
            LOGGER.trace("Undoing state..");
            this.redoHistory.add(0, undoHistory.get(0));
            LOGGER.trace(undoHistory.get(0).getState() + " <- in undo list");
            LOGGER.trace(redoHistory.get(0).getState() + " <- in redo list");
            this.undoHistory.remove(0);
            LOGGER.trace(undoHistory.get(0).getState() + " apres");
        }
    }

    /**
     * Undo method.
     *
     * @param id
     */
    public void undoById(final int id) {
        if (undoHistory.size() > 1) {
            Memento m = findById(id);
            int nLoops = undoHistory.indexOf(m) + 1;
            for (int i = 0; i < nLoops; i++) {
                redoHistory.add(0, undoHistory.get(0));
                undoHistory.remove(0);
            }
        }

    }

    /**
     * Redo method.
     *
     */

    public void redo() {
        if (redoHistory.size() >= 1) {
            LOGGER.trace("Redoing state..");
            undoHistory.add(0, redoHistory.get(0));
            redoHistory.remove(0);
        }
    }

    /**
     * redo method.
     *
     * @param id
     */
    public void redoById(final int id) {
        if (redoHistory.size() >= 1) {
            System.out.println("INSIDE REDOBYID IF STATE");

            Memento m = findRedoById(id);
            System.out.println("eval: " + m.getEvaluation() + "id: " + m.getId());
            int nLoops = redoHistory.indexOf(m) + 1;
            System.out.println("loops: " + nLoops);
            for (int i = 0; i < nLoops; i++) {

                System.out.println("INSIDE REDOBYID LOOP");
                undoHistory.add(0, redoHistory.get(0));
                redoHistory.remove(0);

            }

        }
    }

    /**
     * Listener on list.
     *
     * @param listener
     */
    public void addChangeListenerUndo(final ListChangeListener<Memento> listener) {
        undoHistory.addListener(listener);
    }

    /**
     * Listener on list.
     *
     * @param listener
     */
    public void addChangeListenerRedo(final ListChangeListener<Memento> listener) {
        redoHistory.addListener(listener);
    }

    /**
     * Remove Listener on list.
     *
     * @param listener
     */
    public void removeChangeListenerRedo(final ListChangeListener<Memento> listener) {
        redoHistory.removeListener(listener);
    }

    /**
     * size.
     *
     * @return int size
     */
    public int getSize() {
        return undoHistory.size();
    }

    /**
     * get memento.
     *
     * @return memento
     */
    public Memento getMemento() {
        return undoHistory.get(0);
    }

    /**
     * get memento.
     *
     * @return memento
     */
    public Memento getUndoMemento() {
        return undoHistory.get(0);
    }

    /**
     * get memento.
     *
     * @return memento
     */
    public Memento getRedoMemento() {
        return redoHistory.get(0);
    }

    /**
     * get memento.
     *
     * @param index
     * @return memento
     */
    public Memento getMementoFromIndex(final int index) {
        return undoHistory.get(index);
    }

    /**
     * find memento.
     *
     * @param id
     * @return memento
     */
    public Memento findById(final int id) {
        Memento m = null;
        for (Memento list : undoHistory) {
            if (id == list.getId()) {
                m = list;
                break;
            }
        }
        return m;
    }

    /**
     * find memento.
     *
     * @param id
     * @return memento
     */
    public Memento findRedoById(final int id) {
        Memento m = null;
        for (Memento list : redoHistory) {
            if (id == list.getId()) {
                m = list;
                break;
            }
        }
        return m;
    }

}

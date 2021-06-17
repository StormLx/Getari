package fr.inrae.agroclim.getari.memento;

import fr.inrae.agroclim.indicators.model.Evaluation;
import lombok.Getter;
import lombok.Setter;

public class Memento {
    /**
     * Evaluation.
     */
    @Getter
    private final Evaluation evaluation;
    /**
     * Etat du memento.
     */
    @Getter
    @Setter
    private String state;
    /**
     * ID du memento.
     */
    @Getter
    private final int id;

    public Memento(final Evaluation e, final String s, final int i) {
        super();
        this.evaluation = e;
        this.state = s;
        this.id = i;
    }




}

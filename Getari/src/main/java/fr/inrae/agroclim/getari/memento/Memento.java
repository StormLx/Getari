package fr.inrae.agroclim.getari.memento;

import fr.inrae.agroclim.indicators.model.Evaluation;
import lombok.Getter;
import lombok.Setter;

public class Memento {
@Setter
@Getter

	private Evaluation evaluation;

	public Memento(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

}

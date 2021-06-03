package fr.inrae.agroclim.getari.memento;

import fr.inrae.agroclim.indicators.model.Evaluation;

public class Origin {

	private Evaluation evaluation;

	public Origin() {
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Memento save() {
		return new Memento(evaluation);
	}

	public void restore(Memento m) {
		this.evaluation = m.getEvaluation();
	}

	@Override
	public String toString() {
		return "Origin [fakeData=" + evaluation + "]";
	}

}
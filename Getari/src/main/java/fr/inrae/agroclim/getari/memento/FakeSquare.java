package fr.inrae.agroclim.getari.memento;

public class FakeSquare {
	
	private int a;
	
	public FakeSquare(int a) {
		this.a = a;
	}
	
	public static int squareA(final int a) {
		return a * a;
	}

}

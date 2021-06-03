package fr.inrae.agroclim.getari.memento;

public class FakeData {
	
	private String name;

	
	public FakeData() {}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "FakeData [name = " + name + "]";
	}
}

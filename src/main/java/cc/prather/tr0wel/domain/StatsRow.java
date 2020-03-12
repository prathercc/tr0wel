package cc.prather.tr0wel.domain;

import javafx.beans.property.SimpleStringProperty;

public class StatsRow {
	
	private SimpleStringProperty name;
	private SimpleStringProperty value;
	
	public StatsRow(String name, String value) {
		this.name = new SimpleStringProperty(name);
		this.value = new SimpleStringProperty(value);
	}

	public String getName() {
		return name.get();
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(SimpleStringProperty value) {
		this.value = value;
	}
	
	

}

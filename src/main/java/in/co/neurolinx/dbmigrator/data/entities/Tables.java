package in.co.neurolinx.dbmigrator.data.entities;

public class Tables {
	private String names;
	private String field;

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	
	public String getFields() {
		return field;
	}

	public void setFields(String fields) {
		this.field = fields;
	}

	@Override
	public String toString() {
		return names;
	}

}

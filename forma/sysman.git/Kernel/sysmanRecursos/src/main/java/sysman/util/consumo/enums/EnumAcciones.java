package sysman.util.consumo.enums;

public enum EnumAcciones {
	
	SELECT("S"), 
	INSERT("I"), 
	UPDATE("M"), 
	DELETE("E");

	private final String value;

	private EnumAcciones(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

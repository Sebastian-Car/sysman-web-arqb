package sysman.util.consumo.enums;

public enum EnumAccionAuditoria {
	
	CREAR("CREAR"),
	BORRAR("BORRAR"),
	ACTUALIZAR("ACTUALIZAR");

	private final String key;

	private EnumAccionAuditoria(String key) {
		this.key = key;
	}

	public String getValue() {
		return key;
	}
}
